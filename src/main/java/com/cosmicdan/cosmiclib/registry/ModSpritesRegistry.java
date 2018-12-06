package com.cosmicdan.cosmiclib.registry;

import com.cosmicdan.cosmiclib.annotations.ForgeModLibrary;
import com.cosmicdan.cosmiclib.util.EnvInfo;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

/**
 * Registry for sprites - textures that are stitched to the atlas. This allows us to batch-render stuff with the VertexBuffer and avoid
 * native OpenGL calls for better performance.
 * To use, call {@link #register(ModSprite)} with your own ModSprite objects. Best do this as early as possible (e.g. pre-init) as it
 * must be done before texture stitch event. When that texture stitch event is fired, any registered ModSprite objects will have their
 * {@link ModSprite#getSprite()} populated.
 * @author Daniel 'CosmicDan' Connolly
 */
@ForgeModLibrary
@Log4j2(topic = "CosmicLib/ModSpritesRegistry")
public class ModSpritesRegistry {
	private static final Collection<ModSprite> MOD_SPRITE_REGISTRATIONS = new HashSet<>(1000);

	/**
	 * Register sprites here. Be sure to call this in preInit from client side.
	 * @param modSprite Your mod's ModSprite object. The {@link ModSprite#getSprite()} accessor will return the TextureAtlasSprite
	 *                  once registration succeeds.
	 */
	@ForgeModLibrary
	public static void register(final ModSprite modSprite) {
		MOD_SPRITE_REGISTRATIONS.add(modSprite);
	}

	/**
	 * Batch registration of multiple sprites, but otherwise the same as {@link #register(ModSprite)}
	 */
	@ForgeModLibrary
	public static void register(final ModSprite[] modSprites) {
		MOD_SPRITE_REGISTRATIONS.addAll(Arrays.asList(modSprites));
	}

	@SubscribeEvent
	public void onTextureStitch(final TextureStitchEvent.Pre event) {
		for (final ModSprite modSprite : MOD_SPRITE_REGISTRATIONS) {
			final TextureAtlasSprite atlasSprite = event.getMap().registerSprite(modSprite.getResourceLocation());
			modSprite.setSprite(atlasSprite);
			if (EnvInfo.isDevEnv())
				log.info("Registered sprite: {}", modSprite.getResourceLocation().toString());
		}
	}
}
