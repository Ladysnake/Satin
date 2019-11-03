/*
 * MIT License
 *
 * Copyright (c) 2019 Grondag
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ladysnake.satin.impl;

import ladysnake.satin.api.event.ResolutionChangeCallback;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import net.fabricmc.fabric.api.client.render.InvalidateRenderStateCallback;
import net.minecraft.client.MinecraftClient;

/**
 * A fix for weird framebuffer refresh issues
 *
 * <p> Taken from <a href=https://github.com/grondag/doomtree/blob/20927a5b4b559ebd7f74905eeb68948ed1ea3c50/src/main/java/grondag/doomtree/render/DoomEffectRender.java#L30>Doom Tree's source</a>
 * @author Grondag
 */
public final class FbReloadFix {

    private static MinecraftClient MC = MinecraftClient.getInstance();
    private static int frameCount = 0;

    public static void init() {
        ShaderEffectRenderCallback.EVENT.register(d -> {
            if (frameCount < 2)  {
                if (frameCount++ == 1) {
                    ResolutionChangeCallback.EVENT.invoker().onResolutionChanged(MC.window.getFramebufferWidth(), MC.window.getFramebufferHeight());
                }
            }
        });
        InvalidateRenderStateCallback.EVENT.register(() -> frameCount = 0);
    }

}

