package com.cosmicdan.cosmiclib.archetypes;

import com.cosmicdan.cosmiclib.annotations.ForgeModLibrary;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * ModRegistrable is an interfance and delegator to help safely initialize things before registration
 * TODO: Move and adapt this to 'registry' package
 * @author Daniel 'CosmicDan' Connolly
 */
@SuppressWarnings("WeakerAccess")
@ForgeModLibrary
public interface ModRegistrable {
	/**
	 * Perform post-construcion initialization here.
	 */
	void init();

	@ForgeModLibrary
	Block setCreativeTab(CreativeTabs tab);

	@SuppressWarnings("rawtypes")
	@ForgeModLibrary
	static void register(final RegistryEvent.Register event, final ModRegistrable... modRegistrables) {
		if (!(modRegistrables instanceof IForgeRegistryEntry[])) {
			throw new RuntimeException("Attempted to register something that doesn't implement IForgeRegistryEntry");
		}
		for (final ModRegistrable modRegistrable : modRegistrables) {
			modRegistrable.init();
		}
		//noinspection unchecked
		event.getRegistry().registerAll((IForgeRegistryEntry[]) modRegistrables);
	}
}
