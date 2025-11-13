package com.soonsoft.uranus.services.dictionary.po;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "dictionary_item")
public class DictionaryItemPO {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "dict_group_id")
    private UUID dictGroupId;

    @Column(name = "dict_group_code")
    private String dictGroupCode;

    @Column(name = "dict_item_code")
    private String dictItemCode;

    @Column(name = "dict_item_name")
    private String dictItemName;

    @Column(name = "dict_item_value")
    private String dictItemValue;

    @Column(name = "parent_item_id")
    private UUID parentItemId;

    @Column(name = "parent_item_code")
    private String parentItemCode;

    @Column(name = "remark")
    private String remark;

    @Column(name = "data_status")
    private short dataStatus = 1;

    @Column(name = "create_user")
    private String createUser;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_user")
    private String updateUser;

    @Column(name = "update_time")
    private Date updateTime;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getDictGroupId() {
        return dictGroupId;
    }

    public void setDictGroupId(UUID dictGroupId) {
        this.dictGroupId = dictGroupId;
    }

    public String getDictGroupCode() {
        return dictGroupCode;
    }

    public void setDictGroupCode(String dictGroupCode) {
        this.dictGroupCode = dictGroupCode;
    }

    public String getDictItemCode() {
        return dictItemCode;
    }

    public void setDictItemCode(String dictItemCode) {
        this.dictItemCode = dictItemCode;
    }

    public String getDictItemName() {
        return dictItemName;
    }

    public void setDictItemName(String dictItemName) {
        this.dictItemName = dictItemName;
    }

    public String getDictItemValue() {
        return dictItemValue;
    }

    public void setDictItemValue(String dictItemValue) {
        this.dictItemValue = dictItemValue;
    }

    public UUID getParentItemId() {
        return parentItemId;
    }

    public void setParentItemId(UUID parentItemId) {
        this.parentItemId = parentItemId;
    }

    public String getParentItemCode() {
        return parentItemCode;
    }

    public void setParentItemCode(String parentItemCode) {
        this.parentItemCode = parentItemCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public short getDataStatus() {
        return dataStatus;
    }

    public void setDataStatus(short dataStatus) {
        this.dataStatus = dataStatus;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}
