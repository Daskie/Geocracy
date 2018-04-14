package csc309.geocracy.graphics;

import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;

import csc309.geocracy.Util;
import glm_.vec2.Vec2i;

public class Texture {

    public enum Wrap { EDGE, REPEAT, MIRROR }
    public enum Filter { NEAREST, LINEAR }

    protected String name;
    protected Vec2i size;
    protected Wrap wrap;
    protected Filter filter;
    protected boolean mipmap;
    protected int handle;

    public Texture(String name, Vec2i size, Wrap wrap, Filter filter, boolean mipmap) {
        this.name = name;
        this.size = size;
        this.wrap = wrap;
        this.filter = filter;
        this.mipmap = mipmap;
    }

    public boolean load() {
        unload();

        int[] handleArr = { 0 };
        GLES30.glGenTextures(1, handleArr, 0);
        handle = handleArr[0];
        if (handle == 0) {
            Log.e("Texture", "Failed to generate texture");
            return false;
        }

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, handle);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, translateWrap(wrap));
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, translateWrap(wrap));
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, translateFilter(filter, mipmap));
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, translateFilter(filter, false));
        GLES30.glTexStorage2D(GLES30.GL_TEXTURE_2D, mipmap ? calcMipmapLevels(size) : 1, GLES30.GL_RGB8, size.x, size.y);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);

        if (Util.isGLError()) {
            return false;
        }

        return true;
    }

    public boolean upload(ByteBuffer pixelData) {
        if (handle == 0) {
            Log.e("Texture", "Invalid handle");
            return false;
        }
        if (pixelData.limit() != size.x * size.y * 3) {
            Log.e("Texture", "Invalid pixelData dimensions");
            return false;
        }

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, handle);
        GLES30.glTexSubImage2D(GLES30.GL_TEXTURE_2D, 0, 0, 0, size.x, size.y, GLES30.GL_RGB, GLES30.GL_UNSIGNED_BYTE, pixelData);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);

        if (Util.isGLError()) {
            return false;
        }

        return true;
    }

    public boolean upload(byte[] pixelData) {
        ByteBuffer bb = ByteBuffer.allocateDirect(size.x * size.y * 3);
        bb.put(pixelData);
        bb.flip();
        return upload(bb);
    }

    public void unload() {
        if (handle != 0) {
            int[] handleArr = { handle };
            GLES30.glDeleteTextures(1, handleArr, 0);
            handle = 0;
        }
    }

    public String getName() { return name; }

    public int getHandle() { return handle; }

    private static int translateWrap(Wrap wrap) {
        switch (wrap) {
            case EDGE: return GLES30.GL_CLAMP_TO_EDGE;
            case REPEAT: return GLES30.GL_REPEAT;
            case MIRROR: return GLES30.GL_MIRRORED_REPEAT;
            default: return 0;
        }
    }

    private static int translateFilter(Filter filter, boolean mipmap) {
        if (mipmap) {
            switch (filter) {
                case NEAREST: return GLES30.GL_NEAREST_MIPMAP_NEAREST;
                case LINEAR: return GLES30.GL_LINEAR_MIPMAP_LINEAR;
                default: return 0;
            }
        }
        else {
            switch (filter) {
                case NEAREST: return GLES30.GL_NEAREST;
                case LINEAR: return GLES30.GL_LINEAR;
                default: return 0;
            }
        }
    }

    private static int calcMipmapLevels(Vec2i size) {
        return Util.log2Floor(Math.max(size.x, size.y)) + 1;
    }

}
