package com.cosmicdan.cosmiclib.archetypes;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * ModRegistrable is an interfance and delegator to help safely initialize things before registration
 * @author Daniel 'CosmicDan' Connolly
 */
public interface ModRegistrable {
	/**
	 * Perform post-construcion initialization here.
	 */
	void init();

	Block setCreativeTab(CreativeTabs tab);

	static void register(RegistryEvent.Register event, ModRegistrable... modRegistrables) {
		if (!(modRegistrables instanceof IForgeRegistryEntry[])) {
			throw new RuntimeException("Attempted to register something that doesn't implement IForgeRegistryEntry");
		}
		for (final ModRegistrable modRegistrable : modRegistrables) {
			modRegistrable.init();
		}
		//noinspection unchecked,rawtypes
		event.getRegistry().registerAll((IForgeRegistryEntry[]) modRegistrables);
	}
}
