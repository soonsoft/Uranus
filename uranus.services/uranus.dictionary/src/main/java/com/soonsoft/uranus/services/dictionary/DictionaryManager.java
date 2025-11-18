package com.soonsoft.uranus.services.dictionary;

import java.util.ArrayList;
import java.util.List;

import com.soonsoft.uranus.core.utils.caching.Cache;
import com.soonsoft.uranus.services.dictionary.model.DictionaryGroup;
import com.soonsoft.uranus.services.dictionary.model.DictionaryItem;
import com.soonsoft.uranus.services.dictionary.service.DictionaryService;

public class DictionaryManager {
    private final Cache<String, DictionaryItem> dictionaryCache;
    private final Cache<String, DictionaryGroupIndex> groupIndexCache = new Cache<>();  
    private final DictionaryService dictionaryService;

    public DictionaryManager(DictionaryService dictionaryService) {
        this.dictionaryCache = new Cache<>();
        this.dictionaryService = dictionaryService;

        initailize();
    }

    protected void initailize() {
        List<DictionaryGroup> groups = dictionaryService.getAllEnabledGroups();
        for (DictionaryGroup group : groups) {
            DictionaryGroupIndex groupIndex = new DictionaryGroupIndex(group);
            groupIndexCache.put(group.getDictGroupCode(), groupIndex);
        }

        List<DictionaryItem> items = dictionaryService.getAllEnabledItems();
        for (DictionaryItem item : items) {
            String key = generateCacheKey(item.getDictGroupCode(), item.getDictItemCode());
            dictionaryCache.put(key, item);

            DictionaryGroupIndex groupIndex = groupIndexCache.get(item.getDictGroupCode());
            if (groupIndex != null) {
                groupIndex.getItemCodes().add(item.getDictItemCode());
            }
        }
    }

    protected String generateCacheKey(String groupCode, String itemCode) {
        return groupCode + "::" + itemCode;
    }

    public DictionaryItem getDictionaryItem(String groupCode, String itemCode) {
        String key = generateCacheKey(groupCode, itemCode);
        return dictionaryCache.get(key);
    }

    public List<DictionaryItem> getDictionaryItemsByGroup(String groupCode) {
        DictionaryGroupIndex groupIndex = groupIndexCache.get(groupCode);
        if (groupIndex == null) {
            return java.util.Collections.emptyList();
        }

        List<DictionaryItem> items = new ArrayList<>();
        for (String itemCode : groupIndex.getItemCodes()) {
            String key = generateCacheKey(groupCode, itemCode);
            DictionaryItem item = dictionaryCache.get(key);
            if (item != null) {
                items.add(item);
            }
        }
        return items;
    }

    public static class DictionaryGroupIndex {
        private List<String> itemCodes = new ArrayList<>();
        private final DictionaryGroup group;

        public DictionaryGroupIndex(DictionaryGroup group) {
            this.group = group;
        }

        public DictionaryGroup getGroup() {
            return group;
        }

        public List<String> getItemCodes() {
            return itemCodes;
        }
    }
}
