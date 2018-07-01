package com.cosmicdan.cosmiclib.asm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

import lombok.extern.log4j.Log4j2;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

/**
 * @author Daniel 'CosmicDan' Connolly
 */
@Log4j2(topic = "CosmicLib/AbstractInsnTransformer")
public abstract class AbstractInsnTransformer<T extends AbstractInsnNode> implements IClassTransformer  {
	private String targetClass;
	private final String targetMethod;
	private final String targetDesc;
	private final String hookReason;

	public AbstractInsnTransformer() {
		targetClass = getTargetClass();
		targetMethod = getTargetMethod();
		targetDesc = getTargetDesc();
		hookReason = getReason();
	}

	public abstract String getTargetClass();
	public abstract String getTargetMethod();
	public abstract String getTargetDesc();
	public abstract String getReason();

	public abstract boolean doesModifyClassNode();
	public abstract ClassNode injectClass(ClassNode toInject);

	/**
	 * Do your search logic for the desired AbstractInsnNode you want to inject here.
	 * @param m is the matched MethodNode you can search through
	 * @return the found AbstractInsnNode you want to inject from (as origin). It will also be passed to injectOps.
	 */
	public abstract T getTargetNode(MethodNode m);

	public abstract boolean shouldInjectOpsBeforeNode();

	/**
	 * Perform the opcode injections here.
	 * @param targetNode the origin node we're injecting on (from #getTargetNode)
	 * @return the modified toInject.
	 */
	public abstract InsnList injectOps(T targetNode);


	@Override
	public byte[] transform(String name, String transformedName, byte[] classBytes) {
		if (transformedName.equals(targetClass))
			return patchClass(classBytes);
		return classBytes;
	}

	private byte[] patchClass(byte[] classBytes) {
		ClassNode classNode = new ClassNode();
		final ClassReader classReader = new ClassReader(classBytes);
		classReader.accept(classNode, 0);
		boolean success = false;

		if (doesModifyClassNode()) {
			classNode = injectClass(classNode);
			success = true;
		}

		if ((null != targetMethod) && (null != targetDesc)) {
			success = false;
			final Iterator<MethodNode> methods = classNode.methods.iterator();
			while(methods.hasNext()) {
				final MethodNode m = methods.next();
				if ((m.name.equals(targetMethod) && m.desc.equals(targetDesc))) {
					// DEBUG
					//dumpMethodAsmToText(m, "E:/TEMP/before.txt");

					final T targetNode = getTargetNode(m);
					final InsnList toInject = injectOps(targetNode);
					if (shouldInjectOpsBeforeNode())
						m.instructions.insertBefore(targetNode, toInject);
					else
						m.instructions.insert(targetNode, toInject);
					success = true;
					targetClass = targetClass + "#" + targetMethod; // for log output purposes only
					break;

					// DEBUG
					//dumpMethodAsmToText(m, "E:/TEMP/after.txt");
				}
			}
		}

		if (success) {
			log.info("[i] Patched " + targetClass);
			log.info("    Reason: " + hookReason);
		} // else log error? Nah, silently fail I guess...

		//ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		return writer.toByteArray();
	}

	// ASM debug dumping
	// Thanks to HXSP1947@StackOverflow
	private static final Printer printer = new Textifier();
	private static final TraceMethodVisitor mp = new TraceMethodVisitor(printer);

	public static void dumpMethodAsmToText(MethodNode m, String outputTextFile) {
		try {
			final PrintWriter outputFile = new PrintWriter(outputTextFile);
			for (final AbstractInsnNode node : m.instructions.toArray()) {
				outputFile.append(insnToString(node));
			}
			outputFile.flush();
		} catch (FileNotFoundException e) {}
	}

	private static String insnToString(AbstractInsnNode insn){
		insn.accept(mp);
		final StringWriter sw = new StringWriter();
		printer.print(new PrintWriter(sw));
		printer.getText().clear();
		return sw.toString();
	}
}