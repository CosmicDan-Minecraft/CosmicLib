package com.cosmicdan.cosmiclib.client;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * @author Daniel 'CosmicDan' Connolly
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@UtilityClass
@Log4j2(topic = "CosmicLib/CosmicToaster")
public class CosmicToaster {
	/**
	 * @return true if an existing toast was updated, otherwise false
	 */
	public static boolean addOrUpdate(final Object id, final long duration, final String title, final String subtitle) {
		final GuiToast guiToast = Minecraft.getMinecraft().getToastGui();
		final Toast existingToast = guiToast.getToast(Toast.class, id);

		boolean retVal = false;
		if (null == existingToast) {
			guiToast.add(new Toast(id, duration, title, subtitle));
		} else {
			existingToast.update(duration, title, subtitle);
			retVal = true;
		}
		return retVal;
	}

	private static class Toast implements IToast {
		private final Object uniqueId;
		private long duration;
		private String title;
		private String subtitle;
		private long firstDrawTime;
		private boolean newDisplay;

		private int scaleFactor;
		private double subtitleScale;
		private double subtitlePosMulti;
		private int wrapWidth;

		Toast(final Object uniqueId, final long duration, final String title, final String subtitle) {
			this.uniqueId = uniqueId;
			this.duration = duration;
			this.title = title;
			this.subtitle = subtitle;
			updateScaling();
		}

		@SuppressWarnings("NullableProblems")
		@ParametersAreNonnullByDefault
		@MethodsReturnNonnullByDefault
		@Override
		public IToast.Visibility draw(final GuiToast toastGui, final long delta) {
			if (this.newDisplay) {
				this.firstDrawTime = delta;
				this.newDisplay = false;
			}

			toastGui.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
			GlStateManager.color(1.0F, 1.0F, 1.0F);
			toastGui.drawTexturedModalRect(0, 0, 0, 64, 160, 32);

			toastGui.getMinecraft().fontRenderer.drawString(this.title, 18, 7, -256);

			GL11.glPushMatrix();
			GL11.glScaled(subtitleScale, subtitleScale, subtitleScale);

			toastGui.getMinecraft().fontRenderer.drawSplitString(this.subtitle, (int) (18 * subtitlePosMulti), (int) (18 * subtitlePosMulti), wrapWidth, -1);

			GL11.glScaled(1.0, 1.0, 1.0);
			GL11.glPopMatrix();

			return (duration > (delta - this.firstDrawTime)) ? Visibility.SHOW : Visibility.HIDE;
		}

		@SuppressWarnings({"SuspiciousGetterSetter", "NullableProblems"})
		@MethodsReturnNonnullByDefault
		@Override
		public Object getType() {
			return uniqueId;
		}

		private void updateScaling() {
			//scaleFactor = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
			subtitleScale = 0.5;
			subtitlePosMulti = 1.0 / subtitleScale;
			wrapWidth = (int) (138 * subtitlePosMulti);
		}

		private void update(final long newDuration, final String newTitle, final String newSubtitle) {
			this.duration = newDuration;
			this.title = newTitle;
			this.subtitle = newSubtitle;
			this.newDisplay = true;
			updateScaling();
		}
	}
}
