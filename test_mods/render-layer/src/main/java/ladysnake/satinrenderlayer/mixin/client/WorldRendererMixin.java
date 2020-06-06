package ladysnake.satinrenderlayer.mixin.client;

import ladysnake.satinrenderlayer.SatinRenderLayer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class WorldRendererMixin {
    @Inject(method = "loadProjectionMatrix", at = @At("HEAD"))
    private void updateProjMat(Matrix4f matrix4f, CallbackInfo ci) {
        SatinRenderLayer.rainbowProjMat.set(matrix4f);
    }
}
