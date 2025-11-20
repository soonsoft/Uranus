package com.soonsoft.uranus.services.dictionary;

import java.util.ArrayList;
import java.util.List;

import com.soonsoft.uranus.core.utils.caching.Cache;
import com.soonsoft.uranus.services.dictionary.model.DictionaryGroup;
import com.soonsoft.uranus.services.dictionary.model.DictionaryItem;
import com.soonsoft.uranus.services.dictionary.service.DictionaryManagementService;
import com.soonsoft.uranus.services.dictionary.service.DictionaryService;
import com.soonsoft.uranus.services.dictionary.exceptions.DictionaryNotExistsException;

public class DictionaryManager {
    // Indicates whether the manager has been initialized.
    private final boolean isInitailized;
    private final Cache<String, DictionaryItem> dictionaryCache = new Cache<>();
    private final Cache<String, DictionaryGroupIndex> groupIndexCache = new Cache<>();  
    
    private final DictionaryService dictionaryService;
    private final DictionaryManagementService dictionaryManagementService;

    public DictionaryManager(DictionaryService dictionaryService, 
                             DictionaryManagementService dictionaryManagementService) {
        this.dictionaryService = dictionaryService;
        this.dictionaryManagementService = dictionaryManagementService;

        initailize();
        this.isInitailized = true;
    }

    protected void initailize() {
        if (isInitailized) {
            throw new IllegalStateException("DictionaryManager is already initialized.");
        }
        List<DictionaryGroup> groups = dictionaryService.getAllEnabledGroups();
        if(groups == null || groups.isEmpty()) {
            return;
        }
        groupIndexCache.putAll(putFn -> {
            for (DictionaryGroup group : groups) {
                DictionaryGroupIndex groupIndex = new DictionaryGroupIndex(group);
                putFn.apply(group.getDictGroupCode(), groupIndex);
            }
        });

        List<DictionaryItem> items = dictionaryService.getAllEnabledItems();
        if(items != null && !items.isEmpty()) {
            dictionaryCache.putAll(putFn -> {
                for (DictionaryItem item : items) {
                    String key = generateCacheKey(item.getDictGroupCode(), item.getDictItemCode());
                    putFn.apply(key, item);

                    DictionaryGroupIndex groupIndex = groupIndexCache.get(item.getDictGroupCode());
                    if (groupIndex != null) {
                        groupIndex.getItemCodes().add(item.getDictItemCode());
                    }
                }
            });
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

    public void checkDictionaryItemExists(String groupCode, String itemCode) {
        String key = generateCacheKey(groupCode, itemCode);
        if (!dictionaryCache.containsKey(key)) {
            throw new DictionaryNotExistsException(groupCode, itemCode);
        }
    }

    public void setDictionaryItem(DictionaryItem item) {
        if(item == null || item.getDictGroupCode() == null || item.getDictItemCode() == null) {
            throw new IllegalArgumentException("Dictionary item, group code, and item code must not be null.");
        }
        String key = generateCacheKey(item.getDictGroupCode(), item.getDictItemCode());
        if(dictionaryCache.containsKey(key)) {
            dictionaryManagementService.updateItem(item);
            dictionaryCache.put(key, item);
        } else {
            dictionaryManagementService.addItem(item);
            dictionaryCache.put(key, item);
            DictionaryGroupIndex groupIndex = groupIndexCache.get(item.getDictGroupCode());
            if (groupIndex != null) {
                groupIndex.getItemCodes().add(item.getDictItemCode());
            }
        }
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
