package com.soonsoft.uranus.security.entity;

import java.util.ArrayList;
import java.util.List;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.security.authorization.IResource;
import com.soonsoft.uranus.security.entity.StatusConst.ResourceStatus;
import com.soonsoft.uranus.security.entity.StatusConst.ResourceType;


public class FunctionInfo implements IResource<String> {

    private String resourceCode;

    private String parentResourceCode;

    private String name;

    private String url;

    private String functionStatus;

    private String type = ResourceType.ACTION;

    private String description;

    private List<String> allowUsers;

    private List<String> allowRoles;

    public FunctionInfo(String resourceCode, String name) {
        this(resourceCode, name, null);
    }

    public FunctionInfo(String resourceCode, String name, String url) {
        if(StringUtils.isEmpty(resourceCode)) {
            throw new IllegalArgumentException("the resourceCode is null or empty.");
        }
        this.resourceCode = resourceCode;
        this.name = name;
        this.url = url;
    }

    @Override
    public String getResourceCode() {
        return resourceCode;
    }

    public void setResourceCode(String resourceCode) {
        this.resourceCode = resourceCode;
    }

    @Override
    public String getResourceUrl() {
        return getUrl();
    }

    public String getParentResourceCode() {
        return parentResourceCode;
    }

    public void setParentResourceCode(String parentResourceCode) {
        this.parentResourceCode = parentResourceCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isEnabled() {
        return ResourceStatus.ENABLED.equals(functionStatus);
    }

    public String getFunctionStatus() {
        return functionStatus;
    }

    public void setFunctionStatus(String functionStatus) {
        this.functionStatus = functionStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAllowRoles() {
        return allowRoles;
    }

    public void setAllowRoles(List<String> allowRoles) {
        this.allowRoles = allowRoles;
    }

    public void addAllowRole(String roleCode) {
        Guard.notNull(roleCode, "the parameter [roleCode] is required.");
        if(this.allowRoles == null) {
            this.allowRoles = new ArrayList<>();
        }
        this.allowRoles.add(roleCode);
    }

    public List<String> getAllowUsers() {
        return allowUsers;
    }

    public void setAllowUsers(List<String> allowUsers) {
        this.allowUsers = allowUsers;
    }

    public void addAllowUser(String user) {
        Guard.notEmpty(user, "the parameter user is required.");
        if(this.allowUsers == null) {
            this.allowUsers = new ArrayList<>();
        }
        this.allowUsers.add(user);
    }

    public boolean isType(String type) {
        return StringUtils.equals(this.type, type);
    }

    @Override
    public int hashCode() {
        return resourceCode.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
			return true;
		}

		if (obj instanceof FunctionInfo) {
			return resourceCode.equals(((FunctionInfo) obj).resourceCode);
		}

		return false;
    }
    
}
