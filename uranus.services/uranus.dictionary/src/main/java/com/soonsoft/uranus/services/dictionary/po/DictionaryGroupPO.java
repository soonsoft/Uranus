package com.soonsoft.uranus.services.dictionary.po;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "dictionary_group")
public class DictionaryGroupPO {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "dict_group_code")
    private String dictGroupCode;

    @Column(name = "dict_group_name")
    private String dictGroupName;

    @Column(name = "dict_group_type")
    private String dictGroupType;

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

    public String getDictGroupCode() {
        return dictGroupCode;
    }

    public void setDictGroupCode(String dictGroupCode) {
        this.dictGroupCode = dictGroupCode;
    }

    public String getDictGroupName() {
        return dictGroupName;
    }

    public void setDictGroupName(String dictGroupName) {
        this.dictGroupName = dictGroupName;
    }

    public String getDictGroupType() {
        return dictGroupType;
    }

    public void setDictGroupType(String dictGroupType) {
        this.dictGroupType = dictGroupType;
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
