package com.soonsoft.uranus.services.membership.jdbc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import com.soonsoft.uranus.core.model.data.Page;
import com.soonsoft.uranus.data.config.DataSourceFactory;
import com.soonsoft.uranus.data.config.factorybean.JdbcTemplateDatabaseAccessFactory;
import com.soonsoft.uranus.data.service.jdbc.JdbcSqlParameter;
import com.soonsoft.uranus.data.service.jdbc.JdbcTemplateDatabaseAccess;
import com.soonsoft.uranus.services.membership.config.MembershipDataSourceProperty;
import com.soonsoft.uranus.services.membership.constant.RoleStatusEnum;
import com.soonsoft.uranus.services.membership.po.AuthRole;

import org.junit.Assert;
import org.junit.Test;

public class JdbcTemplateDatabaseAccessTest {

    private JdbcTemplateDatabaseAccess databaseAccess;

    {
        MembershipDataSourceProperty dataSourceProperty = new MembershipDataSourceProperty();
        DataSource dataSource = DataSourceFactory.create(dataSourceProperty);

        JdbcTemplateDatabaseAccessFactory factory = new JdbcTemplateDatabaseAccessFactory(dataSource);
        try {
            databaseAccess = (JdbcTemplateDatabaseAccess) factory.getObject();
        } catch(Exception e) {
            throw new IllegalStateException("init JdbcTemplateDatabaseAccess error.");
        }
    }

    @Test
    public void test_insertWithMap() {
        String sql = "insert into auth_role(role_id, role_name, description, status) values(:roleId, :roleName, :description, :status)";

        Map<String, Object> params = new HashMap<>();
        params.put("roleId", UUID.randomUUID());
        params.put("roleName", "MapRole from JDBC");
        params.put("description", "用 JDBCTemplateDatabaseAcces 创建");
        params.put("status", RoleStatusEnum.ENABLED.Value);

        int effectRows = databaseAccess.insert(sql, params);
        Assert.assertTrue(effectRows == 1);
    }

    @Test
    public void test_insertWithArray() {
        String sql = "insert into auth_role(role_id, role_name, description, status) values(?, ?, ?, ?)";

        Object[] params = new Object[] {
            UUID.randomUUID(), "ArrayRole from JDBC", "用 JDBCTemplateDatabaseAcces 创建", RoleStatusEnum.ENABLED.Value
        };

        int effectRows = databaseAccess.insert(sql, params);
        Assert.assertTrue(effectRows == 1);
    }

    @Test
    public void test_selectPage() {
        String sql = "select role_id, role_name, description, status from auth_role order by role_name asc";

        JdbcSqlParameter<AuthRole> params = new JdbcSqlParameter<>();
        params.setRowMapper((rs, rowNum) -> {
            AuthRole role = new AuthRole();
            role.setRoleId((UUID) rs.getObject("role_id"));
            role.setRoleName(rs.getString("role_name"));
            role.setDescription(rs.getString("description"));
            role.setStatus(rs.getInt("status"));
            return role;
        });
        Page page = new Page(2, 2);

        List<AuthRole> roleList = databaseAccess.select(sql, params, page);
        Assert.assertTrue(!roleList.isEmpty());
        Assert.assertTrue(roleList.get(0) instanceof AuthRole);

        List<Map<String, Object>> mapList = databaseAccess.select(sql, null, page);
        Assert.assertTrue(!mapList.isEmpty());
        Assert.assertTrue(mapList.get(0) instanceof Map);

    }
    
}