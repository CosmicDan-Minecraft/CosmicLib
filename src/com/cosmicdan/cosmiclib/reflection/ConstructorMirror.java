package com.cosmicdan.cosmiclib.reflection;

import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Mirror for instantiating classes with private constructors
 */
@Log4j2(topic =  "CosmicLib/ConstructorMirror")
public class ConstructorMirror extends MirrorBase {
	private final String className;
	private final Class<?>[] parameterTypes;

	private Constructor<?> constructor = null;

	public ConstructorMirror(String className, Class<?>... parameterTypes) {
		this.className = className;
		this.parameterTypes = parameterTypes;
	}

	@Override
	public final void init() {
		if (null == constructor) {
			try {
				constructor = Class.forName(className).getDeclaredConstructor(parameterTypes);
				constructor.setAccessible(true);
			} catch (ClassNotFoundException e) {
				logException(log, e, "Class not found: " + className);
			} catch (NoSuchMethodException e) {
				logException(log, e, "Constructor not found with parameters: " + Arrays.toString(parameterTypes));
			}
		}
	}

	/**
	 * Instantiate this class with the provided outer-class object and parameters.
	 * @param classOwner The outer class owner object instance. Just use null if constructing an outer or nested class.
	 * @param classParams The parameters to pass to the constructor.
	 * @return The newly constructed instance.
	 */
	public final Object construct(Object classOwner, Object... classParams) {
		init();
		Object retVal = null;
		try {
			if (null == classOwner)
				retVal = constructor.newInstance(classParams);
			else
				retVal = constructor.newInstance(classOwner, classParams);
		} catch (InstantiationException e) {
			logException(log, e, "Failed instantiating class (might be an abstract class?): " + constructor.getName() + " (" + Arrays.toString(classParams) + ')');
		} catch (IllegalAccessException e) {
			logException(log, e, "Illegal access to constructor, cannot instantiate: " + constructor.getName() + " (" + Arrays.toString(classParams) + ')');
		} catch (InvocationTargetException e) {
			logException(log, e, "Instantiated class threw an exception: " + constructor.getName() + " (" + Arrays.toString(classParams) + ')');
		} catch (IllegalArgumentException e) {
			logException(log, e, "Instantiated class with wrong number of arguments: " + constructor.getName() + " (" + Arrays.toString(classParams) + ')');
		}

		return retVal;
	}
}
