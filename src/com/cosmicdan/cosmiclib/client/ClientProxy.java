package com.cosmicdan.cosmiclib.client;

import com.cosmicdan.cosmiclib.Main;
import com.cosmicdan.cosmiclib.common.CommonProxy;
import lombok.extern.log4j.Log4j2;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Log4j2(topic =  Main.MODID + "/ClientProxy")
public class ClientProxy extends CommonProxy {
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
