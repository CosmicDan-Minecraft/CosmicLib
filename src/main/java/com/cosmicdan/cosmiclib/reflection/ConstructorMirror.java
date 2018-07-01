package com.cosmicdan.cosmiclib.reflection;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Mirror for instantiating classes with private constructors
 */
@Log4j2(topic = "CosmicLib/ConstructorMirror")
public class ConstructorMirror extends MirrorBase {
	private final String className;
	private final Class<?>[] parameterTypes;

	private ConstructorAccess<?> constructorAccess = null;
	private Constructor<?> constructorSlow = null;

	/**
	 * @param className
	 * @param parameterTypes Passing null will enable the use of faster ReflectASM constructor
	 */
	public ConstructorMirror(String className, Class<?>... parameterTypes) {
		this.className = className;
		this.parameterTypes = parameterTypes.clone();
	}

	@Override
	public final void init() {
		if ((null == constructorAccess) && (null == constructorSlow)) {
			try {
				// always get a legacy reference to the constructor instance
				constructorSlow = Class.forName(className).getDeclaredConstructor(parameterTypes);
				constructorSlow.setAccessible(true);
				if (null == parameterTypes) {
					// no parameter types specified - cache a fast ReflectASM version!
					constructorAccess = ConstructorAccess.get(Class.forName(className));
				}
			} catch (ClassNotFoundException e) {
				throwError(log, e, "Class not found: " + className);
			} catch (NoSuchMethodException e) {
				throwError(log, e, "Constructor not found with parameters: " + Arrays.toString(parameterTypes));
			}
		}
	}

	/**
	 * Instantiate this class with the provided outer-class object and parameters.
	 * @param classOwner The outer class owner object instance. Just use null if constructing an outer or nested class.
	 * @param classParams The parameters to pass to the constructor, if any; null if none.
	 * @return The newly constructed instance.
	 */
	public final Object construct(Object classOwner, Object... classParams) {
		init();
		Object retVal = null;
		try {
			// faster RefelctASM mode
			if (null == classOwner) {
				retVal = ((null == classParams) ?
						constructorAccess.newInstance() :
						constructorSlow.newInstance(classParams)
				);
			} else {
				retVal = ((null == classParams) ?
						constructorAccess.newInstance(classOwner) :
						constructorSlow.newInstance(classOwner, classParams)
				);
			}
		} catch (InstantiationException e) {
			throwError(log, e, "Failed instantiating class (might be an abstract class?): " + constructorSlow.getName() + " (" + Arrays.toString(classParams) + ')');
		} catch (IllegalAccessException e) {
			throwError(log, e, "Illegal access to constructor, cannot instantiate: " + constructorSlow.getName() + " (" + Arrays.toString(classParams) + ')');
		} catch (InvocationTargetException e) {
			throwError(log, e, "Instantiated class threw an exception: " + constructorSlow.getName() + " (" + Arrays.toString(classParams) + ')');
		} catch (IllegalArgumentException e) {
			throwError(log, e, "Instantiated class with wrong number of arguments: " + constructorSlow.getName() + " (" + Arrays.toString(classParams) + ')');
		}

		return retVal;
	}
}
