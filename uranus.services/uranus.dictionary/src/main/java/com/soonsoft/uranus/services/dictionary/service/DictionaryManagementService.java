package com.soonsoft.uranus.services.dictionary.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.services.dictionary.dao.DictionaryGroupDAO;
import com.soonsoft.uranus.services.dictionary.dao.DictionaryItemDAO;
import com.soonsoft.uranus.services.dictionary.model.DictionaryGroup;
import com.soonsoft.uranus.services.dictionary.model.DictionaryItem;
import com.soonsoft.uranus.services.dictionary.po.DictionaryGroupPO;
import com.soonsoft.uranus.services.dictionary.po.DictionaryItemPO;
import com.soonsoft.uranus.services.dictionary.constant.DictionaryDataStatusEnum;

/**
 * Service implementation that uses DAOs to operate on dictionary data.
 */
public class DictionaryManagementService {

    private final DictionaryGroupDAO groupDAO;
    private final DictionaryItemDAO itemDAO;

    public DictionaryManagementService(DictionaryGroupDAO groupDAO, DictionaryItemDAO itemDAO) {
        this.groupDAO = groupDAO;
        this.itemDAO = itemDAO;
    }

    // === conversions between model and PO ===
    private DictionaryGroupPO toPO(DictionaryGroup g) {
        if (g == null) return null;
        DictionaryGroupPO p = new DictionaryGroupPO();
        p.setId(g.getId());
        p.setDictGroupCode(g.getDictGroupCode());
        p.setDictGroupName(g.getDictGroupName());
        p.setDictGroupType(g.getDictGroupType());
        p.setRemark(g.getRemark());
        p.setDataStatus(g.getDataStatus());
        p.setCreateUser(g.getCreateUser());
        p.setCreateTime(g.getCreateTime());
        p.setUpdateUser(g.getUpdateUser());
        p.setUpdateTime(g.getUpdateTime());
        return p;
    }

    private DictionaryGroup fromPO(DictionaryGroupPO p) {
        if (p == null) return null;
        DictionaryGroup g = new DictionaryGroup();
        g.setId(p.getId());
        g.setDictGroupCode(p.getDictGroupCode());
        g.setDictGroupName(p.getDictGroupName());
        g.setDictGroupType(p.getDictGroupType());
        g.setRemark(p.getRemark());
        g.setDataStatus(p.getDataStatus());
        g.setCreateUser(p.getCreateUser());
        g.setCreateTime(p.getCreateTime());
        g.setUpdateUser(p.getUpdateUser());
        g.setUpdateTime(p.getUpdateTime());
        return g;
    }

    private DictionaryItemPO toPO(DictionaryItem g) {
        if (g == null) return null;
        DictionaryItemPO p = new DictionaryItemPO();
        p.setId(g.getId());
        p.setDictGroupId(g.getDictGroupId());
        p.setDictGroupCode(g.getDictGroupCode());
        p.setDictItemCode(g.getDictItemCode());
        p.setDictItemName(g.getDictItemName());
        p.setDictItemValue(g.getDictItemValue());
        p.setParentItemId(g.getParentItemId());
        p.setParentItemCode(g.getParentItemCode());
        p.setRemark(g.getRemark());
        p.setDataStatus(g.getDataStatus());
        p.setCreateUser(g.getCreateUser());
        p.setCreateTime(g.getCreateTime());
        p.setUpdateUser(g.getUpdateUser());
        p.setUpdateTime(g.getUpdateTime());
        return p;
    }

    private DictionaryItem fromPO(DictionaryItemPO p) {
        if (p == null) return null;
        DictionaryItem g = new DictionaryItem();
        g.setId(p.getId());
        g.setDictGroupId(p.getDictGroupId());
        g.setDictGroupCode(p.getDictGroupCode());
        g.setDictItemCode(p.getDictItemCode());
        g.setDictItemName(p.getDictItemName());
        g.setDictItemValue(p.getDictItemValue());
        g.setParentItemId(p.getParentItemId());
        g.setParentItemCode(p.getParentItemCode());
        g.setRemark(p.getRemark());
        g.setDataStatus(p.getDataStatus());
        g.setCreateUser(p.getCreateUser());
        g.setCreateTime(p.getCreateTime());
        g.setUpdateUser(p.getUpdateUser());
        g.setUpdateTime(p.getUpdateTime());
        return g;
    }

    // === group operations ===

    @Transactional
    public void addGroup(DictionaryGroup group) {
        Guard.notNull(group, "the group is required.");
        if (group.getId() == null) group.setId(UUID.randomUUID());
        Date now = new Date();
        if (group.getCreateTime() == null) group.setCreateTime(now);
        group.setUpdateTime(now);
        groupDAO.insert(toPO(group));
    }

    @Transactional
    public void removeGroup(UUID groupId) {
        Guard.notNull(groupId, "the groupId is required.");
        // delete items first
        groupDAO.delete(groupId);
        itemDAO.deleteByGroupId(groupId);
    }

