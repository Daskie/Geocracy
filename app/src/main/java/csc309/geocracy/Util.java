package csc309.geocracy;

import android.opengl.GLES30;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import glm_.vec2.Vec2;
import glm_.vec3.Vec3;
import glm_.vec4.Vec4;

import static android.opengl.GLU.gluErrorString;

public abstract class Util {

    // Reads text file from assets folder into string
    public static String readTextFile(String filename) {

        try {
            InputStream is = Global.getContext().getAssets().open(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder lines = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                lines.append(line);
                lines.append("\n");
            }
            return lines.toString();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Checks the OpenGL state for an error and logs it if found
    public static boolean isGLError() {
        int error = GLES30.glGetError();
        if (error == GLES30.GL_NO_ERROR) {
            return false;
        }

        Log.e("Util", "OpenGL error: " + Integer.toHexString(error) + "(" + gluErrorString(error) + ")");
        return true;
    }

    // Returns a vector rotated 90 degrees CCW
    public static Vec2 orthogonal(Vec2 v) {
        return new Vec2(-v.y, v.x.floatValue());
    }

    // For use with floating point comparisons
    public static final float EPSILON = 1e-6f;

    public static boolean isZero(float v) {
        return Math.abs(v) < EPSILON;
    }

    public static boolean isZero(Vec2 v) {
        return isZero(v.x) && isZero(v.y);
    }

    public static boolean isZero(Vec3 v) {
        return isZero(v.x) && isZero(v.y) && isZero(v.z);
    }

    public static boolean isZero(Vec4 v) {
        return isZero(v.x) && isZero(v.y) && isZero(v.z) && isZero(v.w);
    }

}
