package ladysnake.satin.mixin.client.gl;

import net.minecraft.client.gl.PostProcessShader;
import net.minecraft.client.gl.ShaderEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

/**
 * The ShaderEffect class does not have a getter for its passes, so this mixin does the job until Access Transformers
 * are a thing.
 */
@Mixin(ShaderEffect.class)
public interface AccessiblePassesShaderEffect {
    @Accessor
    List<PostProcessShader> getPasses();
}
