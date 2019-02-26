package ladysnake.satin.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@FunctionalInterface
public interface ResolutionChangeCallback {
    /**
     * Fired each time Minecraft's window resolution changes
     */
    Event<ResolutionChangeCallback> EVENT = EventFactory.createArrayBacked(ResolutionChangeCallback.class,
            (listeners) -> (newWidth, newHeight) -> {
                for (ResolutionChangeCallback event : listeners) {
                    event.onResolutionChanged(newWidth, newHeight);
                }
            });

    void onResolutionChanged(int newWidth, int newHeight);
}
