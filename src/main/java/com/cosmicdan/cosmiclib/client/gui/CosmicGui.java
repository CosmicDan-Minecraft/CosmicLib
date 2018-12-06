package com.cosmicdan.cosmiclib.client.gui;

import com.cosmicdan.cosmiclib.client.gui.elements.CosmicGuiElementBase;
import com.cosmicdan.cosmiclib.client.gui.elements.SpriteElement;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Daniel 'CosmicDan' Connolly
 */
@SuppressWarnings("AbstractClassNeverImplemented")
@Log4j2(topic = "CosmicLib/CosmicGui")
public abstract class CosmicGui {
	private final Minecraft minecraftInstance = Minecraft.getMinecraft();
	private final BufferBuilder tesBuffer = Tessellator.getInstance().getBuffer();
	private final Set<SpriteElement> elementsCacheSprites = new HashSet<>(10);
	private final Set<CosmicGuiElementBase<?>> elementsCacheOthers = new HashSet<>(10);

	private double scaleFactor;
	private int updateCount = Integer.MAX_VALUE; // initial max to force update on first draw

	/**
	 * Call this method EVERY frame from whatever your GUI implementation is.
	 * @param resolution Pass in the current ScaledResolution of Minecraft.
	 */
	@SuppressWarnings("unused")
	protected final void doDraw(final ScaledResolution resolution) {
		if (updateCount >= getElementRegenRate()) {
			updateCount = 0;
			final Set<CosmicGuiElementBase<?>> newElements = new HashSet<>(10);
			setElements(newElements);
			// sort the elements into their respective cache sets
			for (final CosmicGuiElementBase<?> element : newElements) {
				if (element instanceof SpriteElement)
					elementsCacheSprites.add((SpriteElement) element);
				else
					elementsCacheOthers.add(element);
			}
			// update elements
			updateElements(minecraftInstance, resolution);
			//guiVbo.deleteGlBuffers();
			// update scale factor.
			if (3 <= resolution.getScaleFactor()) {
				// TODO: Only if config "Scaling" = "Optimal (Large GUI Scale will be shrunk one factor and Small will be enlarged one factor, i.e. both will appear as if scale is Medium)"
				// for large and auto, reduce it down to medium-equivalent
				scaleFactor = 1.0 - (1.0 / resolution.getScaleFactor());
			} else if (1 == resolution.getScaleFactor()) {
				// TODO: Only if config "Scaling" = "Optimal (Large GUI Scale will be shrunk one factor and Small will be enlarged one factor, i.e. both will appear as if scale is Medium)"
				// for small, double it
				scaleFactor = 2.0;
			} else {
				// TODO: Force if config "Scale" = "Original (Scale will be unchanged and match Minecraft settings"
				// keep original scale
				scaleFactor = 1.0;
			}
		}
		updateCount++;

		// draw all elements
		// TODO: Make a more-generic renderer and call that instead.
		// It should provide scaling for more than just sprites; and call the Renderer.draw directly rather than guiElement
		/*
		CosmicGuiRenderer.startDrawing(scaleFactor);
		for (final ICosmicGuiElement guiElement : elementsCache) {
			guiElement.draw(Tessellator.getInstance().getBuffer());
		}
		CosmicGuiRenderer.finishDrawing();
		*/

		GlStateManager.pushMatrix();
		GlStateManager.scale(scaleFactor, scaleFactor, scaleFactor);
		// first render sprites
		if (!elementsCacheSprites.isEmpty()) {
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			tesBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			for (final SpriteElement spriteElement : elementsCacheSprites) {
				spriteElement.draw(tesBuffer);
			}
			Tessellator.getInstance().draw();
		}
		// now draw all other elements
		for (final CosmicGuiElementBase<?> guiElement : elementsCacheOthers) {
			guiElement.draw(tesBuffer);
		}

		GlStateManager.popMatrix();
	}

	/**
	 * Specify the regen rate for all GUI elements; i.e. the frame delay between calls to
	 * {@link #updateElements(Minecraft, ScaledResolution)} and {@link #setElements(Set)}.
	 * @return The desired frame-delay for successive calls
	 */
	public abstract int getElementRegenRate();

	/**
	 * Used to build the elementsCache with your desired elements. This method will be called every n frames, where n is the value returned
	 * by {@link #getElementRegenRate()}. It is called before {@link #updateElements(Minecraft, ScaledResolution)} - be sure to keep a
	 * local reference to all your elements so you can update them later. Note that this will always be called on the very initial frame,
	 * before {@link #updateElements}. Also be sure to return early in the event that your elements do not need to be rebuilt.
	 * @param elementsToAdd An empty Set to put all your elements.
	 */
	public abstract void setElements(final Set<CosmicGuiElementBase<?>> elementsToAdd);

	/**
	 * Perform your logic and updates for the elements here. This method will be called every n frames, where n is the value returned by
	 * {@link #getElementRegenRate()}. Note that this will always be called on the very initial frame, after {@link #setElements}
	 * is called.
	 * @param minecraft A Minecraft instance to work with
	 * @param resolution Current ScaledResolution
	 */
	public abstract void updateElements(final Minecraft minecraft, final ScaledResolution resolution);
}
