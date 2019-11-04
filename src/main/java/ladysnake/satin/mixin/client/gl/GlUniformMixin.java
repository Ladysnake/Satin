package ladysnake.satin.mixin.client.gl;

import net.minecraft.client.gl.GlUniform;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.spongepowered.asm.mixin.injection.At.Shift.AFTER;

@Mixin(GlUniform.class)
public abstract class GlUniformMixin {
    @Shadow @Final private IntBuffer intData;

    @Redirect(method = "uploadInts", at = @At(value = "INVOKE", target = "Ljava/nio/FloatBuffer;clear()Ljava/nio/Buffer;", remap = false))
    private Buffer fixIntUniforms(FloatBuffer floatBuffer) {
        this.intData.clear();
        return floatBuffer;
    }

    @Inject(method = "upload", at = @At(value = "JUMP", opcode = Opcodes.IFNE, ordinal = 0, shift = AFTER), cancellable = true)
    private void fixUploadEarlyReturn(CallbackInfo ci) {
        ci.cancel();
    }
}
