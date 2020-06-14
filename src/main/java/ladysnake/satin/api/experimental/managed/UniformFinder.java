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

import org.apiguardian.api.API;
import org.jetbrains.annotations.ApiStatus;

import static org.apiguardian.api.API.Status.DEPRECATED;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * @deprecated use the promoted version in {@code ladysnake.satin.api.managed.uniform}
 */
@Deprecated
@ApiStatus.ScheduledForRemoval
@API(status = DEPRECATED, since = "1.4.0")
public interface UniformFinder extends ladysnake.satin.api.managed.uniform.UniformFinder {
    /**
     * Finds a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    Uniform1i findUniform1i(String uniformName);

    /**
     * Finds a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    Uniform2i findUniform2i(String uniformName);

    /**
     * Finds a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    Uniform3i findUniform3i(String uniformName);

    /**
     * Finds a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    Uniform4i findUniform4i(String uniformName);

    /**
     * Finds a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    Uniform1f findUniform1f(String uniformName);

    /**
     * Finds a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    Uniform2f findUniform2f(String uniformName);

    /**
     * Finds a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    Uniform3f findUniform3f(String uniformName);

    /**
     * Finds a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    Uniform4f findUniform4f(String uniformName);

    /**
     * Finds a uniform declared in json
     *
     * @param uniformName the name of the uniform field in the shader source file
     */
    @API(status = EXPERIMENTAL, since = "1.3.0")
    UniformMat4 findUniformMat4(String uniformName);

}