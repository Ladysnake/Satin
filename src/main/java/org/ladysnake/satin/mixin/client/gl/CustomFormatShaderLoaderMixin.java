package org.ladysnake.satin.mixin.client.gl;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gl.PostEffectPipeline;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.ladysnake.satin.impl.CustomFormatFramebuffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Set;

@Mixin(ShaderLoader.class)
public class CustomFormatShaderLoaderMixin {
    @Inject(method = "loadPostEffect(Lnet/minecraft/util/Identifier;Lnet/minecraft/resource/Resource;Lcom/google/common/collect/ImmutableMap$Builder;)V",
    at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;parse(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;"))
    private static void satin$parseCustomTargetFormats(Identifier id, Resource resource, ImmutableMap.Builder<Identifier, PostEffectPipeline> builder, CallbackInfo ci,
                                                       @Local(ordinal = 1) Identifier targetId, @Local JsonElement jsonElement) {
        CustomFormatFramebuffers.parseTargetFormatMap(id, jsonElement);
    }

    @Mixin(targets = "net.minecraft.client.gl.ShaderLoader$Cache")
    static class CacheMixin {
        private @Unique Map<Identifier, Map<Identifier, CustomFormatFramebuffers.TextureFormat>> targetFormats;

        @Inject(method = "<init>", at = @At("RETURN"))
        private void satin$cacheCustomTargetFormats(ShaderLoader shaderLoader, ShaderLoader.Definitions definitions, CallbackInfo ci) {
            this.targetFormats = CustomFormatFramebuffers.getPreparedTargetFormatMaps();
        }

        @Inject(method = "loadProcessor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/PostEffectProcessor;parseEffect(Lnet/minecraft/client/gl/PostEffectPipeline;Lnet/minecraft/client/texture/TextureManager;Lnet/minecraft/client/gl/ShaderLoader;Ljava/util/Set;)Lnet/minecraft/client/gl/PostEffectProcessor;"))
        private void satin$prepareTargetsForProcessorMaking(Identifier id, Set<Identifier> availableExternalTargets, CallbackInfoReturnable<PostEffectProcessor> cir) {
            CustomFormatFramebuffers.prepareFormatsForProcessor(this.targetFormats.get(id));
        }
    }
}
