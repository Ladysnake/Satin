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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ladysnake.satin.impl.CustomFormatFramebuffers;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PostEffectProcessor.class)
public class CustomFormatPostEffectProcessorMixin {

    @Inject(method = "parseTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/PostEffectProcessor;addTarget(Ljava/lang/String;II)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void satin$parseCustomTargetFormat(JsonElement jsonTarget, CallbackInfo ci, JsonObject jsonObject) {
        String format = JsonHelper.getString(jsonObject, CustomFormatFramebuffers.FORMAT_KEY, null);
        if (format != null) {
            CustomFormatFramebuffers.prepareCustomFormat(format);
        }
    }

    /**
     * @reason need to clean up state if an exception is thrown
     */
    @Inject(
            method = "parseEffect",
            slice = @Slice(
                    from = @At(value = "CONSTANT:FIRST", args = "stringValue=targets"),
                    to = @At(value = "CONSTANT:FIRST", args = "stringValue=passes")
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/InvalidHierarchicalFileException;wrap(Ljava/lang/Exception;)Lnet/minecraft/util/InvalidHierarchicalFileException;"
            ),
            allow = 1
    )
    private void satin$cleanupCustomTargetFormat(TextureManager textureManager, Identifier id, CallbackInfo ci) {
        CustomFormatFramebuffers.clearCustomFormat();
    }
}
