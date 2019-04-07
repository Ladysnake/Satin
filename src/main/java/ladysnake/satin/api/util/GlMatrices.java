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

    /**
     * Multiplies together two 4x4 matrices stored in float arrays.
     * <p>
     * It is safe for the destination array to be one of the operands, as the
     * result is stored in intermediate variables.
     *
     * @param dest 16 capacity array to store the output in
     * @param left 16 values array storing the left operand of the matrix product
     * @param right 16 values array storing the right operand of the matrix product
     */
    @API(status = MAINTAINED)
    public static float[] multiplyMat4(float[] dest, float[] left, float[] right) {
        float m00 = left[0] * right[0]  + left[4] * right[1]  + left[8]  * right[2]  + left[12] * right[3];
        float m01 = left[1] * right[0]  + left[5] * right[1]  + left[9]  * right[2]  + left[13] * right[3];
        float m02 = left[2] * right[0]  + left[6] * right[1]  + left[10] * right[2]  + left[14] * right[3];
        float m03 = left[3] * right[0]  + left[7] * right[1]  + left[11] * right[2]  + left[15] * right[3];
        float m10 = left[0] * right[4]  + left[4] * right[5]  + left[8]  * right[6]  + left[12] * right[7];
        float m11 = left[1] * right[4]  + left[5] * right[5]  + left[9]  * right[6]  + left[13] * right[7];
        float m12 = left[2] * right[4]  + left[6] * right[5]  + left[10] * right[6]  + left[14] * right[7];
        float m13 = left[3] * right[4]  + left[7] * right[5]  + left[11] * right[6]  + left[15] * right[7];
        float m20 = left[0] * right[8]  + left[4] * right[9]  + left[8]  * right[10] + left[12] * right[11];
        float m21 = left[1] * right[8]  + left[5] * right[9]  + left[9]  * right[10] + left[13] * right[11];
        float m22 = left[2] * right[8]  + left[6] * right[9]  + left[10] * right[10] + left[14] * right[11];
        float m23 = left[3] * right[8]  + left[7] * right[9]  + left[11] * right[10] + left[15] * right[11];
        float m30 = left[0] * right[12] + left[4] * right[13] + left[8]  * right[14] + left[12] * right[15];
        float m31 = left[1] * right[12] + left[5] * right[13] + left[9]  * right[14] + left[13] * right[15];
        float m32 = left[2] * right[12] + left[6] * right[13] + left[10] * right[14] + left[14] * right[15];
        float m33 = left[3] * right[12] + left[7] * right[13] + left[11] * right[14] + left[15] * right[15];

        dest[0]  = m00;
        dest[1] = m01;
        dest[2] = m02;
        dest[3] = m03;
        dest[4] = m10;
        dest[5] = m11;
        dest[6] = m12;
        dest[7] = m13;
        dest[8] = m20;
        dest[9] = m21;
        dest[10] = m22;
        dest[11] = m23;
        dest[12] = m30;
        dest[13] = m31;
        dest[14] = m32;
        dest[15] = m33;

        return dest;
    }

    /**
     * Multiplies together two 4x4 matrices stored in float buffers.
     * <p>
     * It is safe for the destination buffer to be one of the operands, as the
     * result is stored in intermediate variables.
     *
     * @param fbInvOut 16 capacity array to store the output in
     * @param fbMatLeft 16 values array storing the left operand of the matrix product
     * @param fbMatRight 16 values array storing the right operand of the matrix product
     */
    @API(status = MAINTAINED)
    public static void multiplyMat4FB(FloatBuffer fbInvOut, FloatBuffer fbMatLeft, FloatBuffer fbMatRight) {
        float[] matLeft = inArray;
        float[] matRight = outArray;
        fbMatLeft.get(matLeft);
        fbMatRight.get(matRight);
        multiplyMat4(matLeft, matLeft, matRight);
        fbInvOut.put(matLeft);
    }
}
