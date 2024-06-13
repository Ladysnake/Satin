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
package ladysnake.satin.api.managed.uniform;

import org.apiguardian.api.API;

import java.util.function.IntSupplier;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

public interface SamplerUniformV2 extends SamplerUniform {

    /**
     * Sets the value of a sampler uniform declared in json
     *
     * <p><strong>This method is only supported for {@link ladysnake.satin.api.managed.ManagedShaderEffect}</strong>
     *
     * @param textureSupplier a supplier for opengl texture names
     */
    @API(status = EXPERIMENTAL, since = "1.4.0")
    void set(IntSupplier textureSupplier);
}
