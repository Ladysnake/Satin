package ladysnake.satintest;

import com.mojang.blaze3d.platform.GlStateManager;
import ladysnake.satin.api.event.PostWorldRenderCallback;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import ladysnake.satin.api.util.GlMatrices;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;

public class DepthFx implements PostWorldRenderCallback, ClientTickCallback {
    public static final Identifier FANCY_NIGHT_SHADER_ID = new Identifier(SatinTest.MOD_ID, "shaders/post/rainbow_ping.json");
    public static final DepthFx INSTANCE = new DepthFx();

    private MinecraftClient mc = MinecraftClient.getInstance();
    private int shadowMapTexture = -1;

    private void setupShadowMap() {
        if (shadowMapTexture < 0) {
            shadowMapTexture = GL11.glGenTextures();
            GlStateManager.bindTexture(shadowMapTexture);
            GlStateManager.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            GlStateManager.texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            GlStateManager.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            GlStateManager.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            GlStateManager.texImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, mc.window.getFramebufferWidth(), mc.window.getFramebufferHeight(), 0,GL_DEPTH_COMPONENT, GL_UNSIGNED_BYTE, null);
        }
    }

    public final ManagedShaderEffect testShader = ShaderEffectManager.getInstance().manage(FANCY_NIGHT_SHADER_ID, shader -> {
        shader.setSamplerUniform("DepthSampler", shadowMapTexture);
        shader.setUniformValue("ViewPort", 0, 0, mc.window.getFramebufferWidth(), mc.window.getFramebufferHeight());
    });

    // fancy shader stuff
    private Matrix4f projectionMatrix = new Matrix4f();
    private int ticks;

    private boolean isPlayerEligible(@Nullable PlayerEntity player) {
        if (player != null) {
            World world = player.world;
            float celestialAngle = world.getSkyAngle(1.0f);
            return celestialAngle > 0.23f && celestialAngle < 0.76f || player.y < 88.0 && !player.world.isSkyVisible(player.getBlockPos());
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
        if (isPlayerEligible(mc.player)) {
            mc.getFramebuffer().beginWrite(false);
            setupShadowMap();
            GlStateManager.bindTexture(shadowMapTexture);
            glCopyTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 0, 0, mc.window.getFramebufferWidth(),mc.window.getFramebufferHeight());
            testShader.setUniformValue("STime", (ticks + tickDelta) / 20f);
            testShader.setUniformValue("InverseTransformMatrix", GlMatrices.getInverseTransformMatrix(projectionMatrix));
            Vec3d cameraPos = camera.getPos();
            testShader.setUniformValue("CameraPosition", (float)cameraPos.x, (float)cameraPos.y, (float)cameraPos.z);
            testShader.render(tickDelta);
        }
    }
}
