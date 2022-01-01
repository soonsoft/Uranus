package com.soonsoft.uranus.services.membership.po;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import com.soonsoft.uranus.services.membership.constant.FunctionStatusEnum;
import com.soonsoft.uranus.services.membership.constant.FunctionTypeEnum;

@Table(name = "sys_function")
public class SysFunction {
    
    @Id
    @Column(name = "function_id")
    private UUID functionId;

    @Column(name = "function_name")
    private String functionName;

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(name = "description")
    private String description;

    @Column(name = "url")
    private String url;

    /**
     * 功能类型 menu: 菜单, action: 操作
     */
    @Column(name = "type")
    private String type = FunctionTypeEnum.MENU.Value;

    /**
     * 状态 1: 有效, 0: 无效
     */
    @Column(name = "status")
    private Integer status = FunctionStatusEnum.ENABLED.Value;

    /**
     * 排序
     */
    @Column(name = "sort_value")
    private int sortValue = 0;

    public UUID getFunctionId() {
        return functionId;
    }

    public void setFunctionId(UUID functionId) {
        this.functionId = functionId;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public int getSortValue() {
        return sortValue;
    }

    public void setSortValue(int sortValue) {
        this.sortValue = sortValue;
    }

}
