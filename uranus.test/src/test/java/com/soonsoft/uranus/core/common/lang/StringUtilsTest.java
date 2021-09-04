package com.soonsoft.uranus.core.common.lang;

import org.junit.Assert;
import org.junit.Test;

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

    @Test
    public void test_format() {
        Assert.assertTrue("hello world.".equals(StringUtils.format("hell{0} w{1}rld.", "o", "o")));
        Assert.assertTrue("别用大炮打蚊子".equals(StringUtils.format("别用{0}打{1}{2}", "大炮", "蚊", "子")));
        Assert.assertTrue("hello".equals(StringUtils.format("he{0}{0}o", "l")));
        Assert.assertTrue("hello".equals(StringUtils.format("{0}ello", "h")));
        Assert.assertTrue("hello".equals(StringUtils.format("hell{0}", "o")));
        Assert.assertTrue("hello".equals(StringUtils.format("{1}ell{0}", "o", "h")));
        Assert.assertTrue("{0}".equals(StringUtils.format("{{{0}}}", "0")));
        Assert.assertTrue("StringUtils.format(\"abc{0}efg\", \"d\")".equals(StringUtils.format("StringUtils.format(\"abc{{0}}efg\", \"{0}\")", "d")));
        Assert.assertTrue("}{}aaa".equals(StringUtils.format("}}{{}}{0}", "aaa")));
    }
}