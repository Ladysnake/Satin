package ladysnake.satin.mixin.client.gl;

import net.minecraft.client.gl.JsonGlProgram;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(JsonGlProgram.class)
public interface JsonGlProgramAccessor {
    @Accessor
    Map<String, Object> getSamplerBinds();

    @Accessor
    List<String> getSamplerNames();

    @Accessor
    List<Integer> getSamplerShaderLocs();
}
