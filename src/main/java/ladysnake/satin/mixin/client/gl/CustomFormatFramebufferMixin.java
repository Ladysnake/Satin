/*
 * Satin
 * Copyright (C) 2019-2023 Ladysnake
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
package ladysnake.satin.mixin.client.gl;

import com.mojang.blaze3d.platform.GlConst;
import ladysnake.satin.impl.CustomFormatFramebuffers;
import net.minecraft.client.gl.Framebuffer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Allows framebuffers to have custom formats. Default is still GL_RGBA8.
 * @see CustomFormatFramebuffers
 */
@Mixin(Framebuffer.class)
public abstract class CustomFormatFramebufferMixin {
    @Unique
    private int satin$format = GlConst.GL_RGBA8;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void satin$setFormat(boolean useDepth, CallbackInfo ci) {
        @Nullable CustomFormatFramebuffers.TextureFormat format = CustomFormatFramebuffers.getCustomFormat();
        if (format != null) {
            this.satin$format = format.value;
            CustomFormatFramebuffers.clearCustomFormat();
        }
    }

    @ModifyArg(
            method = "initFbo",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/Framebuffer;setTexFilter(I)V"),
                    to = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_glBindFramebuffer(II)V")
            ),
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V"),
            index = 2
    )
    private int satin$modifyInternalFormat(int internalFormat) {
        return this.satin$format;
    }
}
