package ladysnake.satin.impl;

import ladysnake.satin.api.experimental.managed.*;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.PostProcessShader;
import net.minecraft.util.math.Matrix4f;

import java.util.List;
import java.util.Objects;

public final class ManagedUniform implements
        Uniform1i, Uniform2i, Uniform3i, Uniform4i,
        Uniform1f, Uniform2f, Uniform3f, Uniform4f,
        UniformMat4 {

    private GlUniform[] targets = new GlUniform[0];
    private final String name;
    private int i0, i1, i2, i3;
    private float f0, f1, f2, f3;

    public ManagedUniform(String name) {
        this.name = name;
    }

    public int findUniformTargets(List<PostProcessShader> shaders) {
        this.targets = shaders.stream()
                .map(PostProcessShader::getProgram)
                .map(s -> s.getUniformByName(this.name))
                .filter(Objects::nonNull)
                .toArray(GlUniform[]::new);
        return this.targets.length;
    }

    public String getName() {
        return name;
    }

    @Override
    public void set(int value) {
        GlUniform[] targets = this.targets;
        int nbTargets = targets.length;
        if (nbTargets > 0) {
            if (i0 != value) {
                for (GlUniform target : targets) {
                    target.set(value);
                }
                i0 = value;
            }
        }
    }

    @Override
    public void set(int value0, int value1) {
        GlUniform[] targets = this.targets;
        int nbTargets = targets.length;
        if (nbTargets > 0) {
            if (i0 != value0 || i1 != value1) {
                for (GlUniform target : targets) {
                    target.set(value0, value1);
                }
                i0 = value0;
                i1 = value1;
            }
        }
    }

    @Override
    public void set(int value0, int value1, int value2) {
        GlUniform[] targets = this.targets;
        int nbTargets = targets.length;
        if (nbTargets > 0) {
            if (i0 != value0 || i1 != value1 || i2 != value2) {
                for (GlUniform target : targets) {
                    target.set(value0, value1, value2);
                }
                i0 = value0;
                i1 = value1;
                i2 = value2;
            }
        }
    }

    @Override
    public void set(int value0, int value1, int value2, int value3) {
        GlUniform[] targets = this.targets;
        int nbTargets = targets.length;
        if (nbTargets > 0) {
            if (i0 != value0 || i1 != value1 || i2 != value2 || i3 != value3) {
                for (GlUniform target : targets) {
                    target.set(value0, value1, value2, value3);
                }
                i0 = value0;
                i1 = value1;
                i2 = value2;
                i3 = value3;
            }
        }
    }

    @Override
    public void set(float value) {
        GlUniform[] targets = this.targets;
        int nbTargets = targets.length;
        if (nbTargets > 0) {
            if (f0 != value) {
                for (GlUniform target : targets) {
                    target.set(value);
                }
                f0 = value;
            }
        }
    }

    @Override
    public void set(float value0, float value1) {
        GlUniform[] targets = this.targets;
        int nbTargets = targets.length;
        if (nbTargets > 0) {
            if (f0 != value0 || f1 != value1) {
                for (GlUniform target : targets) {
                    target.set(value0, value1);
                }
                f0 = value0;
                f1 = value1;
            }
        }
    }

    @Override
    public void set(float value0, float value1, float value2) {
        GlUniform[] targets = this.targets;
        int nbTargets = targets.length;
        if (nbTargets > 0) {
            if (f0 != value0 || f1 != value1 || f2 != value2) {
                for (GlUniform target : targets) {
                    target.set(value0, value1, value2);
                }
                f0 = value0;
                f1 = value1;
                f2 = value2;
            }
        }
    }

    @Override
    public void set(float value0, float value1, float value2, float value3) {
        GlUniform[] targets = this.targets;
        int nbTargets = targets.length;
        if (nbTargets > 0) {
            if (f0 != value0 || f1 != value1 || f2 != value2 || f3 != value3) {
                for (GlUniform target : targets) {
                    target.set(value0, value1, value2, value3);
                }
                f0 = value0;
                f1 = value1;
                f2 = value2;
                f3 = value3;
            }
        }
    }

    @Override
    public void set(Matrix4f value) {
        GlUniform[] targets = this.targets;
        int nbTargets = targets.length;
        if (nbTargets > 0) {
            for (GlUniform target : targets) {
                target.set(value);
            }
        }
    }

}
