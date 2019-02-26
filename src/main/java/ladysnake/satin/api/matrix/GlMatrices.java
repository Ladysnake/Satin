package ladysnake.satin.api.matrix;

import org.apiguardian.api.API;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

import static org.apiguardian.api.API.Status.MAINTAINED;

public final class GlMatrices {
    /**Used for opengl interactions such as matrix retrieval*/
    private static FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
    /**A secondary buffer for the same purpose as {@link #buffer}*/
    private static FloatBuffer buffer1 = BufferUtils.createFloatBuffer(16);

    /**
     * A 16-sized float buffer that can be used to send data to shaders.
     * Do not use this buffer for long term data storage, it can be cleared at any time.
     */
    @API(status = MAINTAINED)
    public static FloatBuffer getTmpBuffer() {
        buffer.clear();
        return buffer;
    }

    @API(status = MAINTAINED)
    public static FloatBuffer getProjectionMatrix() {
        FloatBuffer projection = getTmpBuffer();
        GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, projection);
        projection.rewind();
        return projection;
    }

    @API(status = MAINTAINED)
    public static FloatBuffer getProjectionMatrixInverse() {
        FloatBuffer projection = getProjectionMatrix();
        FloatBuffer projectionInverse = buffer1;
        projectionInverse.clear();
        MatUtil.invertMat4FB(projectionInverse, projection);
        projection.rewind();
        projectionInverse.rewind();
        return projectionInverse;
    }

    /**
     * Call this once before doing any transform to get the <em>view</em> matrix, then load identity and call this again after
     * doing any rendering transform to get the <em>model</em> matrix for the rendered object
     */
    @API(status = MAINTAINED)
    public static FloatBuffer getModelViewMatrix() {
        FloatBuffer modelView = getTmpBuffer();
        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, modelView);
        modelView.rewind();
        return modelView;
    }

    @API(status = MAINTAINED)
    public static FloatBuffer getModelViewMatrixInverse() {
        FloatBuffer modelView = getModelViewMatrix();
        FloatBuffer modelViewInverse = buffer1;
        modelViewInverse.clear();
        MatUtil.invertMat4FB(modelViewInverse, modelView);
        modelView.rewind();
        modelViewInverse.rewind();
        return modelViewInverse;
    }
}
