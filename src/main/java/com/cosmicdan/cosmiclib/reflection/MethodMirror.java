package com.cosmicdan.cosmiclib.reflection;

import com.cosmicdan.cosmiclib.obfuscation.ObfuscatedString;
import com.esotericsoftware.reflectasm.MethodAccess;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

@Log4j2(topic = "CosmicLib/MethodMirror")
public class MethodMirror extends MirrorBase {
	private final ObfuscatedString methodName;
	private final Class<?> methodClass;
	private final Class<?>[] parameterTypes;

	private MethodAccess methodAccess = null;
	private int methodIndex;

	/**
	 * Must be public - use AT if necessary!
	 * @param methodName
	 * @param methodClass
	 * @param parameterTypes
	 */
	public MethodMirror(ObfuscatedString methodName, Class<?> methodClass, Class<?>... parameterTypes) {
		this.methodName = methodName;
		this.methodClass = methodClass;
		this.parameterTypes = parameterTypes;
	}

	@Override
	public final void init() {
		if (null == methodAccess) {
			try {
				methodAccess = MethodAccess.get(methodClass);
				methodIndex = methodAccess.getIndex(methodName.get(), parameterTypes);
			} catch (RuntimeException exception) {
				throwError(log, exception, "Could find method: " + methodClass.getName() + '#' + methodName + '(' + parameterTypes.toString() + ')');
			}
		}
	}

	/**
	 * Invoke the method with the provided owner object and parameters.
	 * @param methodOwner The object instance that owns this method.
	 * @param methodParams The parameters to pass to the method.
	 * @return The return object of the method (or boxed primitive), if applicable. Null if void or an error occurred.
	 */
	public final Object call(Object methodOwner, Object... methodParams) {
		init();
		return methodAccess.invoke(methodOwner, methodIndex, methodParams);
	}
}
