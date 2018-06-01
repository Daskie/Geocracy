package cscCCCIX.geocracy.graphics;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import cscCCCIX.geocracy.Util;

public class ScreenTextureRenderer {

    private ScreenTextureShader shader;
    private int vboHandle;
    private int vaoHandle;

    public ScreenTextureRenderer() {
        shader = new ScreenTextureShader();
    }

    public boolean load() {
        unload();

        if (!shader.load()) {
            Log.e("", "Failed to load shader");
            return false;
        }

        // Quad over screen
        float[] points = {
            -1.0f, -1.0f,
             1.0f, -1.0f,
             1.0f,  1.0f,

             1.0f,  1.0f,
            -1.0f,  1.0f,
            -1.0f, -1.0f
        };
        ByteBuffer bb = ByteBuffer.allocateDirect(6 * 2 * 4);
        bb.order(ByteOrder.nativeOrder());
        bb.asFloatBuffer().put(points);

        // Create VBO
        int[] vboHandleArr = { 0 };
        GLES30.glGenBuffers(1, vboHandleArr, 0);
        vboHandle = vboHandleArr[0];
        if (vboHandle == 0) {
            Log.e("", "Failed to generate vbo");
            return false;
        }
        // Upload vbo data
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboHandle);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, bb.limit(), bb, GLES30.GL_STATIC_DRAW);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        // Check for OpenGL errors
        if (Util.isGLError()) {
            Log.e("", "Failed to upload vbo");
            return false;
        }

        // Create VAO
        int[] vaoHandleArr = { 0 };
        GLES30.glGenVertexArrays(1, vaoHandleArr, 0);
        vaoHandle = vaoHandleArr[0];
        if (vaoHandle == 0) {
            Log.e("", "Failed to generate vao");
        }
        // Setup vao attributes and bindings
        GLES30.glBindVertexArray(vaoHandle);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboHandle);
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glVertexAttribPointer(0, 2, GLES30.GL_FLOAT, false, 2 * 4, 0);
        GLES30.glBindVertexArray(0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        GLES30.glDisableVertexAttribArray(0);
        // Check for OpenGL errors
        if (Util.isGLError()) {
            Log.e("", "Failed to setup vao");
            return false;
        }

        return true;
    }

    public void render(int textureHandle) {
        shader.setActive();
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
        GLES30.glBindVertexArray(vaoHandle);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 6);
        GLES30.glBindVertexArray(0);
    }

    public void render(Texture texture) {
        render(texture.getHandle());
    }

    public void unload() {
        shader.unload();
        if (vaoHandle != 0) {
            int[] vaoHandleArr = { vaoHandle };
            GLES30.glDeleteVertexArrays(1, vaoHandleArr, 0);
        }
    }

}
