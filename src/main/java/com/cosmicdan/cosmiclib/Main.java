package com.cosmicdan.cosmiclib;

import com.cosmicdan.cosmiclib.annotations.ForgeDynamic;
import com.cosmicdan.cosmiclib.common.CommonProxy;
import com.cosmicdan.cosmiclib.common.ModConstants;
import lombok.extern.log4j.Log4j2;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@SuppressWarnings({"StaticNonFinalField", "WeakerAccess"})
@Log4j2(topic = "CosmicLib")
@Mod(modid = ModConstants.MODID, version = ModConstants.VERSION, certificateFingerprint = "@jar_fingerprint@")
public class Main {
	@ForgeDynamic
	@Mod.Instance(ModConstants.MODID)
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

	@Mod.EventHandler
	public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
		log.warn("Invalid fingerprint detected! The file " + event.getSource().getName() + " may have been tampered with. This mod will NOT be supported by CosmicDan.");
	}
}
