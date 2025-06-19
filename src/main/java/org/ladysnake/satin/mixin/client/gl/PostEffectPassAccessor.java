package org.ladysnake.satin.mixin.client.gl;

import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PostEffectPass.class)
public interface PostEffectPassAccessor {
    @Accessor
    List<PostEffectPipeline.Uniform> getUniforms();
}
