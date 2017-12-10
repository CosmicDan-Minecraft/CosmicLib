package com.cosmicdan.cosmiclib.obfuscation;

import com.cosmicdan.cosmiclib.util.EnvInfo;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;

/**
 * A String holder that will return one of two values depending on if the environment is obfuscated or development mode
 */
@EqualsAndHashCode
@Log4j2
public class ObfuscatedString {
	private final String stringObf;
	private final String stringDev;

	public ObfuscatedString(String stringObf, String stringDev) {
		this.stringObf = stringObf;
		this.stringDev = stringDev;
	}

	public final String get() {
		return toString();
	}

	@Override
	public final String toString() {
		return EnvInfo.isDevEnv() ? stringDev : stringObf;
	}
}
