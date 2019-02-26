package ladysnake.satin.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface PickEntityShaderCallback {
    /**
     * Fired in {@link net.minecraft.client.render.GameRenderer#onCameraEntitySet(Entity)}
     */
    Event<PickEntityShaderCallback> EVENT = EventFactory.createArrayBacked(PickEntityShaderCallback.class,
            (listeners) -> (entity, loadShaderFunc, appliedShaderGetter) -> {
                for (PickEntityShaderCallback handler : listeners) {
                    handler.pickEntityShader(entity, loadShaderFunc, appliedShaderGetter);
                    // Allow listeners to set the shader themselves if they need to configure it
                    if (appliedShaderGetter.get() != null) {
                        break;
                    }
                }
            });

    void pickEntityShader(@Nullable Entity entity, Consumer<Identifier> loadShaderFunc, Supplier<ShaderEffect> appliedShaderGetter);
}
