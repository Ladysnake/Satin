/*
 * Satin
 * Copyright (C) 2019-2024 Ladysnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; If not, see <https://www.gnu.org/licenses>.
 */
package org.ladysnake.satin.mixin.client.gl;

import net.minecraft.client.gl.PostEffectPipeline;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.ladysnake.satin.impl.CustomFormatFramebuffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(PostEffectProcessor.class)
public class CustomFormatPostEffectProcessorMixin {
    private @Unique Map<Identifier, CustomFormatFramebuffers.TextureFormat> targetFormats;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void satin$loadTargetFormats(List passes, Map internalTargets, Set externalTargets, CallbackInfo ci) {
        this.targetFormats = CustomFormatFramebuffers.getFormatsForProcessor();
    }

    @Inject(method = "render(Lnet/minecraft/client/render/FrameGraphBuilder;IILnet/minecraft/client/gl/PostEffectProcessor$FramebufferSet;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 1),
    locals = LocalCapture.CAPTURE_FAILHARD)
    private void satin$setCustomTargetFormatForFramebufferFactory(FrameGraphBuilder builder, int textureWidth, int textureHeight, PostEffectProcessor.FramebufferSet framebufferSet, CallbackInfo ci, Matrix4f matrix4f, Map map, Iterator var7, Map.Entry<Identifier, PostEffectPipeline.Targets> entry, Identifier identifier2, SimpleFramebufferFactory simpleFramebufferFactory) {
        CustomFormatFramebuffers.setCustomFormatForFactory(simpleFramebufferFactory, this.targetFormats.get(identifier2));
    }
}
