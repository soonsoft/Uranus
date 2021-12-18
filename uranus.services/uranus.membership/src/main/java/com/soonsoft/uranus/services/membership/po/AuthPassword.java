package com.soonsoft.uranus.services.membership.po;

import java.util.Date;
import java.util.UUID;

/**
 * AuthPassword
 */
public class AuthPassword {
    
    private UUID userId;

    private String passwordValue;

    private String passwordSalt;

    private Date passwordChangedTime;

    private Date createTime;
    
    /**
     * @return the userId
     */
    public UUID getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    /**
     * @return the passwordValue
     */
    public String getPasswordValue() {
        return passwordValue;
    }

    /**
     * @param passwordValue the passwordValue to set
     */
    public void setPasswordValue(String passwordValue) {
        this.passwordValue = passwordValue;
    }

    /**
     * @return the passwordSalt
     */
    public String getPasswordSalt() {
        return passwordSalt;
    }

    /**
     * @param passwordSalt the passwordSalt to set
     */
    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    /**
     * @return the passwordChangedTime
     */
    public Date getPasswordChangedTime() {
        return passwordChangedTime;
    }

    /**
     * @param passwordChangedTime the passwordChangedTime to set
     */
    public void setPasswordChangedTime(Date passwordChangedTime) {
        this.passwordChangedTime = passwordChangedTime;
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

    
}