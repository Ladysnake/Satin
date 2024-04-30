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
import org.joml.Matrix4f;

import static org.apiguardian.api.API.Status.MAINTAINED;

public interface UniformMat4 {

    /**
     * Sets the value of a 4x4 matrix uniform
     *
     * @param value a matrix
     */
    @API(status = MAINTAINED, since = "1.4.0")
    void set(Matrix4f value);

    /**
     * Sets the value of a 4x4 matrix uniform through a float array
     *
     * <p> The {@code values} array must have a length of 16, and contain the matrix elements in column-major order.
     *
     * @param values an array representing a 4x4 matrix
     * @throws IllegalArgumentException if {@code values} has an invalid length
     * @apiNote {@link #set(Matrix4f)} should generally be preferred due to its inherent type safety
     */
    @API(status = MAINTAINED, since = "1.15.0")
    void setFromArray(float[] values);
}
