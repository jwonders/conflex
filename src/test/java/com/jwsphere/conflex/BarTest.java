package com.jwsphere.conflex;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class BarTest {

    @Test
    public void injectProperties() {
        Properties p = new Properties();
        p.put("foo", "value");

        Bar bar = new Bar(p);
        assertEquals("value", bar.getFoo());
    }
    
    @Test
    public void injectPrefixedProperties() {
        Properties p = new Properties();
        p.put("prefix.foo", "value");

        Bar bar = new Bar(p, "prefix.");
        assertEquals("value", bar.getFoo());
    }
    
    @Test
    public void injectPerformance() {
        Properties p = new Properties();
        p.put("foo", "value");
        
        List<Bar> bars = new ArrayList<Bar>(100000);

        long nanos = System.nanoTime();
        for (int i = 0; i < 100000; ++i) {
            bars.add(new Bar(p));
        }
        double duration = System.nanoTime() - nanos;
        double ms = duration / (1e6);
        System.out.println(ms + "ms to instantiate 100000 Bar instances");
        
        for (Bar bar : bars) {
            assertEquals("value", bar.getFoo());
        }
    }

    @Test
    public void injectMultithreaded() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100; ++i) {
            service.submit(new Client());
        }
        service.shutdown();
        service.awaitTermination(1, TimeUnit.SECONDS);
    }
    
    private static class Client implements Runnable {
        @Override
        public void run() {
            Properties p = new Properties();
            p.put("foo", "value");

            Bar foo = new Bar(p);
            assertEquals("value", foo.getFoo());
        }
    }
}
