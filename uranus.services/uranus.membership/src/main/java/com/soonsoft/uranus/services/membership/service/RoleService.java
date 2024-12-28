package com.soonsoft.uranus.services.membership.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.soonsoft.uranus.security.authorization.IRoleManager;
import com.soonsoft.uranus.security.authorization.event.IRoleChangedListener;
import com.soonsoft.uranus.security.authorization.event.RoleChangedEvent;
import com.soonsoft.uranus.security.entity.RoleInfo;
import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.services.membership.dao.AuthRoleDAO;
import com.soonsoft.uranus.services.membership.constant.FunctionStatusEnum;
import com.soonsoft.uranus.services.membership.dao.AuthPermissionDAO;
import com.soonsoft.uranus.services.membership.dao.AuthUserRoleRelationDAO;
import com.soonsoft.uranus.services.membership.model.Transformer;
import com.soonsoft.uranus.services.membership.po.AuthRole;
import com.soonsoft.uranus.services.membership.po.AuthPermission;
import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.collection.CollectionUtils;
import com.soonsoft.uranus.core.common.collection.MapUtils;
import com.soonsoft.uranus.core.common.event.IEventListener;
import com.soonsoft.uranus.core.common.event.SimpleEventListener;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.model.data.IPagingList;
import com.soonsoft.uranus.core.model.data.Page;
import com.soonsoft.uranus.core.model.data.PagingList;
import com.soonsoft.uranus.data.common.TransactionHelper;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

public class RoleService implements IRoleManager, IRoleChangedListener<AuthRole> {

    private AuthRoleDAO roleDAO;

    private AuthUserRoleRelationDAO userRoleRelationDAO;

    private AuthPermissionDAO permissionDAO;

    // 事件定义
    private IEventListener<RoleChangedEvent<AuthRole>> roleChangedDelegate = new SimpleEventListener<>(); 

    public RoleService(
            AuthRoleDAO roleDAO, 
            AuthUserRoleRelationDAO userRoleRelationDAO,
            AuthPermissionDAO permissionDAO) {

        this.roleDAO = roleDAO;
        this.userRoleRelationDAO = userRoleRelationDAO;
        this.permissionDAO = permissionDAO;

    }

    // #region IRoleManager methods

	@Override
	public IPagingList<RoleInfo> queryRoles(Map<String, Object> params, int pageIndex, int pageSize) {
        Page page = new Page(pageIndex, pageSize);
        List<AuthRole> roleList = queryRoles(params, page);
        List<RoleInfo> result = roleList.stream()
            .map(r -> Transformer.toSecurityRole(r))
            .collect(Collectors.toList());

        PagingList<RoleInfo> pagingList = new PagingList<>(result, page.getTotal());
        pagingList.setPageIndex(page.getPageIndex());
        pagingList.setPageSize(page.getPageSize());

        return pagingList;
    }

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
        Guard.notEmpty(role.getRoleCode(), "the RoleInfo.role can not be null.");

