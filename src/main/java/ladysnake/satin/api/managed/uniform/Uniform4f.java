/*
 * Satin
 * Copyright (C) 2019-2023 Ladysnake
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
import org.joml.Vector4f;

import static org.apiguardian.api.API.Status.MAINTAINED;

public interface Uniform4f {

    /**
     * Sets the value of a uniform declared in json
     *
     * @param value0 float value
     * @param value1 float value
     * @param value2 float value
     * @param value3 float value
     */
    @API(status = MAINTAINED, since = "1.4.0")
    void set(float value0, float value1, float value2, float value3);

    /**
     * Sets the value of a uniform declared in json
     *
     * <p>The vector's value is read once when this method is called.
     * Further mutations to the vector will have no effect on this uniform's value.
     *
     * @param value the vector of new values
     */
    @API(status = MAINTAINED, since = "1.12.0")
    void set(Vector4f value);
}
