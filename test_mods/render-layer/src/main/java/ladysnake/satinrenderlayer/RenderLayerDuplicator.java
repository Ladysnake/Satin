package ladysnake.satinrenderlayer;

import ladysnake.satinrenderlayer.mixin.client.MultiPhaseAccessor;
import ladysnake.satinrenderlayer.mixin.client.MultiPhaseParametersAccessor;
import ladysnake.satinrenderlayer.mixin.client.RenderLayerAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.client.render.RenderLayer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

public class RenderLayerDuplicator {
    private static final Field multiPhaseParameters$outlineMode;
    private static final Method multiPhaseParametersBuilder$build;

    static {
        try {
            MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
            Class<?> outlineModeClass = Class.forName(mappingResolver.mapClassName("intermediary", "net.minecraft.class_1921$class_4750"));
            Field outlineModeField = null;
            for (Field declaredField : RenderLayer.MultiPhaseParameters.class.getDeclaredFields()) {
                if (declaredField.getType() == outlineModeClass) {
                    if (outlineModeField != null) {
                        throw new IllegalStateException("More than one candidate for MultiPhaseParameters#outlineMode");
                    }
                    outlineModeField = declaredField;
                }
            }
            if (outlineModeField == null) {
                throw new IllegalStateException("No outline mode field in MultiPhaseParameters");
            }
            outlineModeField.setAccessible(true);
            multiPhaseParameters$outlineMode = outlineModeField;
            Method buildMethod = null;
            for (Method declaredMethod : RenderLayer.MultiPhaseParameters.Builder.class.getDeclaredMethods()) {
                if (declaredMethod.getReturnType() == RenderLayer.MultiPhaseParameters.class) {
                    Class<?>[] parameterTypes = declaredMethod.getParameterTypes();
                    if (parameterTypes.length == 1 && parameterTypes[0] == outlineModeClass) {
                        if (buildMethod != null) {
                            throw new IllegalStateException("More than one candidate for MultiPhaseParameters.Builder#build(OutlineMode)");
                        }
                        buildMethod = declaredMethod;
                    }
                }
            }
            if (buildMethod == null) {
                throw new IllegalStateException("No candidate for MultiPhaseParameters.Builder#build(OutlineMode)");
            }
            buildMethod.setAccessible(true);
            multiPhaseParametersBuilder$build = buildMethod;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static RenderLayer copyFrom(String newName, RenderLayer existing, Consumer<RenderLayer.MultiPhaseParameters.Builder> op) {
        if (!(existing instanceof MultiPhaseAccessor)) {
            throw new IllegalArgumentException("Only applicable to the default RenderLayer implementation");
        }
        try {
            return RenderLayer.of(
                    newName,
                    existing.getVertexFormat(),
                    existing.getDrawMode(),
                    existing.getExpectedBufferSize(),
                    existing.hasCrumbling(),
                    ((RenderLayerAccessor) existing).isTranslucent(),
                    copyPhaseParameters((MultiPhaseAccessor) existing, op)
            );
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to duplicate render layer parameters from " + existing);
        }
    }

    private static RenderLayer.MultiPhaseParameters copyPhaseParameters(MultiPhaseAccessor existing, Consumer<RenderLayer.MultiPhaseParameters.Builder> op) throws IllegalAccessException, InvocationTargetException {
        // yes, casting is safe
        @SuppressWarnings("ConstantConditions") MultiPhaseParametersAccessor access = ((MultiPhaseParametersAccessor) (Object) existing.getPhases());
        RenderLayer.MultiPhaseParameters.Builder builder = RenderLayer.MultiPhaseParameters.builder()
                .texture(access.getTexture())
                .transparency(access.getTransparency())
                .diffuseLighting(access.getDiffuseLighting())
                .shadeModel(access.getShadeModel())
                .alpha(access.getAlpha())
                .depthTest(access.getDepthTest())
                .cull(access.getCull())
                .lightmap(access.getLightmap())
                .overlay(access.getOverlay())
                .fog(access.getFog())
                .layering(access.getLayering())
                .target(access.getTarget())
                .texturing(access.getTexturing())
                .writeMaskState(access.getWriteMaskState())
                .lineWidth(access.getLineWidth());
        op.accept(builder);
        return (RenderLayer.MultiPhaseParameters) multiPhaseParametersBuilder$build.invoke(builder, multiPhaseParameters$outlineMode.get(access));
    }
}
