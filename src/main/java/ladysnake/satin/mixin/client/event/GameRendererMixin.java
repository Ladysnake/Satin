package ladysnake.satin.mixin.client.event;

import ladysnake.satin.api.event.PickEntityShaderCallback;
import ladysnake.satin.api.event.PostWorldRenderCallback;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satin.api.experimental.ReadableDepthFramebuffer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
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

    @Shadow private ShaderEffect shader;

    @Shadow protected abstract void loadShader(Identifier location);

    @Shadow @Final private Camera camera;

    /**
     * Fires {@link ShaderEffectRenderCallback#EVENT}
     */
    @Inject(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;drawEntityOutlinesFramebuffer()V", shift = AFTER),
            method = "render"
    )
    private void hookShaderRender(float tickDelta, long nanoTime, boolean renderLevel, CallbackInfo info) {
        ShaderEffectRenderCallback.EVENT.invoker().renderShaderEffects(tickDelta);
    }

    @Inject(method = "renderCenter", at = @At(value = "CONSTANT", args = "stringValue=hand"))
    private void hookPostWorldRender(float tickDelta, long nanoTime, CallbackInfo ci) {
        ((ReadableDepthFramebuffer)MinecraftClient.getInstance().getFramebuffer()).freezeDepthMap();
        PostWorldRenderCallback.EVENT.invoker().onWorldRendered(this.camera, tickDelta, nanoTime);
    }

    /**
     * Fires {@link PickEntityShaderCallback#EVENT}
     * Disabled by optifine
     */
    @Inject(method = "onCameraEntitySet", at = @At(value = "RETURN", ordinal = 1), require = 0)
    private void useCustomEntityShader(@Nullable Entity entity, CallbackInfo info) {
        if (this.shader == null) {
            // Mixin does not like method references to shadowed methods
            //noinspection Convert2MethodRef
            PickEntityShaderCallback.EVENT.invoker().pickEntityShader(entity, loc -> this.loadShader(loc), () -> this.shader);
        }
    }
}
