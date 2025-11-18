package com.soonsoft.uranus.services.dictionary.model;

import java.util.UUID;

public class DictionaryGroup {

	private UUID id;
	private String dictGroupCode;
	private String dictGroupName;
	private String dictGroupType;
	private String remark;
	private short dataStatus = 1;
	private OperateInfo operateInfo;

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
	public OperateInfo getOperateInfo() {
		return operateInfo;
	}

	public void setOperateInfo(OperateInfo operateInfo) {
		this.operateInfo = operateInfo;
	}

	// no delegate accessors; use getOperateInfo()/setOperateInfo() explicitly

}
