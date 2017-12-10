package com.cosmicdan.cosmiclib.gamedata;

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
	private static final double TICKS_PER_HOUR = 1000.0;

	private static final int TICKER_MAX = 10; // TODO: Offload to config. Also a separate ticker for client...?

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
	/** Get the number of ticks that have passed on this day. */
	@Getter private volatile int dayTicksElapsed;
	/** Get current hour of the day. Zero-based. Use {@link #calcHourOfDay()} to get a clock-adjusted hour. */
	@Getter private volatile int hourInDay; // 0 == 6am
	/** Get the number of ticks that have passed since the last whole hour. Use {@link #getMinutesSinceHourElapsed()} for minutes-converted value. */
	@Getter private volatile int ticksSinceHourElapsed;
	/** Get the number of minutes that have passed since the last whole hour. */
	@Getter private volatile int minutesSinceHourElapsed;

	@SubscribeEvent
	public void onServerTick(TickEvent.WorldTickEvent event) {
		if ((null != event.world) && (TickEvent.Phase.END == event.phase)) {
			tick(event.world.getWorldTime());
		}
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		// for clients (both SSP and SMP since we want to keep a client-side timekeeper too)
		if ((null != Minecraft.getMinecraft().world) && (TickEvent.Phase.END == event.phase)) {
			tick(Minecraft.getMinecraft().world.getWorldTime());
		}
	}

	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
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
	public void onWorldUnload(WorldEvent.Unload event) {
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

	private void tick(long worldTime) {
		if (worldTime == this.worldTimeCached)
			// Only tick once per... well, tick. This is mostly for integrated server (SSP)
			return;

		ticker++;
		//log.info("Ticker for worldtime " + worldTime + " is at " + ticker);
		if (!syncDone) {
			if (0 == (worldTime % TICKER_MAX)) {
				log.info("Timekeeper update rate synchronised to worldtime quotient");
				ticker = TICKER_MAX;
				syncDone = true;
			}
		}
		if (TICKER_MAX == ticker) {
			ticker = 0;
			this.worldTimeCached = worldTime;
		}
	}

	/**
	 * Calculate a clock-adjusted hour of day.
	 * e.g. midnight returns 0, sunrise (actual start of a Minecraft day) returns 6.
	 */
	public int calcHourOfDay() {
		return (hourInDay + 6) - ((17 < hourInDay) ? 24 : 0);
	}

	@SuppressWarnings("NonStaticInnerClassInSecureContext")
	private final class TimekeeperSchedule implements Runnable {
		private long worldTimeLast = 0L;
		private int worldDayCountCalc = 0;
		private int worldDayCountCalcLast = 0;
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
				worldDayCountCalc = (int) (worldTimeLast / TICKS_PER_DAY);
				if (worldDayCountCalc != worldDayCountCalcLast) {
					worldDayCountCalcLast = worldDayCountCalc;
					Timekeeper.this.worldDayCount = worldDayCountCalcLast;
				}

				// dayTicksElapsed
				dayTicksElapsedCalc = (int) (worldTimeLast - (worldDayCountCalc * TICKS_PER_DAY));
				if (dayTicksElapsedCalc != dayTicksElapsedCalcLast) {
					dayTicksElapsedCalcLast = dayTicksElapsedCalc;
					Timekeeper.this.dayTicksElapsed = dayTicksElapsedCalcLast;
				}

				// hourInDay
				dayHoursRawElapsedCalc = (int) (dayTicksElapsedCalc / TICKS_PER_HOUR);
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
				dayMinutesPastHourElapsedCalc = (int) ((dayTicksPastHourElapsedCalc / TICKS_PER_HOUR) * 60);
				if (dayMinutesPastHourElapsedCalc != dayMinutesPastHourElapsedCalcLast) {
					dayMinutesPastHourElapsedCalcLast = dayMinutesPastHourElapsedCalc;
					Timekeeper.this.minutesSinceHourElapsed = dayMinutesPastHourElapsedCalcLast;
				}
			}
		}
	}


}
