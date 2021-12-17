package com.soonsoft.uranus.services.membership.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.soonsoft.uranus.data.entity.Page;
import com.soonsoft.uranus.security.entity.RoleInfo;
import com.soonsoft.uranus.services.membership.config.MembershipConfig;
import com.soonsoft.uranus.services.membership.model.MembershipRole;
import com.soonsoft.uranus.services.membership.po.AuthRole;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {MembershipConfig.class})
public class RoleServiceTest {

    @Qualifier("membershipRoleService")
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

    @Test
    public void test_createRole() {
        RoleInfo role = new MembershipRole(UUID.randomUUID().toString(), "CTO");
        role.setDescription("首席技术官");
        role.setEnable(true);

        boolean result = roleService.createRole(role);
        Assert.assertTrue(result);
    }
    
}