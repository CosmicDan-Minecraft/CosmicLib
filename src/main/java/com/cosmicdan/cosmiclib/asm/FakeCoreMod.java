package com.cosmicdan.cosmiclib.asm;

import lombok.extern.log4j.Log4j2;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Purpose of this "CoreMod" is just to ping the classloader to put ASM helpers on the classpath so other Coremods can use them.
 * I don't know of any other way to do this, I tried using ITweaker but meh
 * @author Daniel 'CosmicDan' Connolly
 */
@SuppressWarnings("WeakerAccess")
@IFMLLoadingPlugin.Name("CosmicLibFakeCoreMod")
//@IFMLLoadingPlugin.MCVersion(value = "1.12.2")
@IFMLLoadingPlugin.SortingIndex(1) // How early your core mod is called - Use > 1000 to work with srg names
@Log4j2(topic = "CosmicLib/FakeCoreMod")
public class FakeCoreMod implements IFMLLoadingPlugin {

	private static final String[] CLASSES = {
			"com.cosmicdan.cosmiclib.asm.AbstractInsnTransformer"
	};

	@Nullable
	@Override
	public String[] getASMTransformerClass() {
		log.info("[i] FakeCoreMod loading ASM helper classes...");
		try {
			for (final String className : CLASSES) {
				Class.forName(className);
			}
		} catch (final ClassNotFoundException exception) {
			log.error(exception);
		}
		return null;
	}

	@Nullable
	@Override
	public String getModContainerClass() {
		return null;
	}

	@Nullable
	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(final Map<String, Object> data) {}

	@Nullable
	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}
