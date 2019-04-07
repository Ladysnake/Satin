package ladysnake.satin.api.util;

import org.junit.jupiter.api.Test;

import java.nio.BufferUnderflowException;
import java.nio.FloatBuffer;

import static org.junit.jupiter.api.Assertions.*;

class GlMatricesTest {
    @Test
    void tmpBufferGetsCleared() {
        FloatBuffer buf = GlMatrices.getTmpBuffer();
        buf.put(3f);
        buf.flip();
        assertEquals(3, buf.get(), "buffer doesn't work as expected");
        buf.clear();
        buf.put(3f);
        FloatBuffer testBuf = GlMatrices.getTmpBuffer();
        testBuf.flip();
        assertThrows(BufferUnderflowException.class, testBuf::get, "buffer didn't get cleared");
    }

    @Test
    void invertMat4() {
        float[] original = {1, 1, 1, -1, 1, 1, -1, 1, 1, -1, 1, 1, -1, 1, 1, 1};
        float[] a = new float[16];
        System.arraycopy(original, 0, a, 0, 16);
        float[] ret = new float[16];
        float[] oracle = {0.25f, 0.25f, 0.25f, -0.25f, 0.25f, 0.25f, -0.25f, 0.25f, 0.25f, -0.25f, 0.25f, 0.25f, -0.25f, 0.25f, 0.25f, 0.25f};
        GlMatrices.invertMat4(ret, a);
        assertArrayEquals(oracle, ret, "Result of 4x4 matrix invert is mathematically incorrect");
        assertArrayEquals(original, a, "Computation of 4x4 matrix invert has altered input");
        GlMatrices.invertMat4(a, a);
        assertArrayEquals(oracle, a, "Result of 4x4 matrix invert in place is incorrect");
    }

    @Test
    void mul() {
        float[] a = {3, 1, 4, 1, 5, 2, 5, 6, 7, 8, 3, 7, 8, 7, -2, 9};
        float[] b = {2, 3, 1, 10, 8, -5, 4, -2, 6, 6, 9, 5, 9, 7, -3, 2};
        float[] ret = new float[16];
        GlMatrices.multiplyMat4(ret, a, b);
        float[] oracle = {108, 86, 6, 117, 11, 16, 23, -12, 151, 125, 71, 150, 57, 13, 58, 48};
        assertArrayEquals(oracle, ret, "Result of 4x4 matrix product is mathematically incorrect");
    }
}