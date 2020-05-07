package ladysnake.satin.api.experimental.managed;

import net.minecraft.util.math.Matrix4f;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

public interface UniformMat4 {

    /**
     * Sets the value of a 4x4 matrix uniform
     *
     * @param value a matrix
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    void set(Matrix4f value);

}
