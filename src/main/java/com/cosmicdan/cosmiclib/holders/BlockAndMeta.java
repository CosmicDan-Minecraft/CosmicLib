package com.cosmicdan.cosmiclib.holders;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

@AllArgsConstructor
public class BlockAndMeta {
	public final Block block;
	public final int meta;

	public IBlockState getAsState() {
		return block.getStateFromMeta(meta);
	}
}