        AuthRole authRole = Transformer.toAuthRole(role);
        return updateRole(authRole);
    }

    @Override
    @Transactional
    public boolean deleteRole(String roleId) {
        Guard.notEmpty(roleId, "the roleId is required.");

        int effectRows = roleDAO.delete(UUID.fromString(roleId));
        return effectRows > 0;
    }

    @Override
    public Collection<GrantedAuthority> getUserRoles(UserInfo user) {
        Guard.notNull(user, "the user is required.");
        Guard.notEmpty(user.getUserId(), "the UserInfo.userId can not be null.");

        List<AuthRole> data = userRoleRelationDAO.selectByUserId(UUID.fromString(user.getUserId()));
        if (data == null || data.isEmpty()) {
            return null;
        }

        List<GrantedAuthority> roles = new ArrayList<>(data.size());
        data.forEach(i -> roles.add(Transformer.toSecurityRole(i)));
        return roles;
    }

    @Override
    public Map<String, List<RoleInfo>> getFunctionRoles(Collection<String> resourceCodes) {
        if (resourceCodes == null || resourceCodes.isEmpty()) {
            return null;
        }

        Set<UUID> functionIdSet = resourceCodes.stream().map(i -> UUID.fromString(i)).collect(Collectors.toSet());
        Map<UUID, Set<Object>> functionRoleMap = permissionDAO.selectByFunctions(functionIdSet, FunctionStatusEnum.ENABLED.Value);
        Map<String, List<RoleInfo>> result = MapUtils.createLinkedHashMap(resourceCodes.size());

        if (functionRoleMap != null) {
            resourceCodes.forEach(i -> {
                Set<Object> roleSet = functionRoleMap.get(UUID.fromString(i));
                if (roleSet != null) {
                    List<RoleInfo> roles = new ArrayList<>(roleSet.size());
                    for (Object item : roleSet) {
                        if (item != null) {
                            roles.add(Transformer.toSecurityRole((AuthRole) item));
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

        List<AuthRole> roles = roleDAO.selectRole(params, page);
        if (!CollectionUtils.isEmpty(roles)) {
            Set<UUID> roleIdSet = roles.stream().map(i -> i.getRoleId()).collect(Collectors.toSet());
            Map<UUID, Set<Object>> menuMap = permissionDAO.selectByRoles(roleIdSet, null);
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

        if (role.getRoleId() == null) {
            role.setRoleId(UUID.randomUUID());
        }
        int effectRows = 0;
        List<Object> menuIdList = role.getMenus();
        if (!CollectionUtils.isEmpty(menuIdList)) {
            for (Object item : menuIdList) {
                AuthPermission roleIdFunctionId = new AuthPermission();
                roleIdFunctionId.setRoleId(role.getRoleId());
                roleIdFunctionId.setFunctionId((UUID) item);
                effectRows += permissionDAO.insert(roleIdFunctionId);
            }
        }

        effectRows += roleDAO.insert(role);
        boolean result = effectRows > 0;
        if(result) {
            onRoleChanged(role);
        }
        return result;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    public boolean updateRole(AuthRole role) {
        Guard.notNull(role, "the AuthRole is required.");
        Guard.notNull(role.getRoleId(), "the AuthRole.roleId is required.");

        int effectRows = 0;
        List<Object> menuIdList = role.getMenus();
        if (!CollectionUtils.isEmpty(menuIdList)) {
            permissionDAO.deleteByRoleId(role.getRoleId());
            for (Object item : menuIdList) {
                AuthPermission roleIdFunctionId = new AuthPermission();
                roleIdFunctionId.setRoleId(role.getRoleId());
                roleIdFunctionId.setFunctionId((UUID) item);
                effectRows += permissionDAO.insert(roleIdFunctionId);
            }
        }

        effectRows = roleDAO.update(role);
        boolean result = effectRows > 0;
        if(result) {
            onRoleChanged(role);
        }
        return result;
    }

    public List<String> getFunctionIdList(String roleId) {
        Guard.notEmpty(roleId, "the roleId is required.");

        UUID roleGuid = UUID.fromString(roleId);

        List<UUID> roleIdList = new ArrayList<>();
        roleIdList.add(roleGuid);
        Map<UUID, Set<Object>> menuMap = permissionDAO.selectByRoles(roleIdList, FunctionStatusEnum.ENABLED.Value);
        
        List<String> functionIdList = new ArrayList<>();
        if(menuMap != null) {
            Set<Object> functionIdSet = menuMap.get(roleGuid);
            if(functionIdSet != null) {
                functionIdSet.forEach(i -> functionIdList.add(StringUtils.toString(i)));
            }
        }
        return functionIdList;
    }

    //#region 事件

    @Override
    public void addRoleChanged(Consumer<RoleChangedEvent<AuthRole>> eventHandler) {
        roleChangedDelegate.on(eventHandler);
    }

    @Override
    public void removeRoleChanged(Consumer<RoleChangedEvent<AuthRole>> eventHandler) {
        roleChangedDelegate.off(eventHandler);
    }

    protected void onRoleChanged(AuthRole authRole) {
        TransactionHelper.executeAfterCommit(() -> {
            roleChangedDelegate.trigger(new RoleChangedEvent<AuthRole>(authRole, data -> data));
        });
    }

    //#endregion
}