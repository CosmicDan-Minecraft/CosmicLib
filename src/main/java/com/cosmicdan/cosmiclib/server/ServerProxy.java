package com.cosmicdan.cosmiclib.server;

import com.cosmicdan.cosmiclib.common.CommonProxy;
import lombok.extern.log4j.Log4j2;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Events that are only for dedicated servers.
 * LEGACY ONLY. Use the new RegistryEvent system where possible!
 */
@Log4j2(topic = "CosmicLib/ServerProxy")
public class ServerProxy extends CommonProxy {
	@Override
	public final void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}

	@Override
	public final void init(FMLInitializationEvent event) {
		super.init(event);
	}

	@Override
	public final void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}
}
