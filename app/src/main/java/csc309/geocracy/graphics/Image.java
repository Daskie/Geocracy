package csc309.geocracy.graphics;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;

import csc309.geocracy.Util;

public class Image {

    private String filename;
    private ByteBuffer data;
    private int width, height;

    public Image(String filename) {
        this.filename = filename;
    }

    public boolean load() {
        Bitmap bitmap = Util.readBitmap(filename);
        if (bitmap == null) {
            return false;
        }
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        data = ByteBuffer.allocateDirect(width * height * 4);
        bitmap.copyPixelsToBuffer(data);
        data.flip();
        return true;
    }

    public ByteBuffer getData() {
        return data;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
