package com.soonsoft.uranus.core.common.extension;

import org.junit.Assert;
import org.junit.Test;

public class ExtensionLoaderTest {

    @Test
    public void test_getInstance() {
        ITestService service1 = ExtensionLoader.load(ITestService.class).getInstance("Service1");
        ITestService service2 = ExtensionLoader.load(ITestService.class).getInstance("Service2");

        Assert.assertEquals(service1.action(), TestServiceImpl1.class.getSimpleName());
        Assert.assertEquals(service2.action(), TestServiceImpl2.class.getSimpleName());
    }
}