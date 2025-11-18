package com.soonsoft.uranus.services.dictionary.service;

import java.util.List;

import com.soonsoft.uranus.services.dictionary.dao.DictionaryGroupDAO;
import com.soonsoft.uranus.services.dictionary.dao.DictionaryItemDAO;
import com.soonsoft.uranus.services.dictionary.model.DictionaryGroup;
import com.soonsoft.uranus.services.dictionary.model.DictionaryItem;
import com.soonsoft.uranus.services.dictionary.model.DictionaryConverter;
import com.soonsoft.uranus.services.dictionary.constant.DictionaryDataStatusEnum;

public class DictionaryService {
    
    private final DictionaryGroupDAO groupDAO;
    private final DictionaryItemDAO itemDAO;

    public DictionaryService(DictionaryGroupDAO groupDAO, DictionaryItemDAO itemDAO) {
        this.groupDAO = groupDAO;
        this.itemDAO = itemDAO;
    }

    public List<DictionaryGroup> getAllEnabledGroups() {
        List<com.soonsoft.uranus.services.dictionary.po.DictionaryGroupPO> list = groupDAO.selectByDataStatus((short) DictionaryDataStatusEnum.Enabled.Value);
        if (list == null) return java.util.Collections.emptyList();
        return list.stream().map(DictionaryConverter::fromPO).collect(java.util.stream.Collectors.toList());
    }

    public List<DictionaryItem> getAllEnabledItems() {
        // fetch items by status across groups (mapper doesn't provide selectByDataStatus for items),
        // use generic select with params {dataStatus = Enabled}
        java.util.Map<String, Object> params = java.util.Map.of("dataStatus", (short) DictionaryDataStatusEnum.Enabled.Value);
        List<com.soonsoft.uranus.services.dictionary.po.DictionaryItemPO> list = itemDAO.select(params, null);
        if (list == null) return java.util.Collections.emptyList();
        return list.stream().map(DictionaryConverter::fromPO).collect(java.util.stream.Collectors.toList());
    }

}
