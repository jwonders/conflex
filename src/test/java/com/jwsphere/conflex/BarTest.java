package com.jwsphere.conflex;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;

public class BarTest {

    @Test
    public void injectProperties() {
        Properties p = new Properties();
        p.put("foo", "value");

        Bar foo = new Bar(p);
        assertEquals("value", foo.getFoo());
    }

}
