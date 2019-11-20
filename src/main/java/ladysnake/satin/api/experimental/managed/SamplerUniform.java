package ladysnake.satin.api.experimental.managed;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.AbstractTexture;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

public interface SamplerUniform {
    /**
     * Sets the value of a sampler uniform declared in json
     *
     * @param texture     a texture object
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    void set(AbstractTexture texture);

    /**
     * Sets the value of a sampler uniform declared in json
     *
     * @param textureFbo  a framebuffer which main texture will be used
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    void set(Framebuffer textureFbo);

    /**
     * Sets the value of a sampler uniform declared in json
     *
     * @param textureName an opengl texture name
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    void set(int textureName);
}
