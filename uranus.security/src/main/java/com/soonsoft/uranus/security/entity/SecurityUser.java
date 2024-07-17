package com.soonsoft.uranus.security.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.soonsoft.uranus.core.common.lang.StringUtils;

public class SecurityUser extends UserInfo implements UserDetails {

    private static final String EMPTY_PASSWORD = "NoPassword";
    private static final Set<GrantedAuthority> EMPTY_ROLES = new HashSet<>(0);

    public SecurityUser(UserInfo userInfo) {
        if(userInfo != null) {
            userInfo.copy(this);
        }
    }

    public SecurityUser(String username) {
        this(username, EMPTY_PASSWORD, null, EMPTY_ROLES);
    }

    public SecurityUser(String username, String password, String salt, Collection<GrantedAuthority> roles) {
        this(username, 
            StringUtils.isEmpty(password) ? EMPTY_PASSWORD : password, salt,
            null, null, null, 
            StatusConst.UserStatus.ENABLED,
            roles == null ? EMPTY_ROLES : roles);
    }

    public SecurityUser(
            String username, String password, String passwordSalt,
            String cellPhoneAreaCode, String cellPhone, String email, 
            String status, 
            Collection<? extends GrantedAuthority> authorities) {
        this.setUserName(username);
        this.setCellPhoneAreaCode(cellPhoneAreaCode);
        this.setCellPhone(cellPhone);
        this.setEmail(email);
        this.setStatus(status);

        this.setPassword(password, passwordSalt);
    }

    public String getPasswordSalt() {
        return getPasswordInfo() != null ? getPasswordInfo().getPasswordSalt() : "";
    }


    //#region implements UserDetails

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getRoles();
    }

    @Override
    public String getPassword() {
        return getPasswordInfo() != null ? getPasswordInfo().getPassword() : "";
    }

    @Override
    public String getUsername() {
        return getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return !StatusConst.UserStatus.EXPIRED.equals(this.getStatus());
    }

    @Override
    public boolean isAccountNonLocked() {
        return !StatusConst.UserStatus.LOCKED.equals(this.getStatus());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return StatusConst.UserStatus.ENABLED.equals(this.getStatus());
    }

    //#endregion
    
}
