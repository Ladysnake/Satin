package ladysnake.satin.api.managed;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.Window;
import org.apiguardian.api.API;

import javax.annotation.Nullable;

@API(status = API.Status.EXPERIMENTAL, since = "1.4.0")
public interface ManagedFramebuffer {
    @Nullable
    Framebuffer getFramebuffer();

    void copyDepthFrom(Framebuffer buffer);

    /**
     * Draws this framebuffer, scaling to the default framebuffer's
     * {@linkplain Window#getFramebufferWidth() width} and {@linkplain Window#getFramebufferHeight() height}.
     */
    void draw();

    void draw(int width, int height);
}
