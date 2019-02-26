package ladysnake.satin.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.entity.Entity;

@FunctionalInterface
public interface PostEntitiesRenderCallback {
    /**
     * Fired after Minecraft has rendered all entities and before it renders block entities.
     */
    Event<PostEntitiesRenderCallback> EVENT = EventFactory.createArrayBacked(PostEntitiesRenderCallback.class,
            (listeners) -> (Entity camera, VisibleRegion frustum, float tickDelta) -> {
                for (PostEntitiesRenderCallback handler : listeners) {
                    handler.onEntitiesRendered(camera, frustum, tickDelta);
                }
            });

    void onEntitiesRendered(Entity camera, VisibleRegion frustum, float tickDelta);
}
