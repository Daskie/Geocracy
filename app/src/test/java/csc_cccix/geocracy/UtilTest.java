package csc_cccix.geocracy;

import org.junit.Test;

import csc_cccix.geocracy.Util;
import csc_cccix.geocracy.game.GameData;
import glm_.vec2.Vec2;
import glm_.vec3.Vec3;
import glm_.vec4.Vec4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UtilTest {

    @Test
    public void pow2() {
        assertEquals(4, Util.pow2(2));
        assertEquals(1, Util.pow2(0));
    }

    @Test
    public void isPow2() {
        assertTrue(Util.isPow2(4));
        assertTrue(Util.isPow2(1024));
        assertTrue(Util.isPow2(512));

        assertFalse(Util.isPow2(3));
        assertFalse(Util.isPow2(14));
    }

    @Test
    public void isZero() {
        assertTrue(Util.isZero(0.0f));
        assertTrue(Util.isZero(new Vec2(0.0f, 0.0f)));
        assertTrue(Util.isZero(new Vec3(0.0f, 0.0f, 0.0f)));
        assertTrue(Util.isZero(new Vec4(0.0f, 0.0f, 0.0f, 0.0f)));
    }

    @Test
    public void areEqual() {
        assertTrue(Util.areEqual(1.1f, 1.1f));
        assertTrue(Util.areEqual(new Vec2(1.1f, 1.1f), new Vec2(1.1f, 1.1f)));
        assertTrue(Util.areEqual(new Vec3(1.1f, 1.1f, 1.1f), new Vec3(1.1f, 1.1f, 1.1f)));
        assertTrue(Util.areEqual(new Vec4(1.1f, 1.1f, 1.1f, 1.1f), new Vec4(1.1f, 1.1f, 1.1f, 1.1f)));
    }

    @Test
    public void assign() {
        Vec3 src = new Vec3(1.1f, 1.1f, 1.1f);
        Vec3 dst = new Vec3();
        Util.assign(dst, src);
        assertEquals(src.x, dst.x);
        assertEquals(src.y, dst.y);
        assertEquals(src.z, dst.z);
    }

    @Test
    public void clamp() {
        int low = 0;
        int high = 10;
        assertEquals(low, Util.clamp(-1, low, high));
        assertEquals(high, Util.clamp(11, low, high));
    }

}