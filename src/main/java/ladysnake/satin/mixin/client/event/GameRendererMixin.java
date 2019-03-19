package ladysnake.satin.mixin.client.event;

import ladysnake.satin.api.event.PickEntityShaderCallback;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
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

    @Shadow protected abstract void loadShader(Identifier identifier_1);

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

    /**
     * Fires {@link PickEntityShaderCallback#EVENT}
     */
    @Inject(method = "onCameraEntitySet", at = @At(value = "RETURN", ordinal = 1))
    private void useCustomEntityShader(@Nullable Entity entity, CallbackInfo info) {
        if (this.shader == null) {
            PickEntityShaderCallback.EVENT.invoker().pickEntityShader(entity, this::loadShader, () -> this.shader);
        }
    }
}
