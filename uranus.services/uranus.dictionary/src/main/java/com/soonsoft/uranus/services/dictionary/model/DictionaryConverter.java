package com.soonsoft.uranus.services.dictionary.model;

import com.soonsoft.uranus.services.dictionary.po.DictionaryGroupPO;
import com.soonsoft.uranus.services.dictionary.po.DictionaryItemPO;

/**
 * Converter utilities between model and PO for dictionary module.
 */
public final class DictionaryConverter {

    private DictionaryConverter() { }

    public static DictionaryGroupPO toPO(DictionaryGroup g) {
        if (g == null) return null;
        DictionaryGroupPO p = new DictionaryGroupPO();
        p.setId(g.getId());
        p.setDictGroupCode(g.getDictGroupCode());
        p.setDictGroupName(g.getDictGroupName());
        p.setDictGroupType(g.getDictGroupType());
        p.setRemark(g.getRemark());
        p.setDataStatus(g.getDataStatus());
        if (g.getOperateInfo() != null) {
            p.setCreateUser(g.getOperateInfo().getCreateUser());
            p.setCreateTime(g.getOperateInfo().getCreateTime());
            p.setUpdateUser(g.getOperateInfo().getUpdateUser());
            p.setUpdateTime(g.getOperateInfo().getUpdateTime());
        }
        return p;
    }

    public static DictionaryGroup fromPO(DictionaryGroupPO p) {
        if (p == null) return null;
        DictionaryGroup g = new DictionaryGroup();
        g.setId(p.getId());
        g.setDictGroupCode(p.getDictGroupCode());
        g.setDictGroupName(p.getDictGroupName());
        g.setDictGroupType(p.getDictGroupType());
        g.setRemark(p.getRemark());
        g.setDataStatus(p.getDataStatus());
        OperateInfo oi = new OperateInfo();
        oi.setCreateUser(p.getCreateUser());
        oi.setCreateTime(p.getCreateTime());
        oi.setUpdateUser(p.getUpdateUser());
        oi.setUpdateTime(p.getUpdateTime());
        g.setOperateInfo(oi);
        return g;
    }

    public static DictionaryItemPO toPO(DictionaryItem g) {
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
        if (g.getOperateInfo() != null) {
            p.setCreateUser(g.getOperateInfo().getCreateUser());
            p.setCreateTime(g.getOperateInfo().getCreateTime());
            p.setUpdateUser(g.getOperateInfo().getUpdateUser());
            p.setUpdateTime(g.getOperateInfo().getUpdateTime());
        }
        return p;
    }

    public static DictionaryItem fromPO(DictionaryItemPO p) {
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
        OperateInfo oi = new OperateInfo();
        oi.setCreateUser(p.getCreateUser());
        oi.setCreateTime(p.getCreateTime());
        oi.setUpdateUser(p.getUpdateUser());
        oi.setUpdateTime(p.getUpdateTime());
        g.setOperateInfo(oi);
        return g;
    }

    // helper to ensure operateInfo exists on model instances
    public static OperateInfo ensureOperateInfo(DictionaryGroup g) {
        if (g.getOperateInfo() == null) {
            OperateInfo oi = new OperateInfo();
            g.setOperateInfo(oi);
            return oi;
        }
        return g.getOperateInfo();
    }

    public static OperateInfo ensureOperateInfo(DictionaryItem g) {
        if (g.getOperateInfo() == null) {
            OperateInfo oi = new OperateInfo();
            g.setOperateInfo(oi);
            return oi;
        }
        return g.getOperateInfo();
    }
}
