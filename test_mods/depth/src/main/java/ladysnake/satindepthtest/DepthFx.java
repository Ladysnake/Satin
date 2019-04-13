package ladysnake.satindepthtest;

import ladysnake.satin.api.event.PostWorldRenderCallback;
import ladysnake.satin.api.experimental.ReadableDepthFramebuffer;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import ladysnake.satin.api.util.GlMatrices;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DepthFx implements PostWorldRenderCallback, ClientTickCallback {
    public static final Identifier FANCY_NIGHT_SHADER_ID = new Identifier(SatinDepthTest.MOD_ID, "shaders/post/rainbow_ping.json");
    public static final DepthFx INSTANCE = new DepthFx();

    private MinecraftClient mc = MinecraftClient.getInstance();

    final ManagedShaderEffect testShader = ShaderEffectManager.getInstance().manage(FANCY_NIGHT_SHADER_ID, shader -> {
        shader.setSamplerUniform("DepthSampler", ((ReadableDepthFramebuffer)mc.getFramebuffer()).getStillDepthMap());
        shader.setUniformValue("ViewPort", 0, 0, mc.window.getFramebufferWidth(), mc.window.getFramebufferHeight());
    });

    // fancy shader stuff
    private Matrix4f projectionMatrix = new Matrix4f();
    private int ticks;

    private boolean isWorldNight(@Nullable PlayerEntity player) {
        if (player != null) {
            World world = player.world;
            float celestialAngle = world.getSkyAngle(1.0f);
            return celestialAngle > 0.23f && celestialAngle < 0.76f;
        }
        return false;
    }

    @Override
    public void tick(MinecraftClient minecraftClient) {
        ticks++;
    }

    @Override
    public void onWorldRendered(Camera camera, float tickDelta, long nanoTime) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (isWorldNight(mc.player)) {
            testShader.setUniformValue("STime", (ticks + tickDelta) / 20f);
            testShader.setUniformValue("InverseTransformMatrix", GlMatrices.getInverseTransformMatrix(projectionMatrix));
            Vec3d cameraPos = camera.getPos();
            testShader.setUniformValue("CameraPosition", (float)cameraPos.x, (float)cameraPos.y, (float)cameraPos.z);
            Entity e = camera.getFocusedEntity();
            testShader.setUniformValue("Center", lerp(e.x, e.prevX, tickDelta), lerp(e.y, e.prevY, tickDelta), lerp(e.z, e.prevZ, tickDelta));
            testShader.render(tickDelta);
        }
    }

    private static float lerp(double n, double prevN, float tickDelta) {
        return (float) MathHelper.lerp(tickDelta, prevN, n);
    }
}
