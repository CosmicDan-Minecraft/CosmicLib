package com.cosmicdan.cosmiclib.util;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import net.minecraft.launchwrapper.Launch;

/**
 * Environment info utilities
 */
@UtilityClass
@Log4j2(topic = "CosmicLib/EnvInfo")
public final class EnvInfo {

	/**
	 * Utility method to test if we're in a development (obfuscated) environment or not
	 * @return True if in a dev environment (and therefore deobfuscated names are in use), otherwise false
	 */
	public static boolean isDevEnv() {
		return (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
	}
}
