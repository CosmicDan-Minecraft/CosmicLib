package com.cosmicdan.cosmiclib.registry;

import com.cosmicdan.cosmiclib.annotations.ForgeModLibrary;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

/**
 * Simple holder for registering textures in the {@link ModSpritesRegistry}.
 * @author Daniel 'CosmicDan' Connolly
 */
@SuppressWarnings("WeakerAccess")
@ForgeModLibrary
public class ModSprite {
	/** Full resource location for this sprite, required for registration with ModSpritesRegistry.
	 * Don't include the "textures" prefix or ".png" suffix. */
	@Getter
	private final ResourceLocation resourceLocation;
	/** Populated after the sprite is registered (i.e. TextureStitchEvent) */
	@Getter @Setter
	private TextureAtlasSprite sprite;

	@ForgeModLibrary
	public ModSprite(final String modId, final String resourcePath) {
		this(new ResourceLocation(modId + ':' + resourcePath));
	}

	@ForgeModLibrary
	public ModSprite(final ResourceLocation resourceLocation) {
		this.resourceLocation = resourceLocation;
	}

	@ForgeModLibrary
	public static TextureAtlasSprite[] getAllSprites(final ModSprite[] modSprites) {
		final TextureAtlasSprite[] sprites = new TextureAtlasSprite[modSprites.length];
		for (int index = 0; index < modSprites.length; index++) {
			sprites[index] = modSprites[index].sprite;
		}
		return sprites;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof ModSprite)) return false;
		return this.resourceLocation.equals(((ModSprite) obj).resourceLocation);
	}

	@Override
	public int hashCode() {
		return resourceLocation.hashCode();
	}
}
