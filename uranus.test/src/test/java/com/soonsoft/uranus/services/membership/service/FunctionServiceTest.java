package com.soonsoft.uranus.services.membership.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.soonsoft.uranus.security.entity.FunctionInfo;
import com.soonsoft.uranus.services.membership.config.MembershipConfig;
import com.soonsoft.uranus.services.membership.po.AuthRole;
import com.soonsoft.uranus.services.membership.po.SysMenu;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { MembershipConfig.class })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FunctionServiceTest {

    @Autowired
    private FunctionService functionService;

    /**
     * ps: 执行之前请先创建数据
     */
    @Test
    public void test_getAllMenus() {
        Map<String, Object> params = new HashMap<>();
        params.put("status", 1);
        List<SysMenu> menus = functionService.getAllMenus(params);
        Assert.assertTrue(menus != null && !menus.isEmpty());
        menus.forEach(i -> Assert.assertTrue(i.getRoles() != null && !i.getRoles().isEmpty()));
    }

    @Test
    public void test_createApiFunction() {
        SysMenu apiFunc = new SysMenu();
        apiFunc.setUrl("/account/menus");
        apiFunc.setType(FunctionInfo.ACTION_TYPE);
        apiFunc.setFunctionName("getUserMenus");
        apiFunc.setDescription("查询用户菜单");
        apiFunc.setStatus(SysMenu.STATUS_ENABLED);

        AuthRole role = new AuthRole();
        role.setRoleId(UUID.fromString("767defd2-8b87-11e9-99cb-00163e1c3c68"));
        role.setRoleName("系统管理员");
        apiFunc.addRole(role);

        boolean result = functionService.createMenu(apiFunc);
        Assert.assertTrue(result);
    }
    
}