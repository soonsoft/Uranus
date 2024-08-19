package com.soonsoft.uranus.services.membership.service;

import java.util.Date;

import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.security.entity.security.SecurityUser;
import com.soonsoft.uranus.services.membership.config.MembershipConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { MembershipConfig.class })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserServiceTest {

    @Autowired
    @Qualifier("membershipUserService")
    private UserService userService;

    @Test
    public void test_getUser() {
        UserInfo user = userService.getUser("admin");
        Assert.assertNotNull(user);
    }

    @Test
    public void test_createUser() {
        String password = "1";
        SecurityUser userInfo = new SecurityUser("zhousong", password, null, null);
        userInfo.setNickName("周SOON");
        userInfo.setCellPhone("18666229900");
        userInfo.setCreateTime(new Date());

        boolean result = userService.createUser(userInfo);
        Assert.assertTrue(result);
    }

    @Test
    public void test_deleteUser() {
        boolean result = userService.deleteUser("zhousong");
        Assert.assertTrue(result);
    }
    
}