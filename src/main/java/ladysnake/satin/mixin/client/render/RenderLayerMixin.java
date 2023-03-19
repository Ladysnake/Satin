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
package ladysnake.satin.mixin.client.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderLayer.class)
public abstract class RenderLayerMixin extends RenderPhase {
    public RenderLayerMixin(String name, Runnable beginAction, Runnable endAction) {
        super(name, beginAction, endAction);
    }

    @Mixin(RenderLayer.MultiPhaseParameters.class)
    public interface MultiPhaseParametersAccessor {
        @Accessor
        TextureBase getTexture();

        @Accessor
        RenderPhase.ShaderProgram getProgram();

        @Accessor
        RenderPhase.Transparency getTransparency();

        @Accessor
        RenderPhase.DepthTest getDepthTest();

        @Accessor
        RenderPhase.Cull getCull();

        @Accessor
        RenderPhase.Lightmap getLightmap();

        @Accessor
        RenderPhase.Overlay getOverlay();

        @Accessor
        RenderPhase.Layering getLayering();

        @Accessor
        RenderPhase.Target getTarget();

        @Accessor
        RenderPhase.Texturing getTexturing();

        @Accessor
        RenderPhase.WriteMaskState getWriteMaskState();

        @Accessor
        RenderPhase.LineWidth getLineWidth();

        @Accessor
        RenderLayer.OutlineMode getOutlineMode();
    }
}
