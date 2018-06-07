package csc_cccix.geocracy;

import org.junit.Test;

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
    public void orthogonal() {
        Vec2 ortho = Util.ortho(new Vec2(1.0f, 0.0f));
        assertEquals(ortho.x, 0.0f, 0.0f);
        assertEquals(ortho.y, 1.0f, 0.0f);
    }

    @Test
    public void clamp() {
        int low = 0;
        int high = 10;
        assertEquals(low, Util.clamp(-1, low, high));
        assertEquals(high, Util.clamp(11, low, high));
    }

    @Test
    public void toLong() {
        assertEquals(0xAAAAAAAABBBBBBBBL, Util.toLong(0xBBBBBBBB, 0xAAAAAAAA));
        assertEquals(0xFFFFFFFF00000000L, Util.toLong(0x00000000, 0xFFFFFFFF));
        assertEquals(0x00000000FFFFFFFFL, Util.toLong(0xFFFFFFFF, 0x00000000));
    }

    @Test
    public void fromLong() {
        assertEquals(0xBBBBBBBB, Util.fromLongLower(0xAAAAAAAABBBBBBBBL));
        assertEquals(0xAAAAAAAA, Util.fromLongUpper(0xAAAAAAAABBBBBBBBL));
        assertEquals(0x00000000, Util.fromLongLower(0xFFFFFFFF00000000L));
        assertEquals(0xFFFFFFFF, Util.fromLongUpper(0xFFFFFFFF00000000L));
        assertEquals(0xFFFFFFFF, Util.fromLongLower(0x00000000FFFFFFFFL));
        assertEquals(0x00000000, Util.fromLongUpper(0x00000000FFFFFFFFL));
    }

    @Test
    public void toInt() {
        assertEquals(0xAAAABBBB, Util.toInt((short)0xBBBB, (short)0xAAAA));
        assertEquals(0xFFFF0000, Util.toInt((short)0x0000, (short)0xFFFF));
        assertEquals(0x0000FFFF, Util.toInt((short)0xFFFF, (short)0x0000));

        assertEquals(0xAABBCCDD, Util.toInt((byte)0xDD, (byte)0xCC, (byte)0xBB, (byte)0xAA));
    }

    @Test
    public void cylindricToCartesian() {
        Vec3 v = Util.cylindricToCartesian((float)Math.sqrt(2.0), (float)(Math.PI / 4.0), 1.0f);
        assertEquals(1.0f, v.x, 1e-6f);
        assertEquals(1.0f, v.y, 1e-6f);
        assertEquals(1.0f, v.z, 1e-6f);
    }

}