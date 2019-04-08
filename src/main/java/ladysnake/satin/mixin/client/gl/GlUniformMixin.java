package ladysnake.satin.mixin.client.gl;

import net.minecraft.client.gl.GlUniform;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@Mixin(GlUniform.class)
public abstract class GlUniformMixin {
    @Shadow @Final private IntBuffer intData;

    @Redirect(method = "uploadInts", at = @At(value = "INVOKE", target = "Ljava/nio/FloatBuffer;clear()Ljava/nio/Buffer;"))
    private Buffer fixIntUniforms(FloatBuffer floatBuffer) {
        this.intData.clear();
        return floatBuffer;
    }
}
