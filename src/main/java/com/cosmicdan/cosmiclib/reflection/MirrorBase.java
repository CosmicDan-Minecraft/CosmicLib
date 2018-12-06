package com.cosmicdan.cosmiclib.reflection;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;

/**
 * Mirror base class for shared functionality.
 * The Mirror classes are used as helpers for vanilla code reflection operations.
 * See each Mirror implementation for details.
 */
@SuppressWarnings("unused")
@Log4j2(topic = "CosmicLib/MirrorBase")
abstract class MirrorBase {
	// TODO: Convert to Method Handle? See https://www.baeldung.com/java-method-handles
	/**
	 * Where the expensive reflection stuff happens - finding the target method/field/whatever, setting it accessible,
	 * then caching a reference to it internally for quicker use later.
	 */
	public abstract void init();

	/**
	 * Helper method for logging reflection exceptions
	 */
	static void throwError(final Logger logIn, final Exception e, final String msg) {
		logIn.error(msg);
		throw new RuntimeException(e);
	}
}
