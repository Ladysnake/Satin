/*
 * Satin
 * Copyright (C) 2019-2020 Ladysnake
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
package ladysnake.satin.impl;

import ladysnake.satin.mixin.client.render.RenderLayerMixin;
import ladysnake.satin.mixin.client.render.RenderLayerAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.client.render.RenderLayer;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Consumer;

public final class RenderLayerDuplicator {
    private static final Field multiPhase$phases;
    private static final Field multiPhaseParameters$outlineMode;
    private static final Method multiPhaseParametersBuilder$build;
    private static final Class<?> multiPhaseClass;

    private static Type unmap(Class<?> clazz) {
        // for a better implementation, see https://github.com/Ladysnake/Requiem/blob/523ec343d5/src/main/java/ladysnake/requiem/common/util/reflection/ReflectionHelper.java#L61-L74
        if (clazz.isArray()) throw new IllegalArgumentException(clazz + " is an array type, array types are unsupported");
        String unmappedType = FabricLoader.getInstance().getMappingResolver().unmapClassName("intermediary", clazz.getName());
        return Type.getObjectType(unmappedType.replace('.', '/'));
    }

    private static Field findFieldFromIntermediary(Class<?> owner, String intermediaryName, Class<?> type) throws NoSuchFieldException {
        String fieldDesc = unmap(type).getDescriptor();
        MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
        String intermediaryOwner = mappingResolver.unmapClassName("intermediary", owner.getName());
        String mappedName = mappingResolver.mapFieldName("intermediary", intermediaryOwner, intermediaryName, fieldDesc);
        Field f = owner.getDeclaredField(mappedName);
        f.setAccessible(true);
        return f;
    }

    private static Method findMethodFromIntermediary(Class<?> owner, String intermediaryName, Class<?> returnType, Class<?>... parameterTypes) throws NoSuchMethodException {
        String methodDesc = Type.getMethodDescriptor(unmap(returnType), Arrays.stream(parameterTypes).map(RenderLayerDuplicator::unmap).toArray(Type[]::new));
        MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
        String intermediaryOwner = mappingResolver.unmapClassName("intermediary", owner.getName());
        String mappedName = mappingResolver.mapMethodName("intermediary", intermediaryOwner, intermediaryName, methodDesc);
        Method m = owner.getDeclaredMethod(mappedName, parameterTypes);
        m.setAccessible(true);
        return m;
    }

    static {
        try {
            MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();

            multiPhaseClass = Class.forName(mappingResolver.mapClassName("intermediary", "net.minecraft.class_1921$class_4687"));
            multiPhase$phases = findFieldFromIntermediary(multiPhaseClass, "field_21403", RenderLayer.MultiPhaseParameters.class);

            Class<?> outlineModeClass = Class.forName(mappingResolver.mapClassName("intermediary", "net.minecraft.class_1921$class_4750"));
            multiPhaseParameters$outlineMode = findFieldFromIntermediary(RenderLayer.MultiPhaseParameters.class, "field_21852", outlineModeClass);
            multiPhaseParametersBuilder$build = findMethodFromIntermediary(RenderLayer.MultiPhaseParameters.Builder.class, "method_24297", RenderLayer.MultiPhaseParameters.class, outlineModeClass);
        } catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    public static RenderLayer copy(RenderLayer existing, String newName, Consumer<RenderLayer.MultiPhaseParameters.Builder> op) {
        checkDefaultImpl(existing);
        return ((SatinRenderLayer) existing).satin$copy(newName, op);
    }

    public static RenderLayer.MultiPhaseParameters copyPhaseParameters(RenderLayer existing, Consumer<RenderLayer.MultiPhaseParameters.Builder> op) {
        checkDefaultImpl(existing);
        return ((SatinRenderLayer) existing).satin$copyPhaseParameters(op);
    }

    private static void checkDefaultImpl(RenderLayer existing) {
        if (!multiPhaseClass.isInstance(existing)) {
            throw new IllegalArgumentException("Unrecognized RenderLayer implementation " + existing.getClass() + ". Layer duplication is only applicable to the default (MultiPhase) implementation");
        }
    }

    public interface SatinRenderLayer {
        RenderLayer satin$copy(String newName, Consumer<RenderLayer.MultiPhaseParameters.Builder> op);
        RenderLayer.MultiPhaseParameters satin$copyPhaseParameters(Consumer<RenderLayer.MultiPhaseParameters.Builder> op);
    }
}
