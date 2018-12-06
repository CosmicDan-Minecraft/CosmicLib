package com.cosmicdan.cosmiclib.client.gui.elements;

import com.cosmicdan.cosmiclib.annotations.ForgeModLibrary;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/**
 * @author Daniel 'CosmicDan' Connolly
 */
@SuppressWarnings("AbstractClassNeverImplemented")
@ForgeModLibrary
public abstract class SpriteElement extends CosmicGuiElementBase<TextureAtlasSprite[]> {
	protected SpriteElement(final TextureAtlasSprite[] sprites) {
		super(sprites);
	}

	/**
	 * CosmicGui is expected to have prepared the tesBuffer for drawing already
	 * @param tesBuffer As passed in from a calling CosmicGui implementation.
	 */
	@Override
	public void draw(final BufferBuilder tesBuffer) {
		if (isLayered()) {
			for (int frame = getIndex(); frame < getData().length; frame++) {
				drawFrame(tesBuffer, frame);
			}
		} else {
			drawFrame(tesBuffer, getIndex());
		}
	}

	private void drawFrame(final BufferBuilder tesBuffer, final int frame) {
		final int width = getData()[frame].getIconWidth();
		final int height = getData()[frame].getIconHeight();
		final float textureMinU = getData()[frame].getMinU();
		final float textureMinV = getData()[frame].getMinV();
		final float textureMaxU = getData()[frame].getMaxU();
		final float textureMaxV = getData()[frame].getMaxV();
		final int frameOffsetX = getOffsetXForFrame(frame);
		final int frameOffsetY = getOffsetYForFrame(frame);

		tesBuffer.pos(getPositionX() + frameOffsetX, getPositionY() + frameOffsetY + height, 0)
				.tex(textureMinU, textureMaxV).endVertex();
		tesBuffer.pos(getPositionX() + frameOffsetX + width, getPositionY() + frameOffsetY + height, 0)
				.tex(textureMaxU, textureMaxV).endVertex();
		tesBuffer.pos(getPositionX() + frameOffsetX + width, getPositionY() + frameOffsetY, 0)
				.tex(textureMaxU, textureMinV).endVertex();
		tesBuffer.pos(getPositionX() + frameOffsetX, getPositionY() + frameOffsetY, 0)
				.tex(textureMinU, textureMinV).endVertex();
	}
}
