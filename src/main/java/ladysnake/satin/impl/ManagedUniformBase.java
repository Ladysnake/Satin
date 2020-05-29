package ladysnake.satin.impl;

import net.minecraft.client.gl.JsonGlProgram;
import net.minecraft.client.gl.PostProcessShader;

import java.util.List;

public abstract class ManagedUniformBase {
    protected final String name;

    public ManagedUniformBase(String name) {
        this.name = name;
    }

    public abstract boolean findUniformTargets(List<PostProcessShader> shaders);

    public abstract boolean findUniformTarget(JsonGlProgram program);

    public String getName() {
        return name;
    }
}
