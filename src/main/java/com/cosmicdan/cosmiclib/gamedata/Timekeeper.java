package com.cosmicdan.cosmiclib.gamedata;

import com.cosmicdan.shadowed.libdivide4j.FastDivision;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("CyclicClassDependency")
@Log4j2(topic = "CosmicLib/Timekeeper")
public final class Timekeeper {
	private static final Timekeeper INSTANCE = new Timekeeper();

	private Timekeeper() {}

	public static Timekeeper getInstance() {
		return INSTANCE;
	}

	private static final double TICKS_PER_DAY = 24000.0;
	private static final FastDivision.Magic TICKS_PER_DAY_MAGIC = FastDivision.magicUnsigned((long) TICKS_PER_DAY);
	private static final double TICKS_PER_HOUR = 1000.0;
	private static final FastDivision.Magic TICKS_PER_HOUR_MAGIC = FastDivision.magicUnsigned((long) TICKS_PER_HOUR);
	private static final int MOONPHASES = 8;
	private static final FastDivision.Magic MOONPHASES_MAGIC = FastDivision.magicUnsigned(MOONPHASES);

	private static final int TICKER_MAX = 10;

	private final ScheduledExecutorService timekeeperScheduleServer = Executors.newScheduledThreadPool(1);
	private final ScheduledExecutorService timekeeperScheduleClient = Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> timekeeperTaskServer = null;
	private ScheduledFuture<?> timekeeperTaskClient = null;
	private int ticker = 0;
	private boolean syncDone = false;


	// set by main thread; gotten from timekeeper thread
	@Getter
	private volatile long worldTimeCached = -1L;

	// These are all set by timekeeper thread and gotten (and get'd) via main thread
	/** Return current day number for the world. Zero-based. */
	@Getter private volatile int worldDayCount;
	/** Return current moon phase number for the world. Might not be much faster than the vanilla getMoonPhase, but ensures synchronization. */
	@Getter private volatile int worldMoonPhase;
	/** Get the number of ticks that have passed on this day. */
	@Getter private volatile int dayTicksElapsed;
	/** Get current in-game hour of the day. Zero-based. Use {@link #calcHourOfDay()} to get a clock-adjusted hour. */
	@Getter private volatile int hourInDay; // 0 == 6am
	/** Get the number of ticks that have passed since the last whole hour. Use {@link #getMinutesSinceHourElapsed()} for minutes-converted value. */
	@Getter private volatile int ticksSinceHourElapsed;
	/** Get the number of in-game minutes that have passed since the last whole hour. */
	@Getter private volatile int minutesSinceHourElapsed;

	/**
	 * Calculate a clock-adjusted hour of day.
	 * e.g. midnight returns 0, sunrise (actual start of a Minecraft day) returns 6.
	 */
	public int calcHourOfDay() {
		return (hourInDay + 6) - ((17 < hourInDay) ? 24 : 0);
	}

	/**
	 * Get a nicely-formatted time String, in format of e.g. 10:30pm
	 */
	public String getNiceTimeString(final boolean militaryTime) {
		final String minutesPadded = String.format("%02d", this.minutesSinceHourElapsed);
		int hourOfDay = calcHourOfDay();
		String suffix = "";
		if (!militaryTime) {
			if (12 <= hourOfDay) {
				suffix = "pm";
				hourOfDay -= 12;
			} else {
				suffix = "am";
			}
			if (0 == hourOfDay)
				hourOfDay = 12;
		}
		return hourOfDay + ":" + minutesPadded + suffix;
	}

	@SubscribeEvent
	public void onServerTick(final TickEvent.WorldTickEvent event) {
		if ((null != event.world) && (TickEvent.Phase.END == event.phase)) {
			tick(event.world.getWorldTime());
		}
	}

	@SubscribeEvent
	public void onClientTick(final TickEvent.ClientTickEvent event) {
		// for clients (both SSP and SMP since we want to keep a client-side timekeeper too)
		if ((null != Minecraft.getMinecraft().world) && (TickEvent.Phase.END == event.phase)) {
			tick(Minecraft.getMinecraft().world.getWorldTime());
		}
	}

	@SubscribeEvent
	public void onWorldLoad(final WorldEvent.Load event) {
		if (0 == event.getWorld().provider.getDimension()) {
			final boolean isRemote = event.getWorld().isRemote;
			final ScheduledExecutorService timekeeperSchedule = isRemote ? timekeeperScheduleClient : timekeeperScheduleServer;
			final ScheduledFuture<?> task = timekeeperSchedule.scheduleAtFixedRate(new TimekeeperSchedule(), 0, 10, TimeUnit.MILLISECONDS);
			if (isRemote)
				timekeeperTaskClient = task;
			else
				timekeeperTaskServer = task;
		}
	}

	@SubscribeEvent
	public void onWorldUnload(final WorldEvent.Unload event) {
		if (0 == event.getWorld().provider.getDimension()) {
			if (event.getWorld().isRemote) {
				if (null != timekeeperTaskClient)
					timekeeperTaskClient.cancel(true);
			} else {
				if (null != timekeeperTaskServer)
					timekeeperTaskServer.cancel(true);
			}
		}
		reset();
	}

	private void reset() {
		ticker = 0;
		syncDone = false;
		worldTimeCached = -1L;
	}

