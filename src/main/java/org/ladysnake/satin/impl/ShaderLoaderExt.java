package org.ladysnake.satin.impl;

import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.util.Identifier;

import java.util.Set;

public interface ShaderLoaderExt {
    PostEffectProcessor satin$loadUnchecked(Identifier id, Set<Identifier> availableExternalTargets) throws ShaderLoader.LoadException;
}
