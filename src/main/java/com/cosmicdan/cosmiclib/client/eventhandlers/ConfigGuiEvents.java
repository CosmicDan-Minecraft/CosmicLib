package com.cosmicdan.cosmiclib.client.eventhandlers;

import lombok.extern.log4j.Log4j2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author Daniel 'CosmicDan' Connolly
 */
@Log4j2(topic = "CosmicLib/ConfigGuiEvents")
public class ConfigGuiEvents {
	@SuppressWarnings("unused")
	private static boolean DRAW_CMC_MISSING_TEXT = false;

	@SubscribeEvent
	public void onPreInitGui(final GuiScreenEvent.InitGuiEvent.Post event) {
		DRAW_CMC_MISSING_TEXT = false;
		// checks if the GUI is a Config GUI with CoreModCompanion settings; if so it can disable it if the CMC is missing
		// Lots of hard-coded strings here, meh.
		if (event.getGui() instanceof GuiConfig) {
			// check if this is a "CoreModCompanion" config screen
			final String configSubtitle = ((GuiConfig) event.getGui()).titleLine2;
			if ((null != configSubtitle) && configSubtitle.contains("CoreModCompanion")) {
				// also confirm that this is from a cosmicdan mod config (just in case the "CoreModCompanion" name catches on lol)
				final String modId = ((GuiConfig) event.getGui()).modID;
				final Class<?>[] modConfigClasses = ConfigManager.getModConfigClasses(modId);
				for (final Class<?> modConfigClass : modConfigClasses) {
					if (modConfigClass.getName().contains("cosmicdan")) {
						boolean cmcMissing = false;
						try {
							Class.forName("com.cosmicdan." + modId + ".coremod.CorePlugin");
						} catch (final ClassNotFoundException exception) {
							cmcMissing = true;
						}
						if (cmcMissing) {
							// remove all entries
							((GuiConfig) event.getGui()).entryList.listEntries.clear();
							// manually draw missing CMC text
							DRAW_CMC_MISSING_TEXT = true;
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("IntegerDivisionInFloatingPointContext")
	@SubscribeEvent
	public void drawCmcMissingText(final GuiScreenEvent.DrawScreenEvent event) {
		final String line1 = I18n.format("cosmiclib.config.CmcMissingTextLine1", TextFormatting.RED, TextFormatting.RESET);
		final String line2 = I18n.format("cosmiclib.config.CmcMissingTextLine2");
		if (DRAW_CMC_MISSING_TEXT) {
			final ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
			final int width = res.getScaledWidth();
			final int height = res.getScaledHeight();
			final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
			GlStateManager.pushMatrix();
			GlStateManager.translate((width / 2), (height / 2), 0.0F);
			fontRenderer.drawString(line1, -fontRenderer.getStringWidth(line1) / 2, -5, 0xFFFFFF, true);
			fontRenderer.drawString(line2, -fontRenderer.getStringWidth(line2) / 2, 5, 0xFFFFFF, true);
			GlStateManager.popMatrix();
		}
	}
}
