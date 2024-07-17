package com.soonsoft.uranus.security.simple.service;

import com.soonsoft.uranus.security.entity.SecurityUser;
import com.soonsoft.uranus.security.entity.UserInfo;

import org.junit.Assert;
import org.junit.Test;


public class SimpleUserManagerTest {

    private SimpleUserManager userManager = new SimpleUserManager(null);

    {
        SecurityUser userInfo = new SecurityUser("zhousong", "1", "0911", null);
        userInfo.setCellPhone("13605160000");
        userInfo.setNickName("周大侠");
        userManager.add(userInfo);
    }

    @Test
    public void test_getUser() {
        UserInfo userInfo = userManager.getUser("zhousong");
        Assert.assertTrue(userInfo.getUserName().equals("zhousong"));
        System.out.println(userInfo);
    }

}