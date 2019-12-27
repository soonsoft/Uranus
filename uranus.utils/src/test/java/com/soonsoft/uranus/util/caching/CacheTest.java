package com.soonsoft.uranus.util.caching;

import org.junit.Assert;
import org.junit.Test;

public class CacheTest {

    private Cache<String, String> cache = new Cache<>(128);

    @Test
    public void test_get() {
        cache.put("key", "value");
        Assert.assertEquals(cache.get("key"), "value");
    }
    
}