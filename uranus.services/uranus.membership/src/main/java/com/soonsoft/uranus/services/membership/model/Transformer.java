package com.soonsoft.uranus.services.membership.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.soonsoft.uranus.security.entity.FunctionInfo;
import com.soonsoft.uranus.security.entity.MenuInfo;
import com.soonsoft.uranus.security.entity.PasswordInfo;
import com.soonsoft.uranus.security.entity.PrivilegeInfo;
import com.soonsoft.uranus.security.entity.RoleInfo;
import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.services.membership.constant.FunctionStatusEnum;
import com.soonsoft.uranus.services.membership.constant.FunctionTypeEnum;
import com.soonsoft.uranus.services.membership.constant.PasswordStatusEnum;
import com.soonsoft.uranus.services.membership.constant.PasswordTypeEnum;
import com.soonsoft.uranus.services.membership.constant.RoleStatusEnum;
import com.soonsoft.uranus.services.membership.constant.UserStatusEnum;
import com.soonsoft.uranus.services.membership.po.AuthPassword;
import com.soonsoft.uranus.services.membership.po.AuthPrivilege;
import com.soonsoft.uranus.services.membership.po.AuthRole;
import com.soonsoft.uranus.services.membership.po.AuthUser;
import com.soonsoft.uranus.services.membership.po.SysMenu;
import com.soonsoft.uranus.core.common.lang.StringUtils;

/**
 * 类型变化器
 */
public abstract class Transformer {

    public static UserInfo toUserInfo(
        AuthUser authUser, 
        AuthPassword password, 
        Collection<AuthRole> roles, 
        Collection<AuthPrivilege> functions) {
        
        Set<RoleInfo> roleInfoSet = new HashSet<>();
        if(roles != null) {
            roles.forEach(r -> roleInfoSet.add(toRoleInfo(r)));
        }

        Set<PrivilegeInfo> privilegeSet = new HashSet<>();
        if(functions != null) {
            functions.forEach(p -> privilegeSet.add(
                new MembershipPrivilegeInfo(p.getFunctionId().toString(), p.getFunctionName())));
        }

        PasswordInfo passwordInfo = new PasswordInfo();
        passwordInfo.setPassword(password.getPasswordValue());
        passwordInfo.setPasswordSalt(password.getPasswordSalt());
        passwordInfo.setPasswordStatus(PasswordStatusEnum.valueOf(password.getStatus()).name());
        passwordInfo.setPasswordType(PasswordTypeEnum.valueOf(password.getPasswordType()).name());

        UserInfo user = new UserInfo();
        user.setUserId(authUser.getUserId().toString());
        user.setUserName(authUser.getUserName());
        user.setCellPhone(authUser.getCellPhone());
        user.setNickName(authUser.getNickName());
        user.setStatus(UserStatusEnum.valueOf(authUser.getStatus()).name());
        user.setCreateTime(authUser.getCreateTime());
        user.setPasswordInfo(passwordInfo);
        user.setRoles(roleInfoSet);

        return user;
    }

    public static AuthUser toAuthUser(UserInfo userInfo) {
        AuthUser authUser = new AuthUser();
        
        authUser.setUserId(UUID.fromString(userInfo.getUserId()));
        authUser.setUserName(userInfo.getUserName());
        authUser.setNickName(userInfo.getNickName());
        authUser.setStatus(UserStatusEnum.valueOf(userInfo.getStatus()).Value);
        authUser.setCellPhone(userInfo.getCellPhone());
        authUser.setCreateTime(userInfo.getCreateTime());

        return authUser;
    }

    public static PasswordInfo toPasswordInfo(AuthPassword password) {
        PasswordInfo passwordInfo = new PasswordInfo();
        passwordInfo.setId(password.getUserId().toString());
        passwordInfo.setPassword(password.getPasswordValue());
        passwordInfo.setPasswordSalt(password.getPasswordSalt());
        passwordInfo.setPasswordType(PasswordTypeEnum.valueOf(password.getPasswordType()).name());
        passwordInfo.setPasswordStatus(PasswordStatusEnum.valueOf(password.getStatus()).name());
        password.setCreateTime(password.getCreateTime());
        return passwordInfo;
    }

    public static AuthRole toAuthRole(RoleInfo role) {
        AuthRole authRole = new AuthRole();
        if(StringUtils.isEmpty(role.getRole()) 
            || StringUtils.equals(MembershipRole.EMPTY_ROLE, role.getRole())) {
            authRole.setRoleId(UUID.randomUUID());
        } else {
            authRole.setRoleId(UUID.fromString(role.getRole()));
        }
        authRole.setRoleName(role.getRoleName());
        authRole.setDescription(role.getDescription());
        authRole.setStatus(role.isEnable() ? RoleStatusEnum.ENABLED.Value : RoleStatusEnum.DISABLED.Value);
        return authRole;
    }

    public static RoleInfo toRoleInfo(AuthRole authRole) {
        MembershipRole role = new MembershipRole(authRole.getRoleId().toString(), authRole.getRoleName());
        role.setDescription(authRole.getDescription());
        role.setEnable(RoleStatusEnum.ENABLED.eq(authRole.getStatus()));
        return role;
    }

    public static FunctionInfo toFunctionInfo(SysMenu sysMenu) {
        FunctionInfo functionInfo = null;

        if(StringUtils.equals(FunctionTypeEnum.MENU.Value, sysMenu.getType())) {
            MenuInfo menu = new MenuInfo(
                sysMenu.getFunctionId().toString(), sysMenu.getFunctionName(), sysMenu.getUrl());
            menu.setParentResourceCode(sysMenu.getParentId().toString());
            menu.setEnabled(FunctionStatusEnum.ENABLED.eq(sysMenu.getStatus()));
            menu.setIcon(sysMenu.getIcon());

            functionInfo = menu;
        } else {
            functionInfo = new FunctionInfo(
                sysMenu.getFunctionId().toString(), sysMenu.getFunctionName(), sysMenu.getUrl());
        }

        Collection<AuthRole> roles = sysMenu.getRoles();
        if(roles != null) {
            List<RoleInfo> roleList = new ArrayList<>(roles.size());
            for(Object item : roles) {
                if(item != null) {
                    roleList.add(toRoleInfo((AuthRole) item));
                }
            }
            functionInfo.setAllowRoles(roleList);
        }
        return functionInfo;
    }
    
    public static MenuInfo toMenuInfo(SysMenu sysMenu) {
        if(!StringUtils.equals(FunctionTypeEnum.MENU.Value, sysMenu.getType())) {
            throw new IllegalArgumentException("the parameter sysMenu is not a menu object.");
        }

        return (MenuInfo) toFunctionInfo(sysMenu);
    }
}