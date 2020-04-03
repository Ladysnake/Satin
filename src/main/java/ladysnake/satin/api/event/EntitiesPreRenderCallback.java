package ladysnake.satin.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;

@FunctionalInterface
public interface EntitiesPreRenderCallback {
    /**
     * Fired after Minecraft has rendered all entities and before it renders block entities.
     */
    Event<EntitiesPreRenderCallback> EVENT = EventFactory.createArrayBacked(EntitiesPreRenderCallback.class,
            (listeners) -> (camera, frustum, tickDelta) -> {
                for (EntitiesPreRenderCallback handler : listeners) {
                    handler.beforeEntitiesRender(camera, frustum, tickDelta);
                }
            });

    void beforeEntitiesRender(Camera camera, Frustum frustum, float tickDelta);
}
