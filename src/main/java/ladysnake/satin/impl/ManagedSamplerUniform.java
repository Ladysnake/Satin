package ladysnake.satin.impl;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import ladysnake.satin.api.managed.uniform.SamplerUniform;
import ladysnake.satin.mixin.client.gl.JsonGlProgramAccessor;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.JsonGlProgram;
import net.minecraft.client.gl.PostProcessShader;
import net.minecraft.client.texture.AbstractTexture;

import java.util.ArrayList;
import java.util.List;

public class ManagedSamplerUniform extends ManagedUniformBase implements SamplerUniform {
    protected JsonGlProgram[] targets = new JsonGlProgram[0];
    protected int[] locations = new int[0];
    private Object sampler;

    public ManagedSamplerUniform(String name) {
        super(name);
    }

    @Override
    public boolean findUniformTargets(List<PostProcessShader> shaders) {
        List<JsonGlProgram> targets = new ArrayList<>(shaders.size());
        IntList rawTargets = new IntArrayList(shaders.size());
        for (PostProcessShader shader : shaders) {
            JsonGlProgram program = shader.getProgram();
            JsonGlProgramAccessor access = (JsonGlProgramAccessor) program;
            if (access.getSamplerBinds().containsKey(this.name)) {
                targets.add(program);
                rawTargets.add(getSamplerLoc(access));
            }
        }
        this.targets = targets.toArray(new JsonGlProgram[0]);
        this.locations = rawTargets.toArray(new int[0]);
        return this.targets.length > 0;
    }

    private int getSamplerLoc(JsonGlProgramAccessor access) {
        return access.getSamplerShaderLocs().get(access.getSamplerNames().indexOf(this.name));
    }

    @Override
    public boolean findUniformTarget(JsonGlProgram program) {
        JsonGlProgramAccessor access = (JsonGlProgramAccessor) program;
        if (access.getSamplerBinds().containsKey(this.name)) {
            this.targets = new JsonGlProgram[] {program};
            this.locations = new int[] {getSamplerLoc(access)};
            return true;
        }
        return false;
    }

    @Override
    public void setDirect(int activeTexture) {
        int length = this.locations.length;
        for (int i = 0; i < length; i++) {
            ((JsonGlProgramAccessor) this.targets[i]).getSamplerBinds().remove(this.name);
            GlUniform.uniform1(this.locations[i], activeTexture);
        }
    }

    @Override
    public void set(AbstractTexture texture) {
        set((Object) texture);
    }

    @Override
    public void set(Framebuffer textureFbo) {
        set((Object) textureFbo);
    }

    @Override
    public void set(int textureName) {
        set((Object) textureName);
    }

    private void set(Object value) {
        JsonGlProgram[] targets = this.targets;
        int nbTargets = targets.length;
        if (nbTargets > 0) {
            if (this.sampler != value) {
                for (JsonGlProgram target : targets) {
                    target.bindSampler(this.name, value);
                }
                this.sampler = value;
            }
        }
    }
}
