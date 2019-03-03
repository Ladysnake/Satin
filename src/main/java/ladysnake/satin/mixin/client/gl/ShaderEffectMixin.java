package ladysnake.satin.mixin.client.gl;

import ladysnake.satin.impl.AccessiblePassesShaderEffect;
import net.minecraft.client.gl.PostProcessShader;
import net.minecraft.client.gl.ShaderEffect;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ShaderEffect.class)
public abstract class ShaderEffectMixin implements AccessiblePassesShaderEffect {

    @Shadow @Final private List<PostProcessShader> passes;

    @Override
    public List<PostProcessShader> satin$getPasses() {
        return this.passes;
    }
}
