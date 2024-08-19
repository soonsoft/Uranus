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
import com.soonsoft.uranus.security.entity.security.SecurityRole;
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
import com.soonsoft.uranus.core.common.collection.CollectionUtils;
import com.soonsoft.uranus.core.common.lang.StringUtils;

/**
 * 类型变化器
 */
public abstract class Transformer {

    //#region Users

    public static UserInfo toUserInfo(AuthUser authUser) {
        if(authUser == null) {
            return null;
        }

        List<AuthRole> roles = null;
        if(authUser.getRoles() != null) {
            roles = 
                authUser.getRoles().stream()
                    .map(i -> {
                        AuthRole role = new AuthRole();
                        role.setRoleId((UUID) i);
                        return role;
                    })
                    .toList();
        }

        List<AuthPrivilege> privileges = null;
        if(authUser.getFunctions() != null) {
            privileges = 
                authUser.getFunctions().stream()
                    .map(p -> {
                        if(p instanceof AuthPrivilege privilege) {
                            return privilege;
                        } else {
                            AuthPrivilege privilege = new AuthPrivilege();
                            privilege.setFunctionId((UUID) p);
                            return privilege;
                        }
                    })
                    .toList();
        }

        return toUserInfo(authUser, null, roles, privileges);
    }

    public static UserInfo toUserInfo(
        AuthUser authUser, 
        AuthPassword password, 
        Collection<AuthRole> roles, 
        Collection<AuthPrivilege> privileges) {

        if(authUser == null) {
            return null;
        }
        
        Set<RoleInfo> roleInfoSet = new HashSet<>();
        if(roles != null) {
            roles.forEach(r -> roleInfoSet.add(toSecurityRole(r)));
        }

        Set<PrivilegeInfo> privilegeSet = new HashSet<>();
        if(privileges != null) {
            privileges.forEach(p -> privilegeSet.add(toPrivilegeInfo(p)));
        }

        PasswordInfo passwordInfo = null;
        if(password != null) {
            passwordInfo = new PasswordInfo();
            passwordInfo.setPassword(password.getPasswordValue());
            passwordInfo.setPasswordSalt(password.getPasswordSalt());
            passwordInfo.setPasswordStatus(PasswordStatusEnum.valueOf(password.getStatus()).name());
            passwordInfo.setPasswordType(PasswordTypeEnum.valueOf(password.getPasswordType()).name());
        }

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
        
        if(!StringUtils.isEmpty(userInfo.getUserId())) {
            authUser.setUserId(UUID.fromString(userInfo.getUserId()));
        }
        authUser.setUserName(userInfo.getUserName());
        authUser.setNickName(userInfo.getNickName());
        authUser.setStatus(UserStatusEnum.valueOf(userInfo.getStatus()).Value);
        authUser.setCellPhone(userInfo.getCellPhone());
        authUser.setCreateTime(userInfo.getCreateTime());

        if(!CollectionUtils.isEmpty(userInfo.getPrivileges())) {
            authUser.setFunctions(
                userInfo.getPrivileges().stream()
                    .map(p -> (Object) UUID.fromString(p.getResourceCode()))
                    .toList()
            );
        }

        if(!CollectionUtils.isEmpty(userInfo.getRoles())) {
            authUser.setRoles(
                userInfo.getRoles().stream()
                    .map(r -> (Object) UUID.fromString(r.getRoleCode()))
                    .toList()
            );
        }

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

    //#endregion

    //#region Privileges

    public static AuthPrivilege toAuthPrivilege(PrivilegeInfo privilegeInfo) {
        AuthPrivilege privilege = new AuthPrivilege();
        privilege.setFunctionId(UUID.fromString(privilegeInfo.getResourceCode()));
        privilege.setFunctionName(privilegeInfo.getResourceName());
        privilege.setUserId(
            privilegeInfo.getUserId() != null 
                ? UUID.fromString(privilegeInfo.getUserId()) 
                : null);
        return privilege;
    }

    public static PrivilegeInfo toPrivilegeInfo(AuthPrivilege authPrivilege) {
        return new MembershipPrivilegeInfo(
            authPrivilege.getUserId() != null ? authPrivilege.getUserId().toString() : null, 
            authPrivilege.getFunctionId() != null ? authPrivilege.getFunctionId().toString() : null, 
            authPrivilege.getFunctionName());
    }

    //#endregion

    //#region Roles

    public static AuthRole toAuthRole(RoleInfo role) {
        AuthRole authRole = new AuthRole();
        if(StringUtils.isEmpty(role.getRoleCode()) 
            || StringUtils.equals(MembershipRole.EMPTY_ROLE, role.getRoleCode())) {
            authRole.setRoleId(UUID.randomUUID());
        } else {
            authRole.setRoleId(UUID.fromString(role.getRoleCode()));
        }
        authRole.setRoleName(role.getRoleName());
        authRole.setDescription(role.getDescription());
        authRole.setStatus(RoleStatusEnum.valueOf(role.getRoleStatus()).Value);
        
        if(!CollectionUtils.isEmpty(role.getResourceCodeList())) {
            authRole.setMenus(
                role.getResourceCodeList().stream()
                    .map(i -> (Object) UUID.fromString(i))
                    .toList());
        }
        
        return authRole;
    }

    public static SecurityRole toSecurityRole(AuthRole authRole) {
        MembershipRole role = new MembershipRole(authRole.getRoleId().toString(), authRole.getRoleName());
        role.setDescription(authRole.getDescription());
        role.setRoleStatus(RoleStatusEnum.valueOf(authRole.getStatus()).name());
        
        if(!CollectionUtils.isEmpty(authRole.getMenus())) {
            role.setResourceCodeList(
                authRole.getMenus().stream().map(i -> i.toString()).toList());
        }
        
        return role;
    }

    //#endregion

    //#region Functions

    public static FunctionInfo toFunctionInfo(SysMenu sysMenu) {
        FunctionInfo functionInfo = null;

        if(StringUtils.equals(FunctionTypeEnum.MENU.Value, sysMenu.getType())) {
            MenuInfo menu = new MenuInfo(
                sysMenu.getFunctionId().toString(), sysMenu.getFunctionName(), sysMenu.getUrl());
            menu.setParentResourceCode(sysMenu.getParentId().toString());
            menu.setFunctionStatus(FunctionStatusEnum.valueOf(sysMenu.getStatus()).name());
            menu.setIcon(sysMenu.getIcon());

            functionInfo = menu;
        } else {
            functionInfo = new FunctionInfo(
                sysMenu.getFunctionId().toString(), sysMenu.getFunctionName(), sysMenu.getUrl());
        }

        Collection<AuthRole> roles = sysMenu.getRoles();
        if(roles != null) {
            List<String> roleList = new ArrayList<>(roles.size());
            for(AuthRole item : roles) {
                if(item != null) {
                    roleList.add(item.getRoleId().toString());
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

    public static SysMenu toSysMenu(MenuInfo menuInfo) {
        SysMenu sysMenu = new SysMenu();
        sysMenu.setFunctionId(UUID.fromString(menuInfo.getResourceCode()));
        sysMenu.setFunctionName(menuInfo.getName());
        sysMenu.setDescription(menuInfo.getDescription());
        sysMenu.setParentId(UUID.fromString(menuInfo.getParentResourceCode()));
        sysMenu.setType(FunctionTypeEnum.valueOf(menuInfo.getType()).Value);
        sysMenu.setStatus(FunctionStatusEnum.valueOf(menuInfo.getFunctionStatus()).Value);
        sysMenu.setUrl(menuInfo.getUrl());
        sysMenu.setIcon(menuInfo.getIcon());

        if(menuInfo.getAllowRoles() != null) {
            List<AuthRole> roles = new ArrayList<>(menuInfo.getAllowRoles().size());
            for(String role : menuInfo.getAllowRoles()) {
                AuthRole authRole = new AuthRole();
                authRole.setRoleId(UUID.fromString(role));
                roles.add(authRole);
            }
        }

        return sysMenu;
    }

    //#endregion
}