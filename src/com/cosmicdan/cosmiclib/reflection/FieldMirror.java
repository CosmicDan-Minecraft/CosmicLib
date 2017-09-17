package com.cosmicdan.cosmiclib.reflection;

import com.cosmicdan.cosmiclib.obfuscation.ObfuscatedString;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Field;

@Log4j2(topic =  "CosmicLib/FieldMirror")
public class FieldMirror extends MirrorBase {
	private final ObfuscatedString fieldName;
	private final Class<?> fieldClass;

	private Field field = null;

	public FieldMirror(ObfuscatedString fieldName, Class<?> fieldClass) {
		this.fieldName = fieldName;
		this.fieldClass = fieldClass;
	}

	@Override
	public final void init() {
		if (null == field) {
			try {
				field = fieldClass.getDeclaredField(fieldName.get());
				field.setAccessible(true);
			} catch (NoSuchFieldException e) {
				logException(log, e, "Field not found: " + fieldClass.getName() + '#' + fieldName);
			}
		}
	}

	/**
	 * Set this field to specified value
	 * @param fieldOwner The object instance that owns this field
	 * @param fieldValue The value to set
	 * @return True if successful, false if not
	 */
	public final boolean set(Object fieldOwner, Object fieldValue) {
		init();
		boolean retVal = false;
		try {
			field.set(fieldOwner, fieldValue);
			retVal = true;
		} catch (IllegalAccessException e) {
			logException(log, e, "Illegal access to field; cannot set: " + fieldOwner.getClass().getName() + '#' + field.getName());
		}
		return retVal;
	}

	/**
	 * Get this field value
	 * @param fieldOwner The object instance that owns this field
	 * @return The value, or null if an exception was thrown.
	 */
	public final Object get(Object fieldOwner) {
		init();
		Object retVal = null;
		try {
			retVal = field.get(fieldOwner);
		} catch (IllegalAccessException e) {
			logException(log, e, "Illegal access to field; cannot get: " + fieldOwner.getClass().getName() + '#' + field.getName());
		}
		return retVal;
	}
}
