package com.cosmicdan.cosmiclib.asm;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author Daniel 'CosmicDan' Connolly
 */
public abstract class AbstractHookMethodStartTransformer extends AbstractInsnTransformer<AbstractInsnNode> {

	@Override
	public final AbstractInsnNode getTargetNode(final MethodNode methodNode) {
		return methodNode.instructions.getFirst();
	}

	@Override
	public final boolean shouldInjectOpsBeforeNode() {
		return true;
	}
}