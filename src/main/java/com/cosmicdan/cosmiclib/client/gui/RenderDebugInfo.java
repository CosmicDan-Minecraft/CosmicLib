package com.cosmicdan.cosmiclib.client.gui;

import com.cosmicdan.cosmiclib.Main;
import com.cosmicdan.cosmiclib.gamedata.Timekeeper;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Log4j2(topic = "CosmicLib/RenderDebugInfo")
public class RenderDebugInfo {
	private final Timekeeper timekeeper = Timekeeper.getInstance();

	@SubscribeEvent
	public void onDebugOverlay(RenderGameOverlayEvent.Text event) {
		if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			String debugLine = "CosmicLib: ";

			debugLine += "Time = " + timekeeper.calcHourOfDay() + ":" + String.format("%02d", timekeeper.getMinutesSinceHourElapsed()) + "; ";
			debugLine += "Day# = " + timekeeper.getWorldDayCount() + "; ";
			debugLine += "Cached Ticks = " + timekeeper.getWorldTimeCached();

			event.getLeft().add(debugLine);
		}
	}
}
