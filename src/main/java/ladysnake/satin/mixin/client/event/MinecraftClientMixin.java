package ladysnake.satin.mixin.client.event;

import ladysnake.satin.api.event.ResolutionChangeCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow public Window window;

    @Inject(method = "onResolutionChanged", at = @At("RETURN"))
    private void hookResolutionChanged(CallbackInfo info) {
        int width = this.window.getFramebufferWidth();
        int height = this.window.getFramebufferHeight();
        ResolutionChangeCallback.EVENT.invoker().onResolutionChanged(width, height);
    }
}
