package com.cosmicdan.cosmiclib;

import com.cosmicdan.cosmiclib.annotations.ForgeDynamic;
import com.cosmicdan.cosmiclib.common.CommonProxy;
import lombok.extern.log4j.Log4j2;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Log4j2(topic = "CosmicLib")
@Mod(modid = Main.MODID, version = "${version}")
public class Main {
	public static final String MODNAME = "CosmicLib";
	public static final String MODID = "cosmiclib";

	@ForgeDynamic
	@Mod.Instance(MODID)
	public static Main INSTANCE = null;

	@ForgeDynamic
	@SidedProxy(clientSide="com.cosmicdan.cosmiclib.client.ClientProxy", serverSide="com.cosmicdan.cosmiclib.server.ServerProxy")
	public static CommonProxy PROXY = null;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		PROXY.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		PROXY.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		PROXY.postInit(event);
	}
}
