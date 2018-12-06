package com.cosmicdan.cosmiclib.annotations;

/**
 * This annotation is used to mark a class or member as a Forge Mod Transformer. This is solely for the purpose of ignoring "unused"
 * IntelliJ inspection warnings on CosmicLib-based ASM transformer classes. Functionally-identical to the ForgeModLibrary annotation
 * as far as my inspection profile is concerned - it's just more concise to have it's own.
 */
@SuppressWarnings("ALL")
public @interface ForgeCoremodTransformer {}
