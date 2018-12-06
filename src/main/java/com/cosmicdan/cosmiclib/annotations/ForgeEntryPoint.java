package com.cosmicdan.cosmiclib.annotations;

/**
 * This annotation is used to mark a class or member as a Forge Entry Point. This might include:
 * - Classes instantiated by FML (e.g. Proxies);
 * - Methods called by Forge itself (e.g. Events);
 * - Static fields that Forge sets itself (e.g. INSTANCE);
 * - Methods called from ASM injections;
 * - Overridden functionality in dynamic sub-classes that IntelliJ is ignorant about
 */
@SuppressWarnings("ALL")
public @interface ForgeEntryPoint {}
