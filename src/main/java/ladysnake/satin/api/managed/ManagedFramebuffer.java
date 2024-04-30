/*
 * Satin
 * Copyright (C) 2019-2024 Ladysnake
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
package ladysnake.satin.api.managed;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.util.Window;
import org.apiguardian.api.API;

import javax.annotation.Nullable;
import java.util.function.Consumer;

@API(status = API.Status.EXPERIMENTAL, since = "1.4.0")
public interface ManagedFramebuffer {
    @Nullable
    Framebuffer getFramebuffer();

    /**
     * Begins a write operation on this framebuffer.
     *
     * <p>If the operation is successful, every subsequent draw call will write to this framebuffer.
     *
     * @param updateViewport whether binding this framebuffer should call {@link com.mojang.blaze3d.systems.RenderSystem#viewport(int, int, int, int)}
     */
    void beginWrite(boolean updateViewport);

    /**
     * Copies the depth texture from another framebuffer to this framebuffer.
     *
     * @param buffer the framebuffer to copy depth from
     */
    void copyDepthFrom(Framebuffer buffer);

    /**
     * Draws this framebuffer, scaling to the default framebuffer's
     * {@linkplain Window#getFramebufferWidth() width} and {@linkplain Window#getFramebufferHeight() height}.
     */
    void draw();

    void draw(int width, int height, boolean disableBlend);

    /**
     * Clears the content of this framebuffer.
     */
    void clear();

    void clear(boolean swallowErrors);

    /**
     * Gets a simple {@link RenderLayer} that is functionally identical to {@code baseLayer},
     * but with a different {@link RenderPhase.Target} that binds this framebuffer.
     *
     * @param baseLayer the layer to copy
     * @return a render layer using this framebuffer
     * @see ladysnake.satin.api.util.RenderLayerHelper#copy(RenderLayer, String, Consumer)
     */
    RenderLayer getRenderLayer(RenderLayer baseLayer);
}
