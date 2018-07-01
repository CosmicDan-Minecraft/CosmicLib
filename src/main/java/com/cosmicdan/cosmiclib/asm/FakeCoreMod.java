package com.cosmicdan.cosmiclib.asm;

import lombok.extern.log4j.Log4j2;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

/**
 * Purpose of this "CoreMod" is just to add our ASM helpers to the classpath.
 * I don't know of any other way to do this, I tried using ITweaker but meh
 * @author Daniel 'CosmicDan' Connolly
 */
@IFMLLoadingPlugin.Name("CosmicLibFakeCoreMod")
//@IFMLLoadingPlugin.MCVersion(value = "1.12.2")
@IFMLLoadingPlugin.SortingIndex(1) // How early your core mod is called - Use > 1000 to work with srg names
@Log4j2(topic = "CosmicLib/FakeCoreMod")
public class FakeCoreMod implements IFMLLoadingPlugin {

	private static final String[] CLASSES = {
			"com.cosmicdan.cosmiclib.asm.AbstractInsnTransformer"
	};

	@Override
	public String[] getASMTransformerClass() {
		log.info("[i] FakeCoreMod loading ASM helper classes...");
		try {
			for (final String className : CLASSES) {
				Class.forName(className);
			}
		} catch (ClassNotFoundException e) {
			log.error(e);
		}
		return null;
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}
