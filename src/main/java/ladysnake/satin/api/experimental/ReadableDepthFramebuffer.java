package ladysnake.satin.api.experimental;

import org.apiguardian.api.API;

import javax.annotation.CheckForSigned;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Implemented by every {@link net.minecraft.client.gl.GlFramebuffer} when the feature is enabled in the config.
 * <p>
 * This allows access to a depth texture that the framebuffer writes to instead of the usual render buffer.
 * <p>
 * The replacement of the render buffer with a readable depth texture is only done when at least one mod
 * declares the {@link ladysnake.satin.config.SatinFeatures#readableDepthFramebuffers} feature as used.
 */
public interface ReadableDepthFramebuffer {
    /**
     * Returns the depth texture used by this buffer.
     * <p>
     * This buffer <b>MUST</b> be in use when this method is called.
     * The reason for this is that the depth texture is retrieved
     * reflectively to account for other mods doing similar changes.
     * <p>
     * If the feature is not enabled, or an incompatibility prevents the
     * retrieval of the texture, the returned value will be -1.
     *
     * @return the gl id of the depth texture, or -1 if it cannot be obtained
     */
    @CheckForSigned
    @API(status = EXPERIMENTAL)
    int getCurrentDepthTexture();

}