    @Transactional
    public void removeGroup(String groupCode) {
        Guard.notNull(groupCode, "the groupCode is required.");
        // delete items by group code then group
        itemDAO.deleteByCode(Map.of("dictGroupCode", groupCode));
        groupDAO.deleteByCode(groupCode);
    }

    @Transactional
    public void updateGroup(DictionaryGroup group) {
        Guard.notNull(group, "the group is required.");
        Guard.notNull(group.getId(), "the group.id is required.");
        group.setUpdateTime(new Date());
        groupDAO.updateSelective(toPO(group));
    }

    @Transactional
    public void disableGroup(UUID groupId) {
        Guard.notNull(groupId, "the groupId is required.");
        DictionaryGroupPO p = groupDAO.getByPrimary(groupId);
        if (p != null) {
            short disabledValue = (short) DictionaryDataStatusEnum.Disabled.Value;
            p.setDataStatus(disabledValue);
            p.setUpdateTime(new Date());
            groupDAO.updateSelective(p);
            itemDAO.updateItemsStatusByGroupId(groupId, disabledValue, null);
        }
    }

    @Transactional
    public void enableGroup(UUID groupId) {
        Guard.notNull(groupId, "the groupId is required.");
        DictionaryGroupPO p = groupDAO.getByPrimary(groupId);
        if (p != null) {
            short enabledValue = (short) DictionaryDataStatusEnum.Enabled.Value;
            p.setDataStatus(enabledValue);
            p.setUpdateTime(new Date());
            groupDAO.updateSelective(p);
            itemDAO.updateItemsStatusByGroupId(groupId, enabledValue, null);
        }
    }

    public List<DictionaryGroup> getAllGroups() {
        List<DictionaryGroupPO> list = groupDAO.selectAll();
        if (list == null) return new ArrayList<>();
        return list.stream().map(this::fromPO).collect(Collectors.toList());
    }

    public List<DictionaryGroup> getEnabledGroups() {
        List<DictionaryGroupPO> list = groupDAO.selectByDataStatus((short)1);
        if (list == null) return new ArrayList<>();
        return list.stream().map(this::fromPO).collect(Collectors.toList());
    }

    // === item operations ===

    @Transactional
    public void addItemToGroup(UUID groupId, String itemCode, String itemValue) {
        Guard.notNull(groupId, "the groupId is required.");
        Guard.notNull(itemCode, "the itemCode is required.");
        DictionaryItem item = new DictionaryItem();
        item.setId(UUID.randomUUID());
        item.setDictGroupId(groupId);
        item.setDictItemCode(itemCode);
        item.setDictItemValue(itemValue);
        Date now = new Date();
        item.setCreateTime(now);
        item.setUpdateTime(now);
        itemDAO.insert(toPO(item));
    }

    @Transactional
    public void addItem(DictionaryItem item) {
        Guard.notNull(item, "the item is required.");
        if (item.getId() == null) item.setId(UUID.randomUUID());
        Date now = new Date();
        if (item.getCreateTime() == null) item.setCreateTime(now);
        item.setUpdateTime(now);
        itemDAO.insert(toPO(item));
    }

    @Transactional
    public void removeItem(UUID itemId) {
        Guard.notNull(itemId, "the itemId is required.");
        itemDAO.delete(itemId);
    }

    @Transactional
    public void removeItem(String itemCode) {
        Guard.notNull(itemCode, "the itemCode is required.");
        itemDAO.deleteByCode(Map.of("dictItemCode", itemCode));
    }

    @Transactional
    public void updateItem(DictionaryItem item) {
        Guard.notNull(item, "the item is required.");
        Guard.notNull(item.getId(), "the item.id is required.");
        item.setUpdateTime(new Date());
        itemDAO.updateSelective(toPO(item));
    }

    @Transactional
    public void disableItem(UUID itemId) {
        Guard.notNull(itemId, "the itemId is required.");
        DictionaryItemPO p = itemDAO.getByPrimary(itemId);
        if (p != null) {
            p.setDataStatus((short)4);
            p.setUpdateTime(new Date());
            itemDAO.updateSelective(p);
        }
    }

    @Transactional
    public void enableItem(UUID itemId) {
        Guard.notNull(itemId, "the itemId is required.");
        DictionaryItemPO p = itemDAO.getByPrimary(itemId);
        if (p != null) {
            p.setDataStatus((short)1);
            p.setUpdateTime(new Date());
            itemDAO.updateSelective(p);
        }
    }

    public List<DictionaryItem> getItemsByGroupCode(String groupCode) {
        Guard.notNull(groupCode, "the groupCode is required.");
        List<DictionaryItemPO> list = itemDAO.selectByGroupCode(groupCode);
        if (list == null) return new ArrayList<>();
        return list.stream().map(this::fromPO).collect(Collectors.toList());
    }

}
