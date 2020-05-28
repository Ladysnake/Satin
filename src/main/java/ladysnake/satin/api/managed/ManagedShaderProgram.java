package ladysnake.satin.api.managed;

import ladysnake.satin.api.experimental.managed.UniformFinder;
import net.minecraft.client.gl.JsonGlProgram;
import org.apiguardian.api.API;

@API(status = API.Status.EXPERIMENTAL)
public interface ManagedShaderProgram extends UniformFinder {
    JsonGlProgram getProgram();

    void enable();

    void disable();

    void release();
}
