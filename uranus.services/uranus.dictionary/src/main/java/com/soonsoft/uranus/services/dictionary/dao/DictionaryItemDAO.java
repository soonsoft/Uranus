package com.soonsoft.uranus.services.dictionary.dao;

import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.core.model.data.Page;
import com.soonsoft.uranus.data.IDatabaseAccess;
import com.soonsoft.uranus.data.service.mybatis.MybatisBaseDAO;
import com.soonsoft.uranus.services.dictionary.po.DictionaryItemPO;

public class DictionaryItemDAO extends MybatisBaseDAO<DictionaryItemPO> implements IMapperID {

    public DictionaryItemDAO(IDatabaseAccess<?> databaseAccess) {
        super(databaseAccess);
    }

    public int deleteByCode(Map<String, Object> params) {
        return getDatabaseAccess().delete(getStatement("deleteItemByCode"), params);
    }

    public int deleteByGroupId(Object groupId) {
        return getDatabaseAccess().delete(getStatement("deleteItemByGroupId"), groupId);
    }

    public List<DictionaryItemPO> selectByGroupCode(String groupCode) {
        return getDatabaseAccess().select(getStatement("selectByGroupCode"), Map.of("dictGroupCode", groupCode));
    }

    public List<DictionaryItemPO> select(Map<String, Object> params, Page page) {
        return getDatabaseAccess().select(getStatement("select"), params, page);
    }

    public int updateItemsStatusByGroupId(Object groupId, short dataStatus, String updateUser) {
        Map<String, Object> params = Map.of(
            "groupId", groupId,
            "dataStatus", dataStatus,
            "updateUser", updateUser,
            "updateTime", new java.util.Date()
        );
        return getDatabaseAccess().update(getStatement("updateDictionaryItemsStatusByGroupId"), params);
    }

}
