package com.soonsoft.uranus.security.simple.service;

import com.soonsoft.uranus.security.entity.UserInfo;

import org.junit.Assert;
import org.junit.Test;


public class SimpleUserManagerTest {

    private SimpleUserManager userManager = new SimpleUserManager(null);

    {
        UserInfo userInfo = new UserInfo("zhousong", "1");
        userInfo.setCellPhone("13605160000");
        userInfo.setNickName("周大侠");
        userInfo.setPasswordSalt("0911");
        userManager.add(userInfo);
    }

    @Test
    public void test_getUser() {
        UserInfo userInfo = userManager.getUser("zhousong");
        Assert.assertTrue(userInfo.getUsername().equals("zhousong"));
        System.out.println(userInfo);
    }

}