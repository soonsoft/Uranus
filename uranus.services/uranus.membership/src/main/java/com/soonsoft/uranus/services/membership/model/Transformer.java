package com.soonsoft.uranus.services.membership.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.soonsoft.uranus.security.entity.MenuInfo;
import com.soonsoft.uranus.security.entity.RoleInfo;
import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.services.membership.dto.AuthPassword;
import com.soonsoft.uranus.services.membership.dto.AuthRole;
import com.soonsoft.uranus.services.membership.dto.AuthUser;
import com.soonsoft.uranus.services.membership.dto.SysMenu;
import com.soonsoft.uranus.core.common.lang.StringUtils;

import org.springframework.security.core.GrantedAuthority;

/**
 * 类型变化器
 */
public abstract class Transformer {

    public static UserInfo toUserInfo(AuthUser authUser, AuthPassword password, Collection<AuthRole> roles) {
        Set<GrantedAuthority> roleInfoSet = new HashSet<>();
        if(roles != null) {
            roles.forEach(r -> roleInfoSet.add(toRoleInfo(r)));
        }

        boolean enabled = authUser.getStatus() == AuthUser.ENABLED;
        UserInfo user = new UserInfo(authUser.getUserName(), password.getPasswordValue(), enabled, true, true, true, roleInfoSet); 

        user.setUserId(authUser.getUserId());
        user.setCellPhone(authUser.getCellPhone());
        user.setNickName(authUser.getNickName());
        user.setCreateTime(authUser.getCreateTime());

        return user;
    }

    public static AuthUser toAuthUser(UserInfo userInfo) {
        AuthUser authUser = new AuthUser();
        
        authUser.setUserId(userInfo.getUserId());
        authUser.setUserName(userInfo.getUsername());
        authUser.setNickName(userInfo.getNickName());
        authUser.setStatus(userInfo.isEnabled() ? AuthUser.ENABLED : AuthUser.DISABLED);
        authUser.setCellPhone(userInfo.getCellPhone());
        authUser.setCreateTime(userInfo.getCreateTime());

        return authUser;
    }

    public static AuthRole toAuthRole(RoleInfo role) {
        AuthRole authRole = new AuthRole();
        if(StringUtils.isEmpty(role.getRole()) 
            || StringUtils.equals(MembershipRole.EMPTY_ROLE, role.getRole())) {
            authRole.setRoleId(UUID.randomUUID().toString());
        } else {
            authRole.setRoleId(role.getRole());
        }
        authRole.setRoleName(role.getRoleName());
        authRole.setDescription(role.getDescription());
        authRole.setStatus(role.isEnable() ? AuthRole.ENABLED : AuthRole.DISABLED);
        return authRole;
    }

    public static RoleInfo toRoleInfo(AuthRole authRole) {
        MembershipRole role = new MembershipRole(authRole.getRoleId(), authRole.getRoleName());
        role.setDescription(authRole.getDescription());
        role.setEnable(AuthRole.ENABLED.equals(authRole.getStatus()));
        return role;
    }
    
    public static MenuInfo toMenuInfo(SysMenu sysMenu) {
        MenuInfo menu = new MenuInfo(sysMenu.getFunctionId(), sysMenu.getFunctionName());
        menu.setParentResourceCode(sysMenu.getParentId());
        menu.setEnabled(SysMenu.STATUS_ENABLED.equals(sysMenu.getStatus()));
        menu.setIcon(sysMenu.getIcon());
        menu.setUrl(sysMenu.getUrl());

        Collection<AuthRole> roles = sysMenu.getRoles();
        if(roles != null) {
            List<RoleInfo> roleList = new ArrayList<>(roles.size());
            for(Object item : roles) {
                if(item != null) {
                    roleList.add(toRoleInfo((AuthRole) item));
                }
            }
            menu.setAllowRoles(roleList);
        }
        return menu;
    }
}