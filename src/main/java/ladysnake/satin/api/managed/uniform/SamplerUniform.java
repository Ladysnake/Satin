/*
 * Satin
 * Copyright (C) 2019-2020 Ladysnake
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
package ladysnake.satin.api.managed.uniform;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.AbstractTexture;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.MAINTAINED;

public interface SamplerUniform {
    /**
     * Sets the value of a sampler uniform declared in json
     *
     * @param texture a texture object
     */
    @API(status = MAINTAINED, since = "1.4.0")
    void set(AbstractTexture texture);

    /**
     * Sets the value of a sampler uniform declared in json
     *
     * @param textureFbo a framebuffer which main texture will be used
     */
    @API(status = MAINTAINED, since = "1.4.0")
    void set(Framebuffer textureFbo);

    /**
     * Sets the value of a sampler uniform declared in json
     *
     * @param textureName an opengl texture name
     */
    @API(status = MAINTAINED, since = "1.4.0")
    void set(int textureName);
}