	private void tick(final long worldTime) {
		if (worldTime == this.worldTimeCached)
			// Only tick once per... well, tick. This is mostly for integrated server (SSP)
			return;

		ticker++;
		//log.info("Ticker for worldtime " + worldTime + " is at " + ticker);
		if (!syncDone) {
			if (0 == (worldTime % TICKER_MAX)) {
				log.info("Timekeeper synchronised to worldtime on tick-rate quotient");
				ticker = TICKER_MAX;
				syncDone = true;
			}
		}
		if (TICKER_MAX == ticker) {
			ticker = 0;
			this.worldTimeCached = worldTime;
		}
	}

	@SuppressWarnings("NonStaticInnerClassInSecureContext")
	private final class TimekeeperSchedule implements Runnable {
		private long worldTimeLast = 0L;
		private int worldDayCountCalc = 0;
		private int worldDayCountCalcLast = 0;
		private long worldMoonPhaseInterimCalc = 0;
		private long worldMoonPhaseInterimCalcLast = 0;
		private int worldMoonPhaseCalc = 0;
		private int worldMoonPhaseCalcLast = 0;
		private int dayTicksElapsedCalc = 0;
		private int dayTicksElapsedCalcLast = 0;
		private int dayHoursRawElapsedCalc = 0;
		private int dayHoursRawElapsedCalcLast = 0;
		private int dayTicksPastHourElapsedCalc = 0;
		private int dayTicksPastHourElapsedCalcLast = 0;
		private int dayMinutesPastHourElapsedCalc = 0;
		private int dayMinutesPastHourElapsedCalcLast = 0;

		@SuppressWarnings({"MethodWithMoreThanThreeNegations", "NumericCastThatLosesPrecision"})
		@Override
		public void run() {
			if (Timekeeper.this.worldTimeCached == worldTimeLast)
				return;

			// main thread has updated world time, let's set new values if needed
			worldTimeLast = Timekeeper.this.worldTimeCached;

			if (0 <= worldTimeLast) {
				//log.info(Timekeeper.this.worldTimeCached + " is new, let's do updates!");

				// worldDayCount
				//worldDayCountCalc = (int) (worldTimeLast / TICKS_PER_DAY);
				worldDayCountCalc = (int) FastDivision.divideUnsignedFast(worldTimeLast, TICKS_PER_DAY_MAGIC);
				if (worldDayCountCalc != worldDayCountCalcLast) {
					worldDayCountCalcLast = worldDayCountCalc;
					Timekeeper.this.worldDayCount = worldDayCountCalcLast;
				}

				//worldMoonPhaseCalc = (int) ((((worldTimeLast / 24000L) % 8L) + 8L) % 8);
				//worldMoonPhaseCalc = (int) (((worldDayCountCalc % 8L) + 8L) % 8);

				worldMoonPhaseInterimCalc = FastDivision.remainderUnsignedFast(worldDayCountCalc, MOONPHASES_MAGIC);
				if (worldMoonPhaseInterimCalc != worldMoonPhaseInterimCalcLast) {
					worldMoonPhaseInterimCalcLast = worldMoonPhaseInterimCalc;
					worldMoonPhaseCalc = (int) FastDivision.remainderUnsignedFast((worldMoonPhaseInterimCalcLast + MOONPHASES), MOONPHASES_MAGIC);
					if (worldMoonPhaseCalc != worldMoonPhaseCalcLast) {
						worldMoonPhaseCalcLast = worldMoonPhaseCalc;
						Timekeeper.this.worldMoonPhase = worldMoonPhaseCalcLast;
					}
				}
				//worldMoonPhaseCalc = (int) FastDivision.remainderUnsignedFast()

				// dayTicksElapsed
				dayTicksElapsedCalc = (int) (worldTimeLast - (worldDayCountCalc * TICKS_PER_DAY));
				if (dayTicksElapsedCalc != dayTicksElapsedCalcLast) {
					dayTicksElapsedCalcLast = dayTicksElapsedCalc;
					Timekeeper.this.dayTicksElapsed = dayTicksElapsedCalcLast;
				}

				// hourInDay
				//dayHoursRawElapsedCalc = (int) (dayTicksElapsedCalc / TICKS_PER_HOUR);
				dayHoursRawElapsedCalc = (int) FastDivision.divideUnsignedFast(dayTicksElapsedCalc, TICKS_PER_HOUR_MAGIC);
				if (dayHoursRawElapsedCalc != dayHoursRawElapsedCalcLast) {
					dayHoursRawElapsedCalcLast = dayHoursRawElapsedCalc;
					Timekeeper.this.hourInDay = dayHoursRawElapsedCalcLast;
				}

				// ticksSinceHourElapsed
				dayTicksPastHourElapsedCalc = (int) (dayTicksElapsedCalc - (dayHoursRawElapsedCalc * TICKS_PER_HOUR));
				if (dayTicksPastHourElapsedCalc != dayTicksPastHourElapsedCalcLast) {
					dayTicksPastHourElapsedCalcLast = dayTicksPastHourElapsedCalc;
					Timekeeper.this.ticksSinceHourElapsed = dayTicksPastHourElapsedCalcLast;
				}

				// minutesSinceHourElapsed
				//dayMinutesPastHourElapsedCalc = (int) (dayTicksPastHourElapsedCalc * 60 / TICKS_PER_HOUR);
				dayMinutesPastHourElapsedCalc = (int) (FastDivision.divideUnsignedFast(dayTicksPastHourElapsedCalc * 60L, TICKS_PER_HOUR_MAGIC));
				if (dayMinutesPastHourElapsedCalc != dayMinutesPastHourElapsedCalcLast) {
					dayMinutesPastHourElapsedCalcLast = dayMinutesPastHourElapsedCalc;
					Timekeeper.this.minutesSinceHourElapsed = dayMinutesPastHourElapsedCalcLast;
				}
			}
		}
	}
}
