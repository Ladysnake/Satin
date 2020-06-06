package ladysnake.satin.api.managed;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.Window;
import org.apiguardian.api.API;

import javax.annotation.Nullable;

@API(status = API.Status.EXPERIMENTAL, since = "1.4.0")
public interface ManagedFramebuffer {
    @Nullable
    Framebuffer getFramebuffer();

    /**
     * Begins a write operation on this framebuffer.
     *
     * <p>If the operation is successful, every subsequent draw call will write to this framebuffer.
     *
     * @param updateViewport whether binding this framebuffer should call {@link com.mojang.blaze3d.platform.GlStateManager#viewport(int, int, int, int)}
     */
    void beginWrite(boolean updateViewport);

    /**
     * Copies the depth texture from another framebuffer to this framebuffer.
     *
     * @param buffer the framebuffer to copy depth from
     */
    void copyDepthFrom(Framebuffer buffer);

    /**
     * Draws this framebuffer, scaling to the default framebuffer's
     * {@linkplain Window#getFramebufferWidth() width} and {@linkplain Window#getFramebufferHeight() height}.
     */
    void draw();

    void draw(int width, int height, boolean disableBlend);

    /**
     * Clears the content of this framebuffer.
     */
    void clear();

    void clear(boolean swallowErrors);
}
