package csc_cccix.geocracy.graphics;

import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import csc_cccix.geocracy.Util;
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
    protected String vertFile;
    protected String fragFile;
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
            Log.e("", "Failed to read shader file: " + vertFile);
            return false;
        }
        String fragSrc = Util.readTextFile(fragFile);
        if (fragSrc == null) {
            Log.e("", "Failed to read shader file: " + fragFile);
            return false;
        }

        // Create shaders
        int vertShaderHandle = createShader(vertSrc, Type.VERTEX);
        if (vertShaderHandle == 0) {
            Log.e("", "Failed to create vertex shader");
            return false;
        }
        int fragShaderHandle = createShader(fragSrc, Type.FRAGMENT);
        if (fragShaderHandle == 0) {
            Log.e("", "Failed to create fragment shader");
            GLES30.glDeleteShader(vertShaderHandle);
            return false;
        }

        // Create shader program
        programHandle = createProgram(vertShaderHandle, fragShaderHandle);
        if (programHandle == 0) {
            Log.e("", "Failed to create program");
            GLES30.glDeleteShader(vertShaderHandle);
            GLES30.glDeleteShader(fragShaderHandle);
            return false;
        }

        // Shaders can now be deleted
        GLES30.glDeleteShader(vertShaderHandle);
        GLES30.glDeleteShader(fragShaderHandle);

        // setup uniforms
        if (!setupUniforms()) {
            Log.e("", "Failed to setup uniforms");
            return false;
        }

        return true;
    }

    public void unload() {
        if (programHandle != 0 && GLES30.glIsProgram(programHandle)) {
            GLES30.glDeleteProgram(programHandle);
            programHandle = 0;
        }
    }

    public void setActive() {
        if (programHandle == 0) {
            Log.e("", "Invalid program handle");
            return;
        }

        GLES30.glUseProgram(programHandle);
    }

    public String getName() { return name; }

    public String getVertFile() { return vertFile; }

    public String getFragFile() { return fragFile; }

    public int getProgramHandle() { return programHandle; }

    protected boolean setupUniforms() { return true; }

    protected int getUniformLocation(String name) { return GLES30.glGetUniformLocation(programHandle, name); }

    protected void uploadUniform(int handle, boolean v) { GLES30.glUniform1i(handle, v ? 1 : 0); }

    protected void uploadUniform(int handle, float v) { GLES30.glUniform1f(handle, v); }

    protected void uploadUniform(int handle, Vec2 v) { GLES30.glUniform2f(handle, v.x, v.y); }

    protected void uploadUniform(int handle, Vec3 v) { GLES30.glUniform3f(handle, v.x, v.y, v.z); }

    protected void uploadUniform(int handle, Vec4 v) { GLES30.glUniform4f(handle, v.x, v.y, v.z, v.w); }

    protected void uploadUniform(int handle, int v, boolean signed) {
        if (signed) {
            GLES30.glUniform1i(handle, v);
        }
        else {
            GLES30.glUniform1ui(handle, v);
        }
    }

    protected void uploadUniform(int handle, Vec2i v) { GLES30.glUniform2i(handle, v.x, v.y); }

    protected void uploadUniform(int handle, Vec3i v) { GLES30.glUniform3i(handle, v.x, v.y, v.z); }

    protected void uploadUniform(int handle, Vec4i v) { GLES30.glUniform4i(handle, v.x, v.y, v.z, v.w); }

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

    protected void uploadUniform(int handle, float[] vs) {
        GLES30.glUniform1fv(handle, vs.length, vs, 0);
    }

    protected void uploadUniform(int handle, Vec3[] vs) {
        ByteBuffer bb = ByteBuffer.allocateDirect(vs.length * 3 * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        for (Vec3 v : vs) { fb.put(v.x); fb.put(v.y); fb.put(v.z); }
        fb.flip();
        GLES30.glUniform3fv(handle, vs.length, fb);
    }

    protected void uploadUniform(int handle, int[] vs, boolean signed) {
        if (signed) {
            GLES30.glUniform1iv(handle, vs.length, vs, 0);
        }
        else {
            GLES30.glUniform1uiv(handle, vs.length, vs, 0);
        }
    }

    private static int createShader(String src, Type type) {
        int shaderHandle = 0;
        if (type == Type.VERTEX) {
            shaderHandle = GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER);

        }
        else if (type == Type.FRAGMENT) {
            shaderHandle = GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER);

        }
        if (shaderHandle == 0) {
            Log.e("", "Failed to create shader");
            return 0;
        }

        // Compile shader
        GLES30.glShaderSource(shaderHandle, src);
        GLES30.glCompileShader(shaderHandle);

        // Check for compile errors
        int[] status = { 0 };
        GLES30.glGetShaderiv(shaderHandle, GLES30.GL_COMPILE_STATUS, status, 0);
        if (status[0] == 0) {
            Log.e("", "Failed to compile shader");
            Log.e("", GLES30.glGetShaderInfoLog(shaderHandle));
            GLES30.glDeleteShader(shaderHandle);
            return 0;
        }

        // Check for OpenGL errors
        if (Util.isGLError()) {
            Log.e("", "OpenGL error");
            GLES30.glDeleteShader(shaderHandle);
            return 0;
        }

        return shaderHandle;
    }

    private static int createProgram(int vertShaderHandle, int fragShaderHandle) {
        int programHandle = GLES30.glCreateProgram();
        if (programHandle == 0) {
            Log.e("", "Failed to create program");
            return 0;
        }

        // Attach and link shaders to form program
        GLES30.glAttachShader(programHandle, vertShaderHandle);
        GLES30.glAttachShader(programHandle, fragShaderHandle);
        GLES30.glLinkProgram(programHandle);

        // Check for linking errors
        int[] status = { 0 };
        GLES30.glGetProgramiv(programHandle, GLES30.GL_LINK_STATUS, status, 0);
        if (status[0] == 0) {
            Log.e("", "Failed to link program");
            Log.e("", GLES30.glGetProgramInfoLog(programHandle));
            GLES30.glDeleteProgram(programHandle);
            return 0;
        }

        // Check for OpenGL errors
        if (Util.isGLError()) {
            Log.e("", "OpenGL error");
            GLES30.glDeleteProgram(programHandle);
            return 0;
        }

        return programHandle;
    }

}
