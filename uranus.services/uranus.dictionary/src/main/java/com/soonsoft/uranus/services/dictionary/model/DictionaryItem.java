package com.soonsoft.uranus.services.dictionary.model;

import java.util.Date;
import java.util.UUID;

public class DictionaryItem {

	private UUID id;
	private UUID dictGroupId;
	private String dictGroupCode;
	private String dictItemCode;
	private String dictItemName;
	private String dictItemValue;
	private UUID parentItemId;
	private String parentItemCode;
	private String remark;
	private short dataStatus = 1;
	private String createUser;
	private Date createTime;
	private String updateUser;
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

