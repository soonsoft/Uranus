package com.soonsoft.uranus.services.membership.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.soonsoft.uranus.data.entity.Page;
import com.soonsoft.uranus.security.entity.RoleInfo;
import com.soonsoft.uranus.data.service.mybatis.MybatisDatabaseAccess;
import com.soonsoft.uranus.services.membership.config.MembershipConfig;
import com.soonsoft.uranus.services.membership.model.MembershipRole;
import com.soonsoft.uranus.services.membership.po.AuthRole;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.session.Configuration;
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

    @Test
    public void test_dynamicSelectMapper() {
        MybatisDatabaseAccess dba = (MybatisDatabaseAccess) roleService.getRolesInFunctionsDAO().getMembershipAccess();
        String mappedStatementId = "runtimeSelect";
        String namespace = "membership.auth_role";
        String statementName = namespace + "." + mappedStatementId;
        Map<String, Object> parameter = new HashMap<>();

        {
            // 创建动态的查询语句
            Configuration config = dba.getTemplate().getConfiguration();

            if(!config.hasStatement(statementName)) {
                SqlSourceBuilder sqlBuilder = new SqlSourceBuilder(config);
                SqlSource sqlSource = sqlBuilder.parse(
                    "SELECT role_id, role_name, description, status FROM auth_role", 
                    parameter.getClass(), null);

                MapperBuilderAssistant assistant = new MapperBuilderAssistant(config, null);
                assistant.setCurrentNamespace(namespace);
                assistant.addMappedStatement(
                    mappedStatementId, 
                    sqlSource, 
                    StatementType.PREPARED, 
                    SqlCommandType.SELECT, 
                    null, 
                    null, 
                    null, 
                    parameter.getClass(), 
                    null, 
                    Map.class, 
                    null, 
                    false, 
                    true, 
                    false, 
                    null, 
                    null, 
                    null, 
                    config.getDatabaseId(), 
                    config.getDefaultScriptingLanguageInstance());
            }
        }

        // 执行查询
        List<Object> result = dba.select(statementName);
        Assert.assertNotNull(result);
    }
    
}
