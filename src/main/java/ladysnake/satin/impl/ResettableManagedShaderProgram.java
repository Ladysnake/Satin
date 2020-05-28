package ladysnake.satin.impl;

import com.google.common.base.Preconditions;
import ladysnake.satin.Satin;
import ladysnake.satin.api.managed.ManagedShaderProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.JsonGlProgram;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.function.Consumer;

public final class ResettableManagedShaderProgram extends ResettableManagedShaderBase<JsonGlProgram> implements ManagedShaderProgram {
    /**
     * Callback to run once each time the shader effect is initialized
     */
    private final Consumer<ManagedShaderProgram> initCallback;

    public ResettableManagedShaderProgram(Identifier location, Consumer<ManagedShaderProgram> initCallback) {
        super(location);
        this.initCallback = initCallback;
    }

    @Override
    protected JsonGlProgram parseShader(MinecraftClient mc, Identifier location) throws IOException {
        return new JsonGlProgram(MinecraftClient.getInstance().getResourceManager(), this.getLocation().toString());
    }

    @Override
    public void setup(int newWidth, int newHeight) {
        Preconditions.checkNotNull(this.shader);
        for (ManagedUniform uniform : this.getManagedUniforms()) {
            setupUniform(uniform, this.shader);
        }
        this.initCallback.accept(this);
    }

    @Override
    public JsonGlProgram getProgram() {
        return getShaderOrLog();
    }

    @Override
    public void enable() {
        JsonGlProgram program = this.getProgram();
        if (program != null) {
            program.enable();
        }
    }

    @Override
    public void disable() {
        JsonGlProgram program = this.getProgram();
        if (program != null) {
            program.disable();
        }
    }

    @Override
    protected boolean setupUniform(ManagedUniform uniform, JsonGlProgram shader) {
        return uniform.findUniformTarget(shader);
    }

    @Override
    protected void logInitError(IOException e) {
        Satin.LOGGER.error("Could not create shader program {}", this.getLocation(), e);
    }
}
