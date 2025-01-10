package org.ladysnake.satin.mixin.client.gl;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import org.ladysnake.satin.impl.CustomFormatFramebuffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleFramebufferFactory.class)
public abstract class CustomFormatSimpleFramebufferFactoryMixin {
    @Inject(method = "create()Lnet/minecraft/client/gl/Framebuffer;", at = @At("HEAD"))
    private void satin$prepareCustomFormat(CallbackInfoReturnable<Framebuffer> cir) {
        CustomFormatFramebuffers.prepareCustomFormatForFramebufferInit((SimpleFramebufferFactory) (Object) this);
    }
}
