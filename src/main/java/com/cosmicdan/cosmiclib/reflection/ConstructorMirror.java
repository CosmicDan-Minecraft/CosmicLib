package com.cosmicdan.cosmiclib.reflection;

import lombok.extern.log4j.Log4j2;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Mirror for instantiating classes with non-public constructors
 */
@Log4j2(topic = "CosmicLib/ConstructorMirror")
public class ConstructorMirror extends MirrorBase {
	// TODO: Convert to Method Handle? See https://www.baeldung.com/java-method-handles
	private final String className;
	private final Class<?>[] parameterTypes;

	private Constructor<?> constructor = null;

	public ConstructorMirror(final String className, final Class<?>... parameterTypes) {
		this.className = className;
		this.parameterTypes = parameterTypes.clone();
	}

	@Override
	public final void init() {
		if (null == constructor) {
			try {
				constructor = ReflectionHelper.findConstructor(Class.forName(className), parameterTypes);
			} catch (final ClassNotFoundException exception) {
				throwError(log, exception, "Class not found: " + className);
			}
		}
	}

	/**
	 * Instantiate this class with the provided outer-class object and parameters.
	 * @param classOwner The outer class owner object instance. Just use null if constructing an outer or nested class.
	 * @param classParams The parameters to pass to the constructor, if any; null if none.
	 * @return The newly constructed instance.
	 */
	@SuppressWarnings("unused")
	public final Object construct(final Object classOwner, final Object... classParams) {
		init();
		Object retVal = null;
		try {
			if (null == classOwner)
				retVal = constructor.newInstance(classParams);
			else
				retVal = constructor.newInstance(classOwner, classParams);
		} catch (final InstantiationException exception) {
			throwError(log, exception, "Failed instantiating class (might be an abstract class?): " + constructor.getName() + " (" + Arrays.toString(classParams) + ')');
		} catch (final IllegalAccessException exception) {
			throwError(log, exception, "Illegal access to constructor, cannot instantiate: " + constructor.getName() + " (" + Arrays.toString(classParams) + ')');
		} catch (final InvocationTargetException exception) {
			throwError(log, exception, "Instantiated class threw an exception: " + constructor.getName() + " (" + Arrays.toString(classParams) + ')');
		} catch (final IllegalArgumentException exception) {
			throwError(log, exception, "Instantiated class with wrong number of arguments: " + constructor.getName() + " (" + Arrays.toString(classParams) + ')');
		}

		return retVal;
	}
}
