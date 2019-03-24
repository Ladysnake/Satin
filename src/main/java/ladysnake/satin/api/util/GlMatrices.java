package ladysnake.satin.api.util;

import org.apiguardian.api.API;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.util.Arrays;

import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * This class consists of static methods that operate on matrices.
 */
public final class GlMatrices {
    /**Used for opengl interactions such as matrix retrieval*/
    private static FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
    /**A secondary buffer for the same purpose as {@link #buffer}*/
    private static FloatBuffer buffer1 = BufferUtils.createFloatBuffer(16);
    private static float[] inArray = new float[16];
    private static float[] outArray = new float[16];

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
        invertMat4FB(projectionInverse, projection);
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
        invertMat4FB(modelViewInverse, modelView);
        modelView.rewind();
        modelViewInverse.rewind();
        return modelViewInverse;
    }

    /**
     * Inverts a 4x4 matrix stored in a float array
     *
     * @param matOut 16 capacity array to store the output in
     * @param m      16 values array storing the matrix to invert
     */
    @API(status = MAINTAINED)
    public static void invertMat4(float[] matOut, float[] m) {
        matOut[0] = (m[5] * m[10] * m[15] - m[5] * m[11] * m[14] - m[9] * m[6] * m[15] + m[9] * m[7] * m[14] + m[13] * m[6] * m[11] - m[13] * m[7] * m[10]);
        matOut[1] = (-m[1] * m[10] * m[15] + m[1] * m[11] * m[14] + m[9] * m[2] * m[15] - m[9] * m[3] * m[14] - m[13] * m[2] * m[11] + m[13] * m[3] * m[10]);
        matOut[2] = (m[1] * m[6] * m[15] - m[1] * m[7] * m[14] - m[5] * m[2] * m[15] + m[5] * m[3] * m[14] + m[13] * m[2] * m[7] - m[13] * m[3] * m[6]);
        matOut[3] = (-m[1] * m[6] * m[11] + m[1] * m[7] * m[10] + m[5] * m[2] * m[11] - m[5] * m[3] * m[10] - m[9] * m[2] * m[7] + m[9] * m[3] * m[6]);
        matOut[4] = (-m[4] * m[10] * m[15] + m[4] * m[11] * m[14] + m[8] * m[6] * m[15] - m[8] * m[7] * m[14] - m[12] * m[6] * m[11] + m[12] * m[7] * m[10]);
        matOut[5] = (m[0] * m[10] * m[15] - m[0] * m[11] * m[14] - m[8] * m[2] * m[15] + m[8] * m[3] * m[14] + m[12] * m[2] * m[11] - m[12] * m[3] * m[10]);
        matOut[6] = (-m[0] * m[6] * m[15] + m[0] * m[7] * m[14] + m[4] * m[2] * m[15] - m[4] * m[3] * m[14] - m[12] * m[2] * m[7] + m[12] * m[3] * m[6]);
        matOut[7] = (m[0] * m[6] * m[11] - m[0] * m[7] * m[10] - m[4] * m[2] * m[11] + m[4] * m[3] * m[10] + m[8] * m[2] * m[7] - m[8] * m[3] * m[6]);
        matOut[8] = (m[4] * m[9] * m[15] - m[4] * m[11] * m[13] - m[8] * m[5] * m[15] + m[8] * m[7] * m[13] + m[12] * m[5] * m[11] - m[12] * m[7] * m[9]);
        matOut[9] = (-m[0] * m[9] * m[15] + m[0] * m[11] * m[13] + m[8] * m[1] * m[15] - m[8] * m[3] * m[13] - m[12] * m[1] * m[11] + m[12] * m[3] * m[9]);
        matOut[10] = (m[0] * m[5] * m[15] - m[0] * m[7] * m[13] - m[4] * m[1] * m[15] + m[4] * m[3] * m[13] + m[12] * m[1] * m[7] - m[12] * m[3] * m[5]);
        matOut[11] = (-m[0] * m[5] * m[11] + m[0] * m[7] * m[9] + m[4] * m[1] * m[11] - m[4] * m[3] * m[9] - m[8] * m[1] * m[7] + m[8] * m[3] * m[5]);
        matOut[12] = (-m[4] * m[9] * m[14] + m[4] * m[10] * m[13] + m[8] * m[5] * m[14] - m[8] * m[6] * m[13] - m[12] * m[5] * m[10] + m[12] * m[6] * m[9]);
        matOut[13] = (m[0] * m[9] * m[14] - m[0] * m[10] * m[13] - m[8] * m[1] * m[14] + m[8] * m[2] * m[13] + m[12] * m[1] * m[10] - m[12] * m[2] * m[9]);
        matOut[14] = (-m[0] * m[5] * m[14] + m[0] * m[6] * m[13] + m[4] * m[1] * m[14] - m[4] * m[2] * m[13] - m[12] * m[1] * m[6] + m[12] * m[2] * m[5]);
        matOut[15] = (m[0] * m[5] * m[10] - m[0] * m[6] * m[9] - m[4] * m[1] * m[10] + m[4] * m[2] * m[9] + m[8] * m[1] * m[6] - m[8] * m[2] * m[5]);

        float det = m[0] * matOut[0] + m[1] * matOut[4] + m[2] * matOut[8] + m[3] * matOut[12];
        if (det != 0.0D) {
            for (int i = 0; i < 16; i++) {
                matOut[i] /= det;
            }
        } else {
            Arrays.fill(matOut, 0.0F);
        }
    }

    /**
     * Inverts a matrix stored in a {@link FloatBuffer}
     *
     * @param fbInvOut an output buffer in which the result will be stored
     * @param fbMatIn  an input buffer storing the matrix to invert
     */
    @API(status = MAINTAINED)
    public static void invertMat4FB(FloatBuffer fbInvOut, FloatBuffer fbMatIn) {
        invertMat4FBFA(fbInvOut, fbMatIn, outArray, inArray);
    }

    /**
     * Inverts a matrix stored in a {@link FloatBuffer} using intermediary float arrays
     *
     * @param fbInvOut an output buffer in which the result will be stored
     * @param fbMatIn  an input buffer storing the matrix to invert
     * @param faInv    16 capacity array
     * @param faMat    16 capacity array
     */
    private static void invertMat4FBFA(FloatBuffer fbInvOut, FloatBuffer fbMatIn, float[] faInv, float[] faMat) {
        fbMatIn.get(faMat);
        invertMat4(faInv, faMat);
        fbInvOut.put(faInv);
    }
}
