package com.soonsoft.uranus.security.entity;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.soonsoft.uranus.core.common.lang.StringUtils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * 用户信息
 */
public class UserInfo extends User {

    private static final String EMPTY_PASSWORD = "NoPassword";

    private static final Set<GrantedAuthority> EMPTY_ROLES = new HashSet<>(0);

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 手机号码
     */
    private String cellPhone;

    /**
     * 密码盐值
     */
    private String passwordSalt;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 用户拥有的特权
     */
    private Set<PrivilegeInfo> privileges;

    public UserInfo(String username) {
        this(username, EMPTY_PASSWORD);
    }

    public UserInfo(String username, String password) {
        this(username, password, EMPTY_ROLES);
    }

    public UserInfo(String username, String password, Collection<GrantedAuthority> roles) {
        super(username, 
            StringUtils.isEmpty(password) ? EMPTY_PASSWORD : password, 
            roles == null ? EMPTY_ROLES : roles);
    }

    public UserInfo(String username, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

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

    public Set<PrivilegeInfo> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(Set<PrivilegeInfo> privileges) {
        this.privileges = privileges;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append("; ");
        sb.append("cellPhone: ").append(this.cellPhone).append("; ");
        sb.append("createTime: ").append(this.createTime).append("; ");
        sb.append("nickName: ").append(this.nickName).append("; ");
        sb.append("passwordSalt: ").append("[PROTECTED]");
        return sb.toString();
    }
    
}