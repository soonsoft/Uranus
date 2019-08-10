package com.soonsoft.uranus.web.mvc.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * RequestDataTest
 */
public class RequestDataTest {

    @Test
    public void test_get() {
        RequestData data = new RequestData();
        data.put("name", "zhousong");
        data.put("age", "34");
        data.put("boolean", "true");
        data.put("number", "1.5678");
        data.put("date", "2019-05-09 20:44:33");
        data.put("jsonDate1", "2019-05-09T13:17:45.950Z");
        data.put("jsonDate2", "2019-05-09T12:54:58.950+08:00");

        Assert.assertTrue("zhousong".equals(data.get("name")));
        Assert.assertTrue("Jack".equals(data.get("master", "Jack")));
        Assert.assertNotNull(data.getBoolean("boolean"));
        Assert.assertNotNull(data.getInteger("age"));
        Assert.assertNotNull(data.getFloat("number"));
        Assert.assertNotNull(data.getDouble("number"));
        Assert.assertNotNull(data.getBigDecimal("number"));
        Assert.assertNotNull(data.getDate("date"));
        Assert.assertNotNull(data.getJsonDate("jsonDate2"));
        Assert.assertNotNull(data.getJsonDate("jsonDate1"));
    }

}