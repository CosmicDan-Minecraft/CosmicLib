package com.cosmicdan.cosmiclib.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.tree.ClassNode;

import javax.annotation.Nullable;

/**
 * @author Daniel 'CosmicDan' Connolly
 */
@SuppressWarnings({"SameReturnValue", "unused"})
public abstract class AbstractTransformerBase implements IClassTransformer {
	protected abstract String getTargetClass();
	protected abstract String getTargetMethod();
	protected abstract String getTargetDesc();
	protected abstract String getReason();

	protected abstract boolean doesModifyClassNode();
	@Nullable
	protected abstract ClassNode doClassInjection(ClassNode toInject);

	@SuppressWarnings("AbstractMethodWithMissingImplementations") // IntelliJ bug?
	abstract byte[] patchClass(byte[] classBytes);

	@Override
	public byte[] transform(final String name, final String transformedName, final byte[] basicClass) {
		if (transformedName.equals(getTargetClass()))
			return patchClass(basicClass);
		return basicClass;
	}

	///////////////////////////////
	// ASM debug dump stuff
	// Thanks to HXSP1947@StackOverflow
	///////////////////////////////

	/*
	public static final Printer printer = new Textifier();
	public static final TraceMethodVisitor mp = new TraceMethodVisitor(printer);

	public static void dumpMethodAsmToText(MethodNode m, String outputTextFile) {
		try {
			final PrintWriter outputFile = new PrintWriter(outputTextFile);
			for (final AbstractInsnNode node : m.instructions.toArray()) {
				outputFile.append(insnToString(node));
			}
			outputFile.flush();
		} catch (FileNotFoundException e) {}
	}

	public static String insnToString(AbstractInsnNode insn){
		insn.accept(mp);
		final StringWriter sw = new StringWriter();
		printer.print(new PrintWriter(sw));
		printer.getText().clear();
		return sw.toString();
	}
	*/
}
