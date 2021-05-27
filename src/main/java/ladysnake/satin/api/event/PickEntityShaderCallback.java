/*
 * Satin
 * Copyright (C) 2019-2021 Ladysnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; If not, see <https://www.gnu.org/licenses>.
 */
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
