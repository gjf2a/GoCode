package com.stack.gocode;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by gabriel on 11/12/18.
 */

public class ModUnitTest {
    @Test
    public void mod_wraparound() {
        assertEquals(3, Util.wrap(0, -1, 4));
    }
}
