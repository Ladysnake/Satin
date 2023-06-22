package ladysnake.satin.mixin.client.gl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ladysnake.satin.impl.CustomFormatFramebuffer;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;

@Mixin(PostEffectProcessor.class)
public class PostEffectProcessorMixin {
    @Shadow @Final private Map<String, Framebuffer> targetsByName;

    @Shadow private int width;

    @Shadow private int height;

    @Shadow @Final private List<Framebuffer> defaultSizedTargets;

    @Inject(method = "parseTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/PostEffectProcessor;addTarget(Ljava/lang/String;II)V", ordinal = 1), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void satin$parseCustomTargetFormat(JsonElement jsonTarget, CallbackInfo ci, JsonObject jsonObject, String name, int width, int height) {
        String format = JsonHelper.getString(jsonObject, "format", null);
        if (format != null) {
            this.satin$addTarget(name, CustomFormatFramebuffer.createFramebuffer(width, height, format));
        }
    }

    /**
     * <b>Check me when updating!</b> This duplicates logic from {@link PostEffectProcessor#addTarget(String, int, int)}, so make sure that method
     * has not changed.
     * @param name          the name of the framebuffer to add
     * @param framebuffer   the framebuffer to add
     */
    @Unique
    private void satin$addTarget(String name, Framebuffer framebuffer) {
        this.targetsByName.put(name, framebuffer);
        if (framebuffer.textureWidth == this.width && framebuffer.textureHeight == this.height) {
            this.defaultSizedTargets.add(framebuffer);
        }
    }
}
