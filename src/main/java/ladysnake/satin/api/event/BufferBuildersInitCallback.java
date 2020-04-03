package ladysnake.satin.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.jetbrains.annotations.ApiStatus;

import java.util.SortedMap;

@ApiStatus.Experimental
public interface BufferBuildersInitCallback {
    /**
     * Fired when Minecraft initializes its {@link BufferBuilderStorage}.
     *
     * <p> Mods should initialize their {@link VertexConsumerProvider} and {@link BufferBuilder}
     * at this time.
     */
    Event<BufferBuildersInitCallback> EVENT = EventFactory.createArrayBacked(BufferBuildersInitCallback.class,
            (listeners) -> (bufferBuilders, entityBuffers) -> {
                for (BufferBuildersInitCallback handler : listeners) {
                    handler.initBufferBuilders(bufferBuilders, entityBuffers);
                }
            });

    void initBufferBuilders(BufferBuilderStorage bufferBuilders, SortedMap<RenderLayer, BufferBuilder> entityBuilders);
}
