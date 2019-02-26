package ladysnake.satin.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ShaderEffectRenderCallback {
    /**
     * Fired when Minecraft renders the entity outline framebuffer.
     * Post process shader effects should generally be rendered at that time.
     */
    Event<ShaderEffectRenderCallback> EVENT = EventFactory.createArrayBacked(ShaderEffectRenderCallback.class,
            (listeners) -> (deltaTick) -> {
                for (ShaderEffectRenderCallback handler : listeners) {
                    handler.renderShaderEffects(deltaTick);
                }
            });

    void renderShaderEffects(float deltaTick);
}
