package csc_cccix;

import org.junit.Test;

import csc_cccix.geocracy.Util;

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
}