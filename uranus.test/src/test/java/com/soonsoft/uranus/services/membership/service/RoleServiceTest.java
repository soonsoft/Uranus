package com.soonsoft.uranus.services.membership.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.data.entity.Page;
import com.soonsoft.uranus.services.membership.config.DataSourceConfig;
import com.soonsoft.uranus.services.membership.config.MembershipServiceConfig;
import com.soonsoft.uranus.services.membership.dto.AuthRole;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
/**
 * RoleServiceTest
 */
@RunWith(SpringRunner.class)
@MybatisTest
@ContextConfiguration(classes = {DataSourceConfig.class, MembershipServiceConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RoleServiceTest {

    @Autowired
    private RoleService roleService;

    @Test
    public void test_queryRoles() {
        Map<String, Object> params = new HashMap<>();
        Page page = new Page(1, 1);

        List<AuthRole> roles = roleService.queryRoles(params, page);
        Assert.assertNotNull(roles);
        Assert.assertTrue(roles.size() == 1);
        Assert.assertTrue(page.getTotal() > 0);
    }
    
}