package com.soonsoft.uranus.services.membership.po;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "auth_password")
public class AuthPassword {
    
    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "password_value")
    private String passwordValue;

    @Column(name = "password_salt")
    private String passwordSalt;

    @Column(name = "password_changed_time")
    private Date passwordChangedTime;

    @Column(name = "create_time")
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