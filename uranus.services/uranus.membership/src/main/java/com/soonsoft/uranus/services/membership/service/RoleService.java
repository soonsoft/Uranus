package com.soonsoft.uranus.services.membership.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import com.soonsoft.uranus.data.entity.Page;
import com.soonsoft.uranus.security.authorization.IRoleManager;
import com.soonsoft.uranus.security.authorization.event.IRoleChangedListener;
import com.soonsoft.uranus.security.authorization.event.RoleChangedEvent;
import com.soonsoft.uranus.security.entity.RoleInfo;
import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.services.membership.dao.AuthRoleDAO;
import com.soonsoft.uranus.services.membership.dao.AuthRolesInFunctionsDAO;
import com.soonsoft.uranus.services.membership.dao.AuthUsersInRolesDAO;
import com.soonsoft.uranus.services.membership.dto.AuthRole;
import com.soonsoft.uranus.services.membership.dto.AuthRoleIdAndFunctionId;
import com.soonsoft.uranus.services.membership.dto.SysMenu;
import com.soonsoft.uranus.services.membership.model.Transformer;
import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.collection.CollectionUtils;
import com.soonsoft.uranus.core.common.collection.MapUtils;
import com.soonsoft.uranus.core.common.event.IEventListener;
import com.soonsoft.uranus.core.common.event.SimpleEventListener;
import com.soonsoft.uranus.core.common.lang.StringUtils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * RoleService
 */
public class RoleService implements IRoleManager, IRoleChangedListener<String> {

    private AuthRoleDAO roleDAO;

    private AuthUsersInRolesDAO usersInRolesDAO;

    private AuthRolesInFunctionsDAO rolesInFunctionsDAO;

    // 事件定义
    private IEventListener<RoleChangedEvent<String>> roleChangedDelegate = new SimpleEventListener<>(); 

    public RoleService() {
    }

    public AuthRoleDAO getRoleDAO() {
        return roleDAO;
    }

    public void setRoleDAO(AuthRoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }

    public AuthRolesInFunctionsDAO getRolesInFunctionsDAO() {
        return rolesInFunctionsDAO;
    }

    public void setRolesInFunctionsDAO(AuthRolesInFunctionsDAO rolesInFunctionsDAO) {
        this.rolesInFunctionsDAO = rolesInFunctionsDAO;
    }

    public AuthUsersInRolesDAO getUsersInRolesDAO() {
        return usersInRolesDAO;
    }

    public void setUsersInRolesDAO(AuthUsersInRolesDAO usersInRolesDAO) {
        this.usersInRolesDAO = usersInRolesDAO;
    }

    // #region IRoleManager methods

    @Override
    @Transactional
    public boolean createRole(RoleInfo role) {
        Guard.notNull(role, "the RoleInfo is required.");

        AuthRole authRole = Transformer.toAuthRole(role);
        return createRole(authRole);
    }

    @Override
    @Transactional
    public boolean updateRole(RoleInfo role) {
        Guard.notNull(role, "the RoleInfo is required.");
        Guard.notEmpty(role.getRole(), "the RoleInfo.role can not be null.");

        AuthRole authRole = Transformer.toAuthRole(role);
        return updateRole(authRole);
    }

    @Override
    @Transactional
    public boolean deleteRole(String roleId) {
        Guard.notEmpty(roleId, "the roleId is required.");

        int effectRows = roleDAO.delete(roleId);
        return effectRows > 0;
    }

    @Override
    public Collection<GrantedAuthority> getUserRoles(UserInfo user) {
        Guard.notNull(user, "the user is required.");
        Guard.notEmpty(user.getUserId(), "the UserInfo.userId can not be null.");

        List<AuthRole> data = usersInRolesDAO.selectByUserId(user.getUserId());
        if (data == null || data.isEmpty()) {
            return null;
        }

        List<GrantedAuthority> roles = new ArrayList<>(data.size());
        data.forEach(i -> roles.add(Transformer.toRoleInfo(i)));
        return roles;
    }

    @Override
    public Map<String, List<RoleInfo>> getFunctionRoles(Collection<String> resourceCodes) {
        if (resourceCodes == null || resourceCodes.isEmpty()) {
            return null;
        }

        List<String> functionIdList = new ArrayList<>(resourceCodes.size());
        functionIdList.addAll(resourceCodes);
        Map<String, Set<Object>> functionRoleMap = rolesInFunctionsDAO.selectByFunctions(functionIdList, 1);
        Map<String, List<RoleInfo>> result = MapUtils.createLinkedHashMap(resourceCodes.size());

        if (functionRoleMap != null) {
            resourceCodes.forEach(i -> {
                Set<Object> roleSet = functionRoleMap.get(i);
                if (roleSet != null) {
                    List<RoleInfo> roles = new ArrayList<>(roleSet.size());
                    for (Object item : roleSet) {
                        if (item != null) {
                            roles.add(Transformer.toRoleInfo((AuthRole) item));
                        }
                    }
                    result.put(i, roles);
                } else {
                    result.put(i, null);
                }
            });
        }

        return result;
    }

