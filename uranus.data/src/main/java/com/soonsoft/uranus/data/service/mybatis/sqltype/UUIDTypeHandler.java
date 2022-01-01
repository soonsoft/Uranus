package com.soonsoft.uranus.data.service.mybatis.sqltype;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.soonsoft.uranus.core.common.lang.StringUtils;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

public class UUIDTypeHandler extends BaseTypeHandler<UUID> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UUID parameter, JdbcType jdbcType) throws SQLException {
        if(parameter != null) {
            ps.setObject(i, parameter);
        }
    }

    @Override
    public UUID getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return getValue(rs.getObject(columnName));
    }

    @Override
    public UUID getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return getValue(rs.getObject(columnIndex));
    }

    @Override
    public UUID getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return getValue(cs.getObject(columnIndex));
    }

    private UUID getValue(Object value) {
        if(value == null) {
            return null;
        }
        if(value instanceof UUID) {
            return (UUID) value;
        }
        if(value instanceof String) {
            String str = (String) value;
            if(!StringUtils.isBlank(str)) {
                return UUID.fromString(str);
            }
        }
        return null;
    }
    
}
