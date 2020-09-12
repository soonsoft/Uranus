package com.soonsoft.uranus.services.membership;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.services.membership.config.DataSourceConfig;
import com.soonsoft.uranus.services.membership.config.MembershipServiceConfig;
import com.soonsoft.uranus.services.membership.dto.SysMenu;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * FunctionServiceTest
 */
@RunWith(SpringRunner.class)
@MybatisTest
@ContextConfiguration(classes = {DataSourceConfig.class, MembershipServiceConfig.class})
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
    
}