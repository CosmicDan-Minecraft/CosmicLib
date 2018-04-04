package com.cosmicdan.cosmiclib.reflection;

import com.cosmicdan.cosmiclib.obfuscation.ObfuscatedString;
import com.esotericsoftware.reflectasm.FieldAccess;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Field;

@Log4j2(topic = "CosmicLib/FieldMirror")
public class FieldMirror extends MirrorBase {
	private final ObfuscatedString fieldName;
	private final Class<?> fieldClass;

	private FieldAccess fieldAccess = null;
	private int fieldIndex;

	/**
	 * Must be public - use AT if necessary!
	 * @param fieldName
	 * @param fieldClass
	 */
	public FieldMirror(ObfuscatedString fieldName, Class<?> fieldClass) {
		this.fieldName = fieldName;
		this.fieldClass = fieldClass;
	}

	@Override
	public final void init() {
		if (null == fieldAccess) {
			fieldAccess = FieldAccess.get(fieldClass);
			fieldIndex = fieldAccess.getIndex(fieldName.get());
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
		fieldAccess.set(fieldOwner, fieldIndex, fieldValue);
		return true;
	}

	/**
	 * Get this field value
	 * @param fieldOwner The object instance that owns this field
	 * @return The value, or null if an exception was thrown.
	 */
	public final Object get(Object fieldOwner) {
		init();
		return fieldAccess.get(fieldOwner, fieldIndex);
	}
}
