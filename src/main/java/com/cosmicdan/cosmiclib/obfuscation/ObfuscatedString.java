package com.cosmicdan.cosmiclib.obfuscation;

import com.cosmicdan.cosmiclib.util.EnvInfo;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;

/**
 * A String holder that will return one of two values depending on if the environment is obfuscated or development mode
 */
@EqualsAndHashCode
@Log4j2(topic = "CosmicLib/ObfuscatedString")
public class ObfuscatedString {
	private final String obf;
	private final String dev;

	public ObfuscatedString(final String obf, final String dev) {
		this.obf = obf;
		this.dev = dev;
	}

	public final String get() {
		return toString();
	}

	public final String getDev() {
		return dev;
	}

	public final String getObf() {
		return obf;
	}

	@Override
	public final String toString() {
		return EnvInfo.isDevEnv() ? dev : obf;
	}
}
