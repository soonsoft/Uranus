package com.soonsoft.uranus.security.entity;

import java.util.Date;

import com.soonsoft.uranus.security.entity.StatusConst.PasswordStatus;

public class PasswordInfo {

    private String id;
    private String password;
    private String passwordSalt;
    private String passwordType;
    private String passwordStatus = PasswordStatus.ENABLED;
    private Date createTime;

    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }
    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public String getPasswordType() {
        return passwordType;
    }
    public void setPasswordType(String passwordType) {
        this.passwordType = passwordType;
    }

    public String getPasswordStatus() {
        return passwordStatus;
    }
    public void setPasswordStatus(String passwordStatus) {
        this.passwordStatus = passwordStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
}
