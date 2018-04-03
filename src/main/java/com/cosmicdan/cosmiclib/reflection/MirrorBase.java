package com.cosmicdan.cosmiclib.reflection;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;

/**
 * Mirror base class for shared functionality.
 * The Mirror classes are used as helpers for vanilla code reflection operations.
 * See each Mirror implementation for details.
 */
@Log4j2(topic = "CosmicLib/MirrorBase")
abstract class MirrorBase {
	/**
	 * Where the expensive reflection stuff happens - finding the target method/field/whatever, setting it accessible,
	 * then caching a reference to it internally for quicker use later.
	 */
	public abstract void init();

	/**
	 * Helper method for logging reflection exceptions
	 */
	static void logException(Logger logIn, Exception e, String msg) {
		logIn.error(msg);
		throw new RuntimeException(e);
	}
}
