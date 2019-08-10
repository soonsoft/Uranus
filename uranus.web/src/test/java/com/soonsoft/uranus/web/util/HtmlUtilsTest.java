package com.soonsoft.uranus.web.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * HtmlUtilsTest
 */
public class HtmlUtilsTest {

    @Test
    public void test_toJSON() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "<img src='1' onerror='alert(123)'/>");
        String json = HtmlUtils.toJSON(params);
        Assert.assertNotNull(json);
    }
}