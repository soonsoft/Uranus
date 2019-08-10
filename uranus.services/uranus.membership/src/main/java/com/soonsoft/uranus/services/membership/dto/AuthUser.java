package com.soonsoft.uranus.services.membership.dto;

import java.util.Date;
import java.util.List;

/**
 * AuthUser
 */
public class AuthUser {

    public static final Integer ENABLED = 1;

    public static final Integer DISABLED = 0;

    private String userId;

    private String userName;

    private String nickName;

    private String cellPhone;

    /**
     * 状态 1: 有效, 2: 无效
     */
    private Integer status = ENABLED;

    private Date createTime;

    /** 关联的角色 */
    private List<Object> roles;

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the nickName
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * @param nickName the nickName to set
     */
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * @return the cellPhone
     */
    public String getCellPhone() {
        return cellPhone;
    }

    /**
     * @param cellPhone the cellPhone to set
     */
    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status == null ? ENABLED : status.intValue();
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return the createTime
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime the createTime to set
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<Object> getRoles() {
        return roles;
    }

    public void setRoles(List<Object> roles) {
        this.roles = roles;
    }
    
    
}