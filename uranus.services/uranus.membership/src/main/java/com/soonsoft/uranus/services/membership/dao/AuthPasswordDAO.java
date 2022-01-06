package com.soonsoft.uranus.services.membership.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.data.service.mybatis.MybatisBaseDAO;
import com.soonsoft.uranus.services.membership.constant.PasswordStatusEnum;
import com.soonsoft.uranus.services.membership.po.AuthPassword;

public class AuthPasswordDAO extends MybatisBaseDAO<AuthPassword> implements IMapperID {

    public AuthPasswordDAO(IDatabaseAccess<?> databaseAccess) {
        super(databaseAccess);
    }

    public AuthPassword getEnabledPassword(UUID userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("status", PasswordStatusEnum.ENABLED.Value);
        return getDatabaseAccess().get(getStatement("getPasswordWithStatus"), params);
    }

}