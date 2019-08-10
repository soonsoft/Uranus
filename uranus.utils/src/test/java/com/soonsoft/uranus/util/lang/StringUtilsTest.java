package com.soonsoft.uranus.util.lang;

import org.junit.Assert;
import org.junit.Test;

/**
 * StringUtilsTest
 */
public class StringUtilsTest {

    @Test
    public void test_isEmpty() {
        Assert.assertTrue(StringUtils.isEmpty(null));
        Assert.assertTrue(StringUtils.isEmpty(""));
        Assert.assertFalse(StringUtils.isEmpty("hello world"));
        Assert.assertFalse(StringUtils.isEmpty(" "));
    }

    @Test
    public void test_isBlank() {
        Assert.assertTrue(StringUtils.isBlank(null));
        Assert.assertTrue(StringUtils.isBlank(""));
        Assert.assertTrue(StringUtils.isBlank("  "));
        Assert.assertFalse(StringUtils.isEmpty("abc"));
        Assert.assertFalse(StringUtils.isEmpty(" abc  "));
    }
}