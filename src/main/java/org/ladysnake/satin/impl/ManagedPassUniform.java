/*
 * Satin
 * Copyright (C) 2019-2024 Ladysnake
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
package org.ladysnake.satin.impl;

import com.google.common.primitives.Floats;
import net.minecraft.client.gl.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.ladysnake.satin.api.managed.uniform.*;
import org.ladysnake.satin.mixin.client.gl.PostEffectPassAccessor;
import org.ladysnake.satin.mixin.client.gl.PostEffectUniformAccessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class ManagedPassUniform extends ManagedUniformBase implements
        Uniform1i, Uniform2i, Uniform3i, Uniform4i,
        Uniform1f, Uniform2f, Uniform3f, Uniform4f,
        UniformMat4 {

    private static final PostEffectPipeline.Uniform[] NO_TARGETS = new PostEffectPipeline.Uniform[0];

    private final int count;

    private PostEffectPipeline.Uniform[] targets = NO_TARGETS;
    private float f0, f1, f2, f3;
    private boolean firstUpload = true;

    public ManagedPassUniform(String name, int count) {
        super(name);
        this.count = count;
    }

    @Override
    public boolean findUniformTargets(List<PostEffectPass> shaders) {
        List<PostEffectPipeline.Uniform> list = new ArrayList<>();
        for (PostEffectPass shader : shaders) {
            PostEffectPipeline.Uniform uniform = findUniform((PostEffectPassAccessor) shader);

            if (uniform != null) {
                UniformType uniformType = UniformType.CODEC.byId(uniform.type());
                if (uniformType != null && uniformType.count() != this.count) {
                    throw new IllegalStateException("Mismatched number of values, expected " + this.count + " but JSON definition declares " + uniformType.count());
                }
                list.add(uniform);
            }
        }

        if (!list.isEmpty()) {
            this.targets = list.toArray(new PostEffectPipeline.Uniform[0]);
            this.syncCurrentValues();
            return true;
        } else {
            this.targets = NO_TARGETS;
            return false;
        }
    }

    private PostEffectPipeline.Uniform findUniform(PostEffectPassAccessor shader) {
        for (PostEffectPipeline.Uniform uniform : shader.getUniforms()) {
            if (uniform.name().equals(this.name)) {
                return uniform;
            }
        }
        return null;
    }

    @Override
    public boolean findUniformTarget(ShaderProgram shader) {
        throw new UnsupportedOperationException();
    }

    private void syncCurrentValues() {
        if (!this.firstUpload) {
            for (PostEffectPipeline.Uniform target : this.targets) {
                ((PostEffectUniformAccessor)(Object) target).setValues(Optional.of(List.of(f0, f1, f2, f3)));
            }
        }
    }

    @Override
    public void set(int value) {
        PostEffectPipeline.Uniform[] targets = this.targets;
        int nbTargets = targets.length;
        if (nbTargets > 0) {
            if (firstUpload || f0 != value) {
                for (PostEffectPipeline.Uniform target : targets) {
                    ((PostEffectUniformAccessor)(Object) target).setValues(Optional.of(List.of((float) value)));
                }
                f0 = value;
                firstUpload = false;
            }
        }
    }

    @Override
    public void set(int value0, int value1) {
        PostEffectPipeline.Uniform[] targets = this.targets;
        int nbTargets = targets.length;
        if (nbTargets > 0) {
            if (firstUpload || f0 != value0 || f1 != value1) {
                for (PostEffectPipeline.Uniform target : targets) {
                    ((PostEffectUniformAccessor)(Object) target).setValues(Optional.of(List.of((float) value0, (float) value1)));
                }
                f0 = value0;
                f1 = value1;
                firstUpload = false;
            }
        }
    }

    @Override
    public void set(int value0, int value1, int value2) {
        PostEffectPipeline.Uniform[] targets = this.targets;
        int nbTargets = targets.length;
        if (nbTargets > 0) {
            if (firstUpload || f0 != value0 || f1 != value1 || f2 != value2) {
                for (PostEffectPipeline.Uniform target : targets) {
                    ((PostEffectUniformAccessor)(Object) target).setValues(Optional.of(List.of((float) value0, (float) value1, (float) value2)));
                }
                f0 = value0;
                f1 = value1;
                f2 = value2;
                firstUpload = false;
            }
        }
    }

    @Override
    public void set(int value0, int value1, int value2, int value3) {
        PostEffectPipeline.Uniform[] targets = this.targets;
        int nbTargets = targets.length;
        if (nbTargets > 0) {
            if (firstUpload || f0 != value0 || f1 != value1 || f2 != value2 || f3 != value3) {
                for (PostEffectPipeline.Uniform target : targets) {
                    ((PostEffectUniformAccessor)(Object) target).setValues(Optional.of(List.of((float) value0, (float) value1, (float) value2, (float) value3)));
                }
                f0 = value0;
                f1 = value1;
                f2 = value2;
                f3 = value3;
                firstUpload = false;
            }
        }
    }

    @Override
    public void set(float value) {
        PostEffectPipeline.Uniform[] targets = this.targets;
        int nbTargets = targets.length;
        if (nbTargets > 0) {
            if (firstUpload || f0 != value) {
                for (PostEffectPipeline.Uniform target : targets) {
                    ((PostEffectUniformAccessor)(Object) target).setValues(Optional.of(List.of(value)));
                }
                f0 = value;
                firstUpload = false;
            }
        }
    }

    @Override
    public void set(float value0, float value1) {
        PostEffectPipeline.Uniform[] targets = this.targets;
        int nbTargets = targets.length;
        if (nbTargets > 0) {
            if (firstUpload || f0 != value0 || f1 != value1) {
                for (PostEffectPipeline.Uniform target : targets) {
                    ((PostEffectUniformAccessor)(Object) target).setValues(Optional.of(List.of(value0, value1)));
                }
                f0 = value0;
                f1 = value1;
                firstUpload = false;
            }
        }
    }

    @Override
    public void set(Vector2f value) {
        set(value.x(), value.y());
    }

    @Override
    public void set(float value0, float value1, float value2) {
        PostEffectPipeline.Uniform[] targets = this.targets;
        int nbTargets = targets.length;
        if (nbTargets > 0) {
            if (firstUpload || f0 != value0 || f1 != value1 || f2 != value2) {
                for (PostEffectPipeline.Uniform target : targets) {
                    ((PostEffectUniformAccessor)(Object) target).setValues(Optional.of(List.of(value0, value1, value2)));
                }
                f0 = value0;
                f1 = value1;
                f2 = value2;
                firstUpload = false;
            }
        }
    }

    @Override
    public void set(Vector3f value) {
        set(value.x(), value.y(), value.z());
    }

    @Override
    public void set(float value0, float value1, float value2, float value3) {
        PostEffectPipeline.Uniform[] targets = this.targets;
        int nbTargets = targets.length;
        if (nbTargets > 0) {
            if (firstUpload || f0 != value0 || f1 != value1 || f2 != value2 || f3 != value3) {
                for (PostEffectPipeline.Uniform target : targets) {
                    ((PostEffectUniformAccessor)(Object) target).setValues(Optional.of(List.of(value0, value1, value2, value3)));
                }
                f0 = value0;
                f1 = value1;
                f2 = value2;
                f3 = value3;
                firstUpload = false;
            }
        }
    }

    @Override
    public void set(Vector4f value) {
        set(value.x(), value.y(), value.z(), value.w());
    }

    @Override
    public void set(Matrix4f value) {
        PostEffectPipeline.Uniform[] targets = this.targets;
        int nbTargets = targets.length;
        if (nbTargets > 0) {
            for (PostEffectPipeline.Uniform target : targets) {
                ((PostEffectUniformAccessor)(Object) target).setValues(Optional.of(Floats.asList(value.get(new float[16]))));
            }
        }
    }

    @Override
    public void setFromArray(float[] values) {
        if (this.count != values.length) {
            throw new IllegalArgumentException("Mismatched values size, expected " + count + " but got " + values.length);
        }

        PostEffectPipeline.Uniform[] targets = this.targets;
        int nbTargets = targets.length;
        if (nbTargets > 0) {
            for (PostEffectPipeline.Uniform target : targets) {
                ((PostEffectUniformAccessor)(Object) target).setValues(Optional.of(Floats.asList(values)));
            }
        }
    }
}
