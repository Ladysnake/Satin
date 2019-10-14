package ladysnake.satin.mixin.client.gl;

import net.minecraft.client.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Matrix4f.class)
public interface Matrix4FAccessor {
    @Accessor
    float[] getComponents();
}
