package org.ladysnake.satin.mixin.client.gl;

import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.util.Identifier;
import org.ladysnake.satin.impl.ShaderLoaderExt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(ShaderLoader.class)
public abstract class ShaderLoaderMixin implements ShaderLoaderExt {
    @Shadow private ShaderLoader.Cache cache;

    @Override
    public PostEffectProcessor satin$loadUnchecked(Identifier id, Set<Identifier> availableExternalTargets) throws ShaderLoader.LoadException {
        return this.cache.getOrLoadProcessor(id, availableExternalTargets);
    }
}