    // #endregion

    public List<AuthRole> queryRoles(Map<String, Object> params, Page page) {

        if (page == null) {
            page = new Page();
        }

        List<AuthRole> roles = roleDAO.select(params, page);
        if (!CollectionUtils.isEmpty(roles)) {
            List<String> roleIdList = new ArrayList<>(roles.size());
            for (AuthRole role : roles) {
                roleIdList.add(role.getRoleId());
            }
            Map<String, Set<Object>> menuMap = rolesInFunctionsDAO.selectByRoles(roleIdList, null);
            if (!MapUtils.isEmpty(menuMap)) {
                roles.forEach(role -> {
                    List<Object> idList = new ArrayList<>();
                    Set<Object> menus = menuMap.get(role.getRoleId());
                    if (!CollectionUtils.isEmpty(menus)) {
                        idList.addAll(menus);
                    }
                    role.setMenus(idList);
                });
            }
        }
        return roles;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    public boolean createRole(AuthRole role) {
        Guard.notNull(role, "the AuthRole is required.");

        if (StringUtils.isEmpty(role.getRoleId())) {
            role.setRoleId(UUID.randomUUID().toString());
        }
        int effectRows = 0;
        List<Object> menuIdList = role.getMenus();
        if (!CollectionUtils.isEmpty(menuIdList)) {
            for (Object item : menuIdList) {
                AuthRoleIdAndFunctionId roleIdFunctionId = new AuthRoleIdAndFunctionId();
                roleIdFunctionId.setRoleId(role.getRoleId());
                roleIdFunctionId.setFunctionId((String) item);
                effectRows += rolesInFunctionsDAO.insert(roleIdFunctionId);
            }
        }

        effectRows += roleDAO.insert(role);
        boolean result = effectRows > 0;
        if(result) {
            onRoleChanged(role.getRoleId());
        }
        return result;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    public boolean updateRole(AuthRole role) {
        Guard.notNull(role, "the AuthRole is required.");
        Guard.notEmpty(role.getRoleId(), "the AuthRole.roleId is required.");

        int effectRows = 0;
        List<Object> menuIdList = role.getMenus();
        if (!CollectionUtils.isEmpty(menuIdList)) {
            rolesInFunctionsDAO.deleteByRoleId(role.getRoleId());
            for (Object item : menuIdList) {
                AuthRoleIdAndFunctionId roleIdFunctionId = new AuthRoleIdAndFunctionId();
                roleIdFunctionId.setRoleId(role.getRoleId());
                roleIdFunctionId.setFunctionId((String) item);
                effectRows += rolesInFunctionsDAO.insert(roleIdFunctionId);
            }
        }

        effectRows = roleDAO.update(role);
        boolean result = effectRows > 0;
        if(result) {
            onRoleChanged(role.getRoleId());
        }
        return result;
    }

    public List<String> getFunctionIdList(String roleId) {
        Guard.notEmpty(roleId, "the roleId is required.");

        List<String> roleIdList = new ArrayList<>();
        roleIdList.add(roleId);
        Map<String, Set<Object>> menuMap = rolesInFunctionsDAO.selectByRoles(roleIdList, SysMenu.STATUS_ENABLED);
        
        List<String> functionIdList = new ArrayList<>();
        if(menuMap != null) {
            Set<Object> functionIdSet = menuMap.get(roleId);
            if(functionIdSet != null) {
                functionIdSet.forEach(i -> functionIdList.add((String)i));
            }
        }
        return functionIdList;
    }

    //#region 事件

    @Override
    public void addRoleChanged(Consumer<RoleChangedEvent<String>> eventHandler) {
        roleChangedDelegate.on(eventHandler);
    }

    @Override
    public void removeRoleChanged(Consumer<RoleChangedEvent<String>> eventHandler) {
        roleChangedDelegate.off(eventHandler);
    }

    protected void onRoleChanged(String roleId) {
        roleChangedDelegate.trigger(new RoleChangedEvent<String>(roleId, data -> data));
    }

    //#endregion
}