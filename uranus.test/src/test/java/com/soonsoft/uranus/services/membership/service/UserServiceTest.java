package com.soonsoft.uranus.services.membership.service;

import java.util.Date;

import com.soonsoft.uranus.data.EnableDatabaseAccess;
import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.services.membership.config.DataSourceConfig;
import com.soonsoft.uranus.services.membership.config.MembershipServiceConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * UserServiceTest
 */
@RunWith(SpringRunner.class)
@MybatisTest
@ContextConfiguration(classes = {DataSourceConfig.class, MembershipServiceConfig.class})
@EnableDatabaseAccess
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void test_getUser() {
        createUser();

        UserInfo user = userService.getUser("zhousong");
        Assert.assertNotNull(user);
    }

    private void createUser() {
        String password = userService.encryptPassword("1", null);
        UserInfo userInfo = new UserInfo("zhousong", password);
        userInfo.setNickName("周松");
        userInfo.setCellPhone("18666229900");
        userInfo.setPasswordSalt(null);
        userInfo.setCreateTime(new Date());

        userService.createUser(userInfo);
    }
    
}