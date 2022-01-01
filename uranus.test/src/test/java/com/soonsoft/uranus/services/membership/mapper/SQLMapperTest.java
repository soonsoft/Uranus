package com.soonsoft.uranus.services.membership.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.data.service.meta.TableInfo;
import com.soonsoft.uranus.data.service.meta.loader.ITableInfoLoader;
import com.soonsoft.uranus.data.service.meta.loader.jpa.JAPTableInfoLoader;
import com.soonsoft.uranus.data.service.mybatis.MybatisDatabaseAccess;
import com.soonsoft.uranus.data.service.mybatis.mapper.sql.Delete;
import com.soonsoft.uranus.data.service.mybatis.mapper.sql.GetByPrimary;
import com.soonsoft.uranus.data.service.mybatis.mapper.sql.Insert;
import com.soonsoft.uranus.data.service.mybatis.mapper.sql.InsertSelective;
import com.soonsoft.uranus.data.service.mybatis.mapper.sql.Update;
import com.soonsoft.uranus.data.service.mybatis.mapper.sql.UpdateSelective;
import com.soonsoft.uranus.services.membership.config.MembershipConfig;
import com.soonsoft.uranus.services.membership.po.AuthRole;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.session.Configuration;
import org.junit.Assert;
import org.junit.Before;
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
public class SQLMapperTest {

    private final static String PREFIX_OF_NAMESPACE = "uranus.test.";

    private TableInfo authRoleTable;

    @Autowired
    @Qualifier("membershipAccess")
    private IDatabaseAccess<?> databaseAccess;

    private AuthRole data;

    private final static String ROLE_ID = "e1e469be-3507-4cca-a803-a3b29d4b95c6";

    @Before
    public void init() {
        {
            ITableInfoLoader loader = new JAPTableInfoLoader();
            authRoleTable = loader.load(AuthRole.class);
        }

        {
            data = new AuthRole();
            data.setRoleId(UUID.fromString(ROLE_ID));
            data.setRoleName("Center of Universe");
            data.setDescription("宇宙中心");
            data.setStatus(1);
        }
    }

    @Test
    public void test_insertMapper() {
        Insert insert = new Insert();

        String namespace = PREFIX_OF_NAMESPACE + authRoleTable.getTableName();
        String id = insert.getMapperName();
        
        MybatisDatabaseAccess dba = (MybatisDatabaseAccess) this.databaseAccess;
        Configuration config = dba.getTemplate().getConfiguration();
        MapperBuilderAssistant builderAssistant = new MapperBuilderAssistant(config, "TEST");
        builderAssistant.setCurrentNamespace(namespace);

        insert.add(builderAssistant, AuthRole.class, authRoleTable);

        int effectRows = dba.insert(namespace + "." + id, data);
        Assert.assertTrue(effectRows > 0);
    }

    @Test
    public void test_updateMapper() {
        Update update = new Update();

        String namespace = PREFIX_OF_NAMESPACE + authRoleTable.getTableName();
        String id = update.getMapperName();
        
        MybatisDatabaseAccess dba = (MybatisDatabaseAccess) this.databaseAccess;
        Configuration config = dba.getTemplate().getConfiguration();
        MapperBuilderAssistant builderAssistant = new MapperBuilderAssistant(config, "TEST");
        builderAssistant.setCurrentNamespace(namespace);

        update.add(builderAssistant, AuthRole.class, authRoleTable);

        data.setRoleName("COU");
        data.setDescription("整个宇宙的中心哦");
        data.setStatus(0);
        int effectRows = dba.update(namespace + "." + id, data);
        Assert.assertTrue(effectRows > 0);
    }

    @Test
    public void test_getByPrimaryMapper() {
        GetByPrimary get = new GetByPrimary();

        String namespace = PREFIX_OF_NAMESPACE + authRoleTable.getTableName();
        String id = get.getMapperName();
        
        MybatisDatabaseAccess dba = (MybatisDatabaseAccess) this.databaseAccess;
        Configuration config = dba.getTemplate().getConfiguration();
        MapperBuilderAssistant builderAssistant = new MapperBuilderAssistant(config, "TEST");
        builderAssistant.setCurrentNamespace(namespace);

        get.add(builderAssistant, AuthRole.class, authRoleTable);

        Map<String, Object> param = new HashMap<>();
        param.put("p0", data.getRoleId());
        List<AuthRole> authRoles = dba.select(namespace + "." + id, param);
        
        Assert.assertTrue(authRoles.size() == 1);
        Assert.assertTrue(authRoles.get(0).getRoleId().toString().equals(ROLE_ID));
    }

    @Test
    public void test_deleteMapper() {
        Delete delete = new Delete();

        String namespace = PREFIX_OF_NAMESPACE + authRoleTable.getTableName();
        String id = delete.getMapperName();
        
        MybatisDatabaseAccess dba = (MybatisDatabaseAccess) this.databaseAccess;
        Configuration config = dba.getTemplate().getConfiguration();
        MapperBuilderAssistant builderAssistant = new MapperBuilderAssistant(config, "TEST");
        builderAssistant.setCurrentNamespace(namespace);

        delete.add(builderAssistant, AuthRole.class, authRoleTable);

        Map<String, Object> param = new HashMap<>();
        param.put("p0", data.getRoleId());
        int effectRows = dba.delete(namespace + "." + id, param);
        Assert.assertTrue(effectRows > 0);
    }

    @Test
    public void test_insertSelectiveMapper() {
        InsertSelective insertSelective = new InsertSelective();

        String namespace = PREFIX_OF_NAMESPACE + authRoleTable.getTableName();
        String id = insertSelective.getMapperName();
        
        MybatisDatabaseAccess dba = (MybatisDatabaseAccess) this.databaseAccess;
        Configuration config = dba.getTemplate().getConfiguration();
        MapperBuilderAssistant builderAssistant = new MapperBuilderAssistant(config, "TEST");
        builderAssistant.setCurrentNamespace(namespace);

        insertSelective.add(builderAssistant, AuthRole.class, authRoleTable);

        AuthRole role = new AuthRole();
        role.setRoleId(UUID.randomUUID());
        role.setRoleName("AAA");
        int effectRows = dba.insert(namespace + "." + id, role);
        Assert.assertTrue(effectRows > 0);
    }

    @Test
    public void test_updateSelectiveMapper() {
        UpdateSelective updateSelective = new UpdateSelective();

        String namespace = PREFIX_OF_NAMESPACE + authRoleTable.getTableName();
        String id = updateSelective.getMapperName();
        
        MybatisDatabaseAccess dba = (MybatisDatabaseAccess) this.databaseAccess;
        Configuration config = dba.getTemplate().getConfiguration();
        MapperBuilderAssistant builderAssistant = new MapperBuilderAssistant(config, "TEST");
        builderAssistant.setCurrentNamespace(namespace);

        updateSelective.add(builderAssistant, AuthRole.class, authRoleTable);

        AuthRole role = new AuthRole();
        role.setRoleId(data.getRoleId());
        role.setStatus(0);
        int effectRows = dba.insert(namespace + "." + id, role);
        Assert.assertTrue(effectRows > 0);
    }
}
