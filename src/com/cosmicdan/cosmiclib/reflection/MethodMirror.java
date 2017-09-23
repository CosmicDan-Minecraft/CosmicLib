package com.cosmicdan.cosmiclib.reflection;

import com.cosmicdan.cosmiclib.obfuscation.ObfuscatedString;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

@Log4j2(topic = "CosmicLib/MethodMirror")
public class MethodMirror extends MirrorBase {
	private final ObfuscatedString methodName;
	private final Class<?> methodClass;
	private final Class<?>[] parameterTypes;

	private Method method = null;

	public MethodMirror(ObfuscatedString methodName, Class<?> methodClass, Class<?>... parameterTypes) {
		this.methodName = methodName;
		this.methodClass = methodClass;
		this.parameterTypes = parameterTypes;
	}

	@Override
	public final void init() {
		if (null == method) {
			try {
				method = methodClass.getDeclaredMethod(methodName.get(), parameterTypes);
				method.setAccessible(true);
			} catch (NoSuchMethodException e) {
				logException(log, e, "Method not found: " + methodClass.getName() + '.' + methodName.get() + " (" + Arrays.toString(parameterTypes) + ')');
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
		Object retVal = null;
		try {
			retVal = method.invoke(methodOwner, methodParams);
		} catch (IllegalAccessException e) {
			logException(log, e, "Illegal access to method, cannot invoke: " + methodOwner.getClass().getName() + '.' + method.getName());
		} catch (InvocationTargetException e) {
			logException(log, e, "Invoked method threw an exception: " + methodOwner.getClass().getName() + '.' + method.getName());
		} catch (IllegalArgumentException e) {
			logException(log, e, "Invoked method with wrong number of arguments: " + methodOwner.getClass().getName() + '.' + method.getName());
		}

		return retVal;
	}
}
