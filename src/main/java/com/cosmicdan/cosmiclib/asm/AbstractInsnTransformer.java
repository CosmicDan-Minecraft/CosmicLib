package com.cosmicdan.cosmiclib.asm;

import lombok.extern.log4j.Log4j2;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author Daniel 'CosmicDan' Connolly
 */
@Log4j2(topic = "CosmicLib/AbstractInsnTransformer")
public abstract class AbstractInsnTransformer<T extends AbstractInsnNode> extends AbstractTransformerBase  {
	private String targetPatchedEntryForLog = "";

	/**
	 * Do your search logic for the desired AbstractInsnNode you want to inject here.
	 * @param methodNode is the matched MethodNode you can search through
	 * @return the found AbstractInsnNode you want to inject from (as origin). It will also be passed to doNodeInjection.
	 */
	protected abstract T getTargetNode(MethodNode methodNode);

	@SuppressWarnings("SameReturnValue")
	protected abstract boolean shouldInjectOpsBeforeNode();

	/**
	 * Perform the opcode injections here.
	 * @param targetNode the origin node we're injecting on (from #getTargetNode)
	 * @return the modified toInject.
	 */
	protected abstract InsnList doNodeInjection(T targetNode);

	@Override
	byte[] patchClass(final byte[] classBytes) {
		ClassNode classNode = new ClassNode();
		final ClassReader classReader = new ClassReader(classBytes);
		classReader.accept(classNode, 0);
		boolean success = false;

		if (doesModifyClassNode()) {
			classNode = doClassInjection(classNode);
			success = true;
		}

		if ((null != getTargetMethod()) && (null != getTargetDesc())) {
			success = false;
			for (final MethodNode methodNode : classNode.methods) {
				if ((methodNode.name.equals(getTargetMethod()) && methodNode.desc.equals(getTargetDesc()))) {
					final T targetNode = getTargetNode(methodNode);
					final InsnList toInject = doNodeInjection(targetNode);
					if (shouldInjectOpsBeforeNode())
						methodNode.instructions.insertBefore(targetNode, toInject);
					else
						methodNode.instructions.insert(targetNode, toInject);
					success = true;
					targetPatchedEntryForLog = getTargetClass() + '#' + getTargetMethod(); // for log output purposes only
					break;
				}
			}
		}

		if (success) {
			log.info("[i] Patched {}", targetPatchedEntryForLog);
			log.info("    Reason: {}", getReason());
		} // else log error? Nah, silently fail I guess...
		
		final ClassWriter writer = new CoremodClassWriter();
		classNode.accept(writer);
		return writer.toByteArray();
	}

	/**
	 * ASM in Forge has it's own Classloader which is apparently unaware of Minecraft classes, so we need to use the current Classloader
	 * (which is probably FML). COMPUTE_MAX is one alternative to this but that often fails with stackmap errors in Java 1.7+.
	 *
	 * I (CosmicDan) copied most of this from ClassWriter; the only real change is getting the classloader on construction instead of
	 * leaving it up to the class visitor to get it's own.
	 */
	private static final class CoremodClassWriter extends ClassWriter {
		private final ClassLoader classLoader;

		private CoremodClassWriter() {
			super(ClassWriter.COMPUTE_FRAMES);
			classLoader = getClass().getClassLoader();
		}

		@SuppressWarnings("MethodWithMultipleReturnPoints")
		@Override
		protected String getCommonSuperClass(final String type1, final String type2) {
			Class<?> class1;
			final Class<?> class2;
			try {
				class1 = Class.forName(type1.replace('/', '.'), false, classLoader);
				class2 = Class.forName(type2.replace('/', '.'), false, classLoader);
			} catch (final ClassNotFoundException exception) {
				throw new RuntimeException(exception);
			}

			if (class1.isAssignableFrom(class2)) {
				return type1;
			}
			if (class2.isAssignableFrom(class1)) {
				return type2;
			}
			if (class1.isInterface() || class2.isInterface()) {
				return "java/lang/Object";
			} else {
				do {
					class1 = class1.getSuperclass();
				} while (!class1.isAssignableFrom(class2));
				return class1.getName().replace('.', '/');
			}
		}
	}
}