package com.cosmicdan.cosmiclib.reflection;

import com.cosmicdan.cosmiclib.obfuscation.ObfuscatedString;
import lombok.extern.log4j.Log4j2;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
@Log4j2(topic = "CosmicLib/FieldMirror")
public class FieldMirror<T> extends MirrorBase {
	// TODO: Convert to Method Handle? See https://www.baeldung.com/java-method-handles
	private final Class<?> fieldOwner;
	private final ObfuscatedString fieldName;

	private Field fieldAccess = null;

	public FieldMirror(final ObfuscatedString fieldName, final Class<?> fieldOwner) {
		this.fieldName = fieldName;
		this.fieldOwner = fieldOwner;
	}

	@Override
	public final void init() {
		if (null == fieldAccess) {
			try {
				fieldAccess = ReflectionHelper.findField(fieldOwner, fieldName.get());
			} catch (final RuntimeException exception) {
				throwError(log, exception, "Could not find field: " + fieldOwner.getName() + '#' + fieldName);
			}
		}
	}

	/**
	 * Set this field to specified value
	 * @param fieldOwner The object instance that owns this field
	 * @param fieldValue The value to set
	 */
	public final void set(final Object fieldOwner, final T fieldValue) {
		init();
		try {
			fieldAccess.set(fieldOwner, fieldValue);
		} catch (final IllegalAccessException exception) {
			throwError(log, exception, "Could not set field: " + this.fieldOwner.getName() + '#' + fieldName);
		}
	}

	/**
	 * Get this field value
	 * @param fieldInstance The object instance that owns this field
	 * @return The value, or null if an exception was thrown.
	 */
	public final T get(final Object fieldInstance) {
		init();
		T retVal = null;
		try {
			//noinspection unchecked
			retVal = (T) fieldAccess.get(fieldInstance);
		} catch (final IllegalAccessException exception) {
			throwError(log, exception, "Could not get field: " + fieldInstance.getClass().getName() + '#' + fieldName);
		}
		return retVal;
	}
}
