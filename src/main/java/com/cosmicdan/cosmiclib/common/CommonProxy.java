package com.cosmicdan.cosmiclib.common;

import com.cosmicdan.cosmiclib.gamedata.Timekeeper;
import lombok.extern.log4j.Log4j2;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Events for both physical servers and clients. Most things will belong here.
 */
@Log4j2(topic = "CosmicLib/CommonProxy")
public class CommonProxy {
	/**
	 * Register blocks/items to GameRegistry, (tile) entities ans assign oredict names
	 */
	public void preInit(FMLPreInitializationEvent event) {

	}

	/**
	 * Register worldgen, recipes, event handlers and send IMC messages
	 */
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(Timekeeper.getInstance());
	}

	/**
	 * Other stuff e.g. mod integrations, housework
	 */
	public void postInit(FMLPostInitializationEvent event) {

	}
}
