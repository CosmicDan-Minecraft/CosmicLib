package com.cosmicdan.cosmiclib.client.gui.elements;

import com.cosmicdan.cosmiclib.annotations.ForgeModLibrary;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;

/**
 * @author Daniel 'CosmicDan' Connolly
 */
@ForgeModLibrary
@SuppressWarnings({"WeakerAccess", "AbstractClassNeverImplemented"})
@Log4j2
public abstract class TextElement extends CosmicGuiElementBase<String[]> {
	private int color = 0xFFFFFF;
	private boolean dropShadow = true;

	protected TextElement(final String[] data) {
		super(data);
	}

	public TextElement setColor(final int color) {
		this.color = color;
		return this;
	}

	public TextElement setDropShadow(final boolean dropShadow) {
		this.dropShadow = dropShadow;
		return this;
	}

	@ForgeModLibrary
	protected void setText(final String newText, final int index) {
		getData()[index] = newText;
	}

	@Override
	public void draw(final BufferBuilder tesBuffer) {
		Minecraft.getMinecraft().fontRenderer.drawString(getData()[getIndex()], (float) getPositionX(), (float) getPositionY(), color, dropShadow);
	}
}
