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
package ladysnake.satin.api.experimental.managed;

import net.minecraft.util.math.Matrix4f;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

public interface UniformMat4 {

    /**
     * Sets the value of a 4x4 matrix uniform
     *
     * @param value a matrix
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    void set(Matrix4f value);

}
