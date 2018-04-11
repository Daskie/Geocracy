package csc309.geocracy;

import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import glm_.mat2x2.Mat2;
import glm_.mat3x3.Mat3;
import glm_.mat4x4.Mat4;
import glm_.vec2.Vec2;
import glm_.vec2.Vec2i;
import glm_.vec3.Vec3;
import glm_.vec3.Vec3i;
import glm_.vec4.Vec4;
import glm_.vec4.Vec4i;

public abstract class Shader {

    public enum Type { VERTEX, FRAGMENT }

    protected String name;
    protected String vertFile, fragFile;
    protected int programHandle;

    public Shader(String name, String vertFile, String fragFile) {
        this.name = name;
        this.vertFile = vertFile;
        this.fragFile = fragFile;
    }

    public boolean load() {
        unload();

        // Read in shader files
        String vertSrc = Util.readTextFile(vertFile);
        if (vertSrc == null) {
            Log.e("Shader", "Failed to read shader file: " + vertFile);
            return false;
        }
        String fragSrc = Util.readTextFile(fragFile);
        if (fragSrc == null) {
            Log.e("Shader", "Failed to read shader file: " + fragFile);
            return false;
        }

        // Create shaders
        int vertShaderHandle = createShader(vertSrc, Type.VERTEX);
        if (vertShaderHandle == 0) {
            Log.e("Shader", "Failed to create vertex shader");
            return false;
        }
        int fragShaderHandle = createShader(fragSrc, Type.FRAGMENT);
        if (fragShaderHandle == 0) {
            Log.e("Shader", "Failed to create fragment shader");
            GLES30.glDeleteShader(vertShaderHandle);
            return false;
        }

        // Create shader program
        programHandle = createProgram(vertShaderHandle, fragShaderHandle);
        if (programHandle == 0) {
            Log.e("Shader", "Failed to create program");
            GLES30.glDeleteShader(vertShaderHandle);
            GLES30.glDeleteShader(fragShaderHandle);
            return false;
        }

        // Shaders can now be deleted
        GLES30.glDeleteShader(vertShaderHandle);
        GLES30.glDeleteShader(fragShaderHandle);

        // setup uniforms
        if (!setupUniforms()) {
            Log.e("Shader", "Failed to setup uniforms");
            return false;
        }

        return true;
    }

    public void unload() {
        if (programHandle != 0) {
            GLES30.glDeleteProgram(programHandle);
            programHandle = 0;
        }
    }

    public void setActive() {
        if (programHandle == 0) {
            Log.e("Shader", "Invalid program handle");
            return;
        }

        GLES30.glUseProgram(programHandle);
    }

    public String getName() { return name; }

    public String getVertFile() { return vertFile; }

    public String getFragFile() { return fragFile; }

    public int getProgramHandle() { return programHandle; }

    protected boolean setupUniforms() { return true; }

    protected int getUniformLocation(String name) {
        return GLES30.glGetUniformLocation(programHandle, name);
    }

    protected void uploadUniform(int handle, boolean v) {
        GLES30.glUniform1i(handle, v ? 1 : 0);
    }

    protected void uploadUniform(int handle, float v) {
        GLES30.glUniform1f(handle, v);
    }

    protected void uploadUniform(int handle, Vec2 v) {
        GLES30.glUniform2f(handle, v.x, v.y);
    }

    protected void uploadUniform(int handle, Vec3 v) {
        GLES30.glUniform3f(handle, v.x, v.y, v.z);
    }

    protected void uploadUniform(int handle, Vec4 v) {
        GLES30.glUniform4f(handle, v.x, v.y, v.z, v.w);
    }

    protected void uploadUniform(int handle, int v) {
        GLES30.glUniform1i(handle, v);
    }
    protected void uploadUniform(int handle, Vec2i v) {
        GLES30.glUniform2i(handle, v.x, v.y);
    }

    protected void uploadUniform(int handle, Vec3i v) {
        GLES30.glUniform3i(handle, v.x, v.y, v.z);
    }

    protected void uploadUniform(int handle, Vec4i v) {
        GLES30.glUniform4i(handle, v.x, v.y, v.z, v.w);
    }

    protected void uploadUniform(int handle, Mat2 v) {
        ByteBuffer bb = ByteBuffer.allocateDirect(2 * 2 * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        v.to(fb);
        GLES30.glUniformMatrix2fv(handle, 1, false, fb);
    }

    protected void uploadUniform(int handle, Mat3 v) {
        ByteBuffer bb = ByteBuffer.allocateDirect(3 * 3 * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        v.to(fb);
        GLES30.glUniformMatrix3fv(handle, 1, false, fb);
    }

    protected void uploadUniform(int handle, Mat4 v) {
        ByteBuffer bb = ByteBuffer.allocateDirect(4 * 4 * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        v.to(fb);
        GLES30.glUniformMatrix4fv(handle, 1, false, fb);
    }

    private static int createShader(String src, Type type) {
        int shaderHandle = 0;
        switch (type) {
            case VERTEX:
                shaderHandle = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER);
                break;
            case FRAGMENT:
                shaderHandle = GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER);
                break;
        }
        if (shaderHandle == 0) {
            Log.e("Shader", "Failed to create shader");
            return 0;
        }

        // Compile shader
        GLES30.glShaderSource(shaderHandle, src);
        GLES30.glCompileShader(shaderHandle);

        // Check for compile errors
        int status[] = { 0 };
        GLES30.glGetShaderiv(shaderHandle, GLES30.GL_COMPILE_STATUS, status, 0);
        if (status[0] == 0) {
            Log.e("Shader", "Failed to compile shader");
            Log.e("Shader", GLES30.glGetShaderInfoLog(shaderHandle));
            GLES30.glDeleteShader(shaderHandle);
            return 0;
        }

        // Check for OpenGL errors
        if (Util.isGLError()) {
            GLES30.glDeleteShader(shaderHandle);
            return 0;
        }

        return shaderHandle;
    }

    static private int createProgram(int vertShaderHandle, int fragShaderHandle) {
        int programHandle = GLES30.glCreateProgram();
        if (programHandle == 0) {
            Log.e("Shader", "Failed to create program");
            return 0;
        }

        // Attach and link shaders to form program
        GLES30.glAttachShader(programHandle, vertShaderHandle);
        GLES30.glAttachShader(programHandle, fragShaderHandle);
        GLES30.glLinkProgram(programHandle);

        // Check for linking errors
        int status[] = { 0 };
        GLES30.glGetProgramiv(programHandle, GLES30.GL_LINK_STATUS, status, 0);
        if (status[0] == 0) {
            Log.e("Shader", "Failed to link program");
            Log.e("Shader", GLES30.glGetProgramInfoLog(programHandle));
            GLES30.glDeleteProgram(programHandle);
            return 0;
        }

        // Check for OpenGL errors
        if (Util.isGLError()) {
            GLES30.glDeleteProgram(programHandle);
            return 0;
        }

        return programHandle;
    }

}
