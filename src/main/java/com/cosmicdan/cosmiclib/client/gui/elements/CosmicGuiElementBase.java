package com.cosmicdan.cosmiclib.client.gui.elements;

import com.cosmicdan.cosmiclib.client.gui.CosmicGui;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import net.minecraft.client.renderer.BufferBuilder;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @param <T> The data type of this element. If you wish to take advantage of batch sprite rendering, be sure this is a TextureAtlasSprite.
 */
@SuppressWarnings({"WeakerAccess", "unused", "AbstractClassNeverImplemented"})
public abstract class CosmicGuiElementBase<T> {
	private static final AtomicLong NEXT_ELEMENT_ID = new AtomicLong(0);
	/**
	 * Unique ID for this element. This is the only value used for equality checks, making it easier to replace existing elements in
	 * {@link CosmicGui#setElements} for example.
	 */
	@Getter @JsonIgnore
	private final long elementId = NEXT_ELEMENT_ID.getAndIncrement();

	@Getter
	private double positionX, positionY;
	@Getter
	private int index;
	private T data;

	CosmicGuiElementBase(final T data) {
		this.data = data;
	}

	public void setData(final T data) {
		this.data = data;
	}

	public T getData() {
		return data;
	}

	/** Set the element index to be drawn. If {@link #isLayered} is true, this determines the starting element for the layering; if false
	 * then this determines the only element index to be drawn. */
	public void setIndex(final int index) {
		this.index = index;
	}

	public void setPosition(final double positionX, final double positionY) {
		this.positionX = positionX;
		this.positionY = positionY;
	}

	public abstract void draw(BufferBuilder tesBuffer);

	/** Return true if all data elements should be drawn in sequence during rendering, otherwise false for single element index. Use
	 * {@link #setIndex} to set either the starting index to draw in the case of the former, or only index to be drawn for the latter. */
	public abstract boolean isLayered();

	/** Return the x offset desired for a given element index to draw. */
	public abstract int getOffsetXForFrame(int dataIndexRequested);

	/** Return the y offset desired for a given element index to draw. */
	public abstract int getOffsetYForFrame(int dataIndexRequested);

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof CosmicGuiElementBase)) return false;
		return this.elementId == ((CosmicGuiElementBase<?>)obj).elementId;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(elementId);
	}

}
