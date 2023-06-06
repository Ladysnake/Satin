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
package ladysnake.satindepthtest;

import ladysnake.satin.api.event.PostWorldRenderCallback;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satin.api.experimental.ReadableDepthFramebuffer;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import ladysnake.satin.api.managed.uniform.Uniform1f;
import ladysnake.satin.api.managed.uniform.Uniform3f;
import ladysnake.satin.api.managed.uniform.UniformMat4;
import ladysnake.satin.api.util.GlMatrices;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class DepthFx implements PostWorldRenderCallback, ShaderEffectRenderCallback, ClientTickEvents.EndTick {
    public static final Identifier FANCY_NIGHT_SHADER_ID = new Identifier(SatinDepthTest.MOD_ID, "shaders/post/rainbow_ping.json");
    public static final DepthFx INSTANCE = new DepthFx();

    private final MinecraftClient mc = MinecraftClient.getInstance();

    final ManagedShaderEffect testShader = ShaderEffectManager.getInstance().manage(FANCY_NIGHT_SHADER_ID, shader -> {
        shader.setSamplerUniform("DepthSampler", ((ReadableDepthFramebuffer)mc.getFramebuffer()).getStillDepthMap());
        shader.setUniformValue("ViewPort", 0, 0, mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight());
    });
    private final Uniform1f uniformSTime = testShader.findUniform1f("STime");
    private final UniformMat4 uniformInverseTransformMatrix = testShader.findUniformMat4("InverseTransformMatrix");
    private final Uniform3f uniformCameraPosition = testShader.findUniform3f("CameraPosition");
    private final Uniform3f uniformCenter = testShader.findUniform3f("Center");

    // fancy shader stuff
    private final Matrix4f projectionMatrix = new Matrix4f();
    private int ticks;

    private boolean isWorldNight(@Nullable PlayerEntity player) {
        if (player != null) {
            World world = player.world;
            float celestialAngle = world.getSkyAngle(1.0f);
            return 0.23f < celestialAngle && celestialAngle < 0.76f;
        }
        return false;
    }

    @Override
    public void onEndTick(MinecraftClient minecraftClient) {
        if (!minecraftClient.isPaused()) {
            ticks++;
        }
    }

    @Override
    public void onWorldRendered(Camera camera, float tickDelta, long nanoTime) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (isWorldNight(mc.player)) {
            uniformSTime.set((ticks + tickDelta) / 20f);
            uniformInverseTransformMatrix.set(GlMatrices.getInverseTransformMatrix(projectionMatrix));
            Vec3d cameraPos = camera.getPos();
            uniformCameraPosition.set((float)cameraPos.x, (float)cameraPos.y, (float)cameraPos.z);
            Entity e = camera.getFocusedEntity();
            uniformCenter.set(lerpf(e.getX(), e.prevX, tickDelta), lerpf(e.getY(), e.prevY, tickDelta), lerpf(e.getZ(), e.prevZ, tickDelta));
        }
    }

    @Override
    public void renderShaderEffects(float tickDelta) {
        testShader.render(tickDelta);
    }

    private static float lerpf(double n, double prevN, float tickDelta) {
        return (float) MathHelper.lerp(tickDelta, prevN, n);
    }
}
