package org.ladysnake.satin.mixin.client.event;

import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import org.ladysnake.satin.impl.ReloadableShaderEffectManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShaderLoader.class)
public class ShaderLoaderMixin {
    @Inject(
            method = "apply(Lnet/minecraft/client/gl/ShaderLoader$Definitions;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V",
            at = @At(value = "RETURN")
    )
    private void loadSatinPrograms(ShaderLoader.Definitions definitions, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        ReloadableShaderEffectManager.INSTANCE.reload(resourceManager);
    }
}