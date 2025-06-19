package org.ladysnake.satin.mixin.client.gl;

import net.minecraft.client.gl.PostEffectPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Optional;

@Mixin(PostEffectPipeline.Uniform.class)
public interface PostEffectUniformAccessor {
    @Accessor
    @Mutable
    void setValues(Optional<List<Float>> values);
}
