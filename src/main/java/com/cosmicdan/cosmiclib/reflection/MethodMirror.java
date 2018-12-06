package com.cosmicdan.cosmiclib.reflection;

import com.cosmicdan.cosmiclib.obfuscation.ObfuscatedString;
import lombok.extern.log4j.Log4j2;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Method;
import java.util.Arrays;

@SuppressWarnings("unused")
@Log4j2(topic = "CosmicLib/MethodMirror")
public class MethodMirror<T> extends MirrorBase {
	// TODO: Convert to Method Handle? See https://www.baeldung.com/java-method-handles
	private final ObfuscatedString methodName;
	private final Class<?> methodOwner;
	private final Class<?>[] parameterTypes;

	private Method methodAccess = null;

	public MethodMirror(final ObfuscatedString methodName, final Class<?> methodClass, final Class<?>... parameterTypes) {
		this.methodName = methodName;
		this.methodOwner = methodClass;
		this.parameterTypes = parameterTypes.clone();
	}

	@Override
	public final void init() {
		if (null == methodAccess) {
			try {
				methodAccess = ReflectionHelper.findMethod(methodOwner, methodName.getDev(), methodName.getObf(), parameterTypes);
			} catch (final ReflectionHelper.UnableToFindMethodException exception) {
				throwError(log, exception, "Error finding method: " + methodOwner.getName() + '#' + methodName + '(' + Arrays.toString(parameterTypes) + ')');
			}
		}
	}

	/**
	 * Invoke the method with the provided owner object and parameters.
	 * @param methodInstance The object instance that owns this method.
	 * @param methodParams The parameters to pass to the method.
	 * @return The return object of the method (or boxed primitive), if applicable. Null if void or an error occurred.
	 */
	public final T call(final Object methodInstance, final Object... methodParams) {
		init();
		T retVal = null;
		//noinspection OverlyBroadCatchBlock
		try {
			//noinspection unchecked
			retVal = (T) methodAccess.invoke(methodInstance, methodParams);
		} catch (final Exception exception) {
			throwError(log, exception, "Error invoking method: " + methodOwner.getName() + '#' + methodName + '(' + Arrays.toString(parameterTypes) + ')');
		}
		return retVal;
	}
}
