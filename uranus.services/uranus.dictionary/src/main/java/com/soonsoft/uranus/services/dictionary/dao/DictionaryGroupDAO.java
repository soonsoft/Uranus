package com.soonsoft.uranus.services.dictionary.dao;

import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.core.model.data.Page;
import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.data.service.mybatis.MybatisBaseDAO;
import com.soonsoft.uranus.services.dictionary.po.DictionaryGroupPO;

public class DictionaryGroupDAO extends MybatisBaseDAO<DictionaryGroupPO> implements IMapperID {

    public DictionaryGroupDAO(IDatabaseAccess<?> databaseAccess) {
        super(databaseAccess);
    }

    public DictionaryGroupPO getByCode(String code) {
        return getDatabaseAccess().get(getStatement("getByCode"), code);
    }

    public int deleteByCode(String code) {
        return getDatabaseAccess().delete(getStatement("deleteGroupByCode"), code);
    }

    public List<DictionaryGroupPO> selectAll() {
        return getDatabaseAccess().select(getStatement("selectAllGroups"));
    }

    public List<DictionaryGroupPO> selectByDataStatus(short status) {
        return getDatabaseAccess().select(getStatement("selectByDataStatus"), Map.of("dataStatus", status));
    }

    public List<DictionaryGroupPO> select(Map<String, Object> params, Page page) {
        return getDatabaseAccess().select(getStatement("select"), params, page);
    }

}
