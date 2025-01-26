package org.ladysnake.satin.impl;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import org.ladysnake.satin.api.event.EntitiesPostRenderCallback;
import org.ladysnake.satin.api.event.EntitiesPreRenderCallback;
import org.ladysnake.satin.api.event.PostWorldRenderCallbackV3;

import java.util.Objects;

/**
 * Delegates the invocation of events {@link EntitiesPreRenderCallback}, {@link EntitiesPostRenderCallback},
 * and {@link PostWorldRenderCallbackV3} to equivalent events in Fabric API.
 */
@SuppressWarnings("deprecation")
public final class WorldRenderEventsDelegator implements WorldRenderEvents.BeforeEntities, WorldRenderEvents.AfterEntities, WorldRenderEvents.Last {

    public static final WorldRenderEventsDelegator INSTANCE = new WorldRenderEventsDelegator();

    @Override
    public void beforeEntities(WorldRenderContext context) {
        EntitiesPreRenderCallback.EVENT.invoker()
                .beforeEntitiesRender(
                        context.camera(),
                        Objects.requireNonNull(context.frustum()),
                        context.tickCounter().getTickDelta(false)
                );
    }

    @Override
    public void afterEntities(WorldRenderContext context) {
        EntitiesPostRenderCallback.EVENT.invoker()
                .onEntitiesRendered(
                        context.camera(),
                        Objects.requireNonNull(context.frustum()),
                        context.tickCounter().getTickDelta(false)
                );
    }

    @Override
    public void onLast(WorldRenderContext context) {
        PostWorldRenderCallbackV3.EVENT.invoker()
                .onWorldRendered(
                        Objects.requireNonNull(context.matrixStack()),
                        context.projectionMatrix(),
                        context.positionMatrix(),
                        context.camera(),
                        context.tickCounter().getTickDelta(false)
                );
    }

    private WorldRenderEventsDelegator() {

    }
}