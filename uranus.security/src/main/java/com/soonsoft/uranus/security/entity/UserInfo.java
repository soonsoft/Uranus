package com.soonsoft.uranus.security.entity;


import java.util.Date;
import java.util.Set;

/**
 * 用户信息
 */
public class UserInfo {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 手机地区码（e.g. 中国：+86）
     */
    private String cellPhoneAreaCode;

    /**
     * 手机号码
     */
    private String cellPhone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户状态（Enabled, Disabled, Locked, Expired）
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 用户密码
     */
    private PasswordInfo passwordInfo;

    /**
     * 用户拥有的角色
     */
    private Set<RoleInfo> roles;

    /**
     * 用户拥有的特权
     */
    private Set<PrivilegeInfo> privileges;


    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getCellPhoneAreaCode() {
        return cellPhoneAreaCode;
    }
    public void setCellPhoneAreaCode(String cellPhoneAreaCode) {
        this.cellPhoneAreaCode = cellPhoneAreaCode;
    }

    public String getCellPhone() {
        return cellPhone;
    }
    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public PasswordInfo getPasswordInfo() {
        return passwordInfo;
    }
    public void setPasswordInfo(PasswordInfo passwordInfo) {
        this.passwordInfo = passwordInfo;
    }
    public void setPassword(String password, String passwordSalt) {
        this.passwordInfo = new PasswordInfo();
        this.passwordInfo.setPassword(password);
        this.passwordInfo.setPasswordSalt(passwordSalt);
    }

    public Set<PrivilegeInfo> getPrivileges() {
        return privileges;
    }
    public void setPrivileges(Set<PrivilegeInfo> privileges) {
        this.privileges = privileges;
    }

    public Set<RoleInfo> getRoles() {
        return roles;
    }
    public void setRoles(Set<RoleInfo> roles) {
        this.roles = roles;
    }

    public void copy(UserInfo dist) {
        dist.setUserId(this.getUserId());
        dist.setUserName(this.getUserName());
        dist.setCellPhoneAreaCode(this.getCellPhoneAreaCode());
        dist.setCellPhone(this.getCellPhone());
        dist.setEmail(this.getEmail());
        dist.setNickName(this.getNickName());
        dist.setStatus(this.getStatus());
        dist.setCreateTime(this.getCreateTime());
        dist.setRoles(this.getRoles());
        dist.setPrivileges(this.getPrivileges());
    }
    
}