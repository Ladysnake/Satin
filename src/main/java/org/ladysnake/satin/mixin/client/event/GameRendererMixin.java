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
package org.ladysnake.satin.mixin.client.event;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.ladysnake.satin.api.event.PickEntityShaderCallback;
import org.ladysnake.satin.api.event.ShaderEffectRenderCallback;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

import static org.spongepowered.asm.mixin.injection.At.Shift.AFTER;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow private @Nullable Identifier postProcessorId;

    @Shadow
    protected abstract void setPostProcessor(Identifier id);

    @Shadow @Final private MinecraftClient client;

    /**
     * Fires {@link ShaderEffectRenderCallback#EVENT}
     */
    @Inject(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawEntityOutlinesFramebuffer()V", shift = AFTER),
            method = "render"
    )
    private void hookShaderRender(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        ShaderEffectRenderCallback.EVENT.invoker().renderShaderEffects(tickCounter.getTickDelta(tick));
    }

    /**
     * Fires {@link PickEntityShaderCallback#EVENT}
     * Disabled by optifine
     */
    @Inject(method = "onCameraEntitySet", at = @At(value = "RETURN"), require = 0)
    private void useCustomEntityShader(@Nullable Entity entity, CallbackInfo info) {
        if (this.postProcessorId == null) {
            // Mixin does not like method references to shadowed methods
            //noinspection Convert2MethodRef
            PickEntityShaderCallback.EVENT.invoker().pickEntityShader(
                    entity,
                    loc -> this.setPostProcessor(loc),
                    () -> this.client.getShaderLoader().loadPostEffect(this.postProcessorId, DefaultFramebufferSet.MAIN_ONLY)
            );
        }
    }
}
