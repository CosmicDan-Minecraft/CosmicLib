package com.cosmicdan.cosmiclib.archetypes;

import com.cosmicdan.cosmiclib.Main;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;

/**
 * Base class for mod blocks.
 * Do NOT register this directly to the Forge event bus - use {@link ModRegistrable#register} instead.
 * @author Daniel 'CosmicDan' Connolly
 */
public abstract class ModBlockBase extends Block implements ModRegistrable {
	private final String name;

	protected ModBlockBase(Material material, String name) {
		super(material);
		this.name = name;
	}

	@Override
	public void init() {
		setUnlocalizedName(name);
		setRegistryName(name);
		setHardness(getHardness());
		setResistance(getResistance());
	}

	public abstract float getHardness();
	public abstract float getResistance();

	public void registerItemModel(String modid, Item itemBlock) {
		Main.PROXY.registerItemInventoryRenderer(modid, itemBlock, 0, name);
	}

	public Item createItemBlock() {
		return new ItemBlock(this).setRegistryName(name);
	}
}
