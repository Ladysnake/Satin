/*
 * Satin
 * Copyright (C) 2019-2022 Ladysnake
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

public interface ShaderEffectRenderCallback {
    /**
     * Fired when Minecraft renders the entity outline framebuffer.
     * Post process shader effects should generally be rendered at that time.
     */
    Event<ShaderEffectRenderCallback> EVENT = EventFactory.createArrayBacked(ShaderEffectRenderCallback.class,
            (listeners) -> (tickDelta) -> {
                for (ShaderEffectRenderCallback handler : listeners) {
                    handler.renderShaderEffects(tickDelta);
                }
            });

    void renderShaderEffects(float tickDelta);
}
