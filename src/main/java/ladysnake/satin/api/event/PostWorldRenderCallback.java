package ladysnake.satin.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.Camera;

@FunctionalInterface
public interface PostWorldRenderCallback {
    /**
     * Fired after Minecraft has rendered everything in the world, before it renders hands, HUDs and GUIs.
     */
    Event<PostWorldRenderCallback> EVENT = EventFactory.createArrayBacked(PostWorldRenderCallback.class,
            (listeners) -> (camera, tickDelta, nanoTime) -> {
                for (PostWorldRenderCallback handler : listeners) {
                    handler.onWorldRendered(camera, tickDelta, nanoTime);
                }
            });

    void onWorldRendered(Camera camera, float tickDelta, long nanoTime);
}
