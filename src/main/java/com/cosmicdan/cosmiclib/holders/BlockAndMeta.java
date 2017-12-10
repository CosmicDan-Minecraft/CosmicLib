package com.cosmicdan.cosmiclib.holders;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.block.Block;

@AllArgsConstructor
public class BlockAndMeta {
	public final Block block;
	public final int meta;
}
