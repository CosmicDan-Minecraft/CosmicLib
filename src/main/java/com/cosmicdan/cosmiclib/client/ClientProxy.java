package com.cosmicdan.cosmiclib.client;

import com.cosmicdan.cosmiclib.annotations.ForgeEntryPoint;
import com.cosmicdan.cosmiclib.client.eventhandlers.ConfigGuiEvents;
import com.cosmicdan.cosmiclib.common.CommonProxy;
import com.cosmicdan.cosmiclib.registry.ModSpritesRegistry;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Client-only events
 * LEGACY ONLY. Use the new RegistryEvent system where possible!
 */
@ForgeEntryPoint
@Log4j2(topic = "CosmicLib/ClientProxy")
public class ClientProxy extends CommonProxy {
	@Override
	public final void preInit(final FMLPreInitializationEvent event) {
		super.preInit(event);
		MinecraftForge.EVENT_BUS.register(new ModSpritesRegistry());
		log.info("Registered ModSpritesRegistry");
	}

	@Override
	public final void init(final FMLInitializationEvent event) {
		super.init(event);
	}

	@Override
	public final void postInit(final FMLPostInitializationEvent event) {
		super.postInit(event);
		MinecraftForge.EVENT_BUS.register(new RenderDebugInfo());
		MinecraftForge.EVENT_BUS.register(new ConfigGuiEvents());
		log.info("Registered RenderDebugInfo");
	}

	@Override
	public void registerItemInventoryRenderer(final String modid, final Item item, final int meta, final String id) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(modid + ':' + id, "inventory"));
	}
}
