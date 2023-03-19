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
package ladysnake.satin.api.managed;

import ladysnake.satin.api.managed.uniform.UniformFinder;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;
import org.apiguardian.api.API;

import java.util.function.Consumer;

/**
 * @see ShaderEffectManager#manageCoreShader(Identifier) 
 * @see ShaderEffectManager#manageCoreShader(Identifier, VertexFormat, Consumer)
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
public interface ManagedCoreShader extends UniformFinder {
    ShaderProgram getProgram();

    /**
     * Releases this shader's resources.
     *
     * <p>If the caller does not intend to use this shader effect again, they
     * should call {@link ShaderEffectManager#dispose(ManagedShaderEffect)}.
     *
     * @see ShaderEffectManager#dispose(ManagedCoreShader)
     */
    void release();

    /**
     * Gets a simple {@link RenderLayer} that is functionally identical to {@code baseLayer},
     * but with a different {@link RenderPhase.Target} that enables this program.
     *
     * <p>The new {@link RenderLayer} will use this shader's vertex format as specified in {@link ShaderEffectManager#manageCoreShader(Identifier, VertexFormat, Consumer)}.
     *
     * @param baseLayer the layer to copy
     * @return a render layer using this shader program
     * @see ladysnake.satin.api.util.RenderLayerHelper#copy(RenderLayer, String, Consumer)
     */
    RenderLayer getRenderLayer(RenderLayer baseLayer);
}
