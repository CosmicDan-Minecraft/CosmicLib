package com.cosmicdan.cosmiclib.client.gui;

import com.cosmicdan.cosmiclib.Main;
import com.cosmicdan.cosmiclib.gamedata.Timekeeper;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Log4j2(topic = Main.MODNAME + "/RenderDebugInfo")
public class RenderDebugInfo {
	@SubscribeEvent
	public void onDebugOverlay(RenderGameOverlayEvent.Text event) {
		if (Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			String debugLine = "CosmicLib: ";

			Timekeeper timekeeper = Timekeeper.getInstance();
			debugLine += timekeeper.calcHourOfDay() + ":" + timekeeper.getMinutesSinceHourElapsed();
			//debugLine += timekeeper.calcHourOfDay() + ":" + timekeeper.getTicksSinceHourElapsed();
			//debugLine += timekeeper.getHourInDay() + ":" + timekeeper.getTicksSinceHourElapsed();

			event.getLeft().add(debugLine);
		}
	}
}
