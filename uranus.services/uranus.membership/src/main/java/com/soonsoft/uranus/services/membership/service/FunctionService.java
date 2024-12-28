package com.soonsoft.uranus.services.membership.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.event.IEventListener;
import com.soonsoft.uranus.core.common.event.SimpleEventListener;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.data.common.TransactionHelper;
import com.soonsoft.uranus.core.common.collection.CollectionUtils;
import com.soonsoft.uranus.core.common.collection.MapUtils;

import com.soonsoft.uranus.util.caching.Cache;

import com.soonsoft.uranus.security.authorization.IFunctionManager;
import com.soonsoft.uranus.security.authorization.event.FunctionChangedEvent;
import com.soonsoft.uranus.security.authorization.event.IFunctionChangedListener;
import com.soonsoft.uranus.security.entity.FunctionInfo;
import com.soonsoft.uranus.security.entity.MenuInfo;
import com.soonsoft.uranus.security.entity.RoleInfo;
import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.security.entity.StatusConst.ResourceType;
import com.soonsoft.uranus.services.membership.constant.FunctionStatusEnum;
import com.soonsoft.uranus.services.membership.dao.AuthPermissionDAO;
import com.soonsoft.uranus.services.membership.dao.SysFunctionDAO;
import com.soonsoft.uranus.services.membership.dao.SysMenuDAO;
import com.soonsoft.uranus.services.membership.model.Transformer;
import com.soonsoft.uranus.services.membership.po.AuthRole;
import com.soonsoft.uranus.services.membership.po.AuthPermission;
import com.soonsoft.uranus.services.membership.po.SysMenu;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

public class FunctionService implements IFunctionManager, IFunctionChangedListener<SysMenu> {

    private SysFunctionDAO functionDAO;

    private SysMenuDAO menuDAO;

    private AuthPermissionDAO permissionDAO;

    private final Cache<String, FunctionInfo> functionStore;

    private List<String> sequence;

    private static final Object locker = new Object();

    /** 事件定义 */
    private final IEventListener<FunctionChangedEvent<SysMenu>> functionChangedDelegate = new SimpleEventListener<>();

    public FunctionService(
            SysFunctionDAO functionDAO,
            SysMenuDAO menuDAO,
            AuthPermissionDAO permissionDAO) {

        this(functionDAO, menuDAO, permissionDAO, new Cache<>(128));
    }

    public FunctionService(
            SysFunctionDAO functionDAO,
            SysMenuDAO menuDAO,
            AuthPermissionDAO permissionDAO,
            Cache<String, FunctionInfo> functionStore) {

        Guard.notNull(functionStore, "the parameter functionStore is required.");

        this.functionDAO = functionDAO;
        this.menuDAO = menuDAO;
        this.permissionDAO = permissionDAO;
        this.functionStore = functionStore;

        // 注册缓存更新
        addFunctionChanged(e -> {
            FunctionInfo functionInfo = Transformer.toFunctionInfo(e.getData());
            FunctionInfo oldFunctionInfo = functionStore.get(functionInfo.getResourceCode());
            if(oldFunctionInfo != null) {
                functionInfo.setAllowRoles(oldFunctionInfo.getAllowRoles());
            }
            functionStore.put(functionInfo.getResourceCode(), functionInfo);
        });
    }

    // #region IFunctionManager methods

    @Override
    public List<FunctionInfo> queryFunctions(String status, String... resourceTypes) {
        if (functionStore != null) {
            List<String> mySequence = this.sequence;
            if (mySequence != null) {
                List<FunctionInfo> records = new ArrayList<>(mySequence.size());
                for (String code : mySequence) {
                    FunctionInfo functionInfo = functionStore.get(code);
                    if (functionInfo != null) {
                        records.add(functionInfo);
                    }
                }
                // 检查是否全部命中
                if (records.size() == mySequence.size()) {
                    return records;
                }
            }
        }

        Map<String, Object> params = MapUtils.createHashMap(1);
        params.put("status", FunctionStatusEnum.ENABLED.Value);
        params.put("typeList", CollectionUtils.createArrayList(resourceTypes));
        
        List<SysMenu> menus = getAllMenus(params);

        List<FunctionInfo> records = new ArrayList<>(menus.size());
        if (functionStore != null && !CollectionUtils.isEmpty(menus)) {
            Map<String, FunctionInfo> cacheValue = MapUtils.createHashMap(menus.size());
            List<String> sequence = new ArrayList<>(menus.size());
            menus.forEach(i -> {
                FunctionInfo functionInfo = Transformer.toFunctionInfo(i);
                sequence.add(functionInfo.getResourceCode());
                records.add(functionInfo);
                cacheValue.put(functionInfo.getResourceCode(), functionInfo);
            });
            functionStore.putAll(cacheValue);
            synchronized (locker) {
                this.sequence = sequence;
            }
        }

        return records;
    }

    @Override
    public List<MenuInfo> getMenus(UserInfo user) {
        if (user != null) {
            Collection<RoleInfo> authorities = user.getRoles();
            if (!CollectionUtils.isEmpty(authorities)) {
                Set<String> userRoles = new HashSet<>();
                authorities.forEach(i -> userRoles.add(i.getRoleCode()));

                List<MenuInfo> menus = getEnabledMenus();
                List<MenuInfo> userMenus = new ArrayList<>(menus.size());
                for (MenuInfo menu : menus) {
                    List<String> roles = menu.getAllowRoles();
                    for (String roleCode : roles) {
                        if (userRoles.contains(roleCode)) {
                            userMenus.add(menu);
                            break;
                        }
                    }
                }
                return userMenus;
            }
        }
        return null;
    }

    @Override
    public List<MenuInfo> queryMenus(Map<String, Object> params) {
        List<SysMenu> menuList = getAllMenus(params);
        return menuList.stream().map(m -> Transformer.toMenuInfo(m)).toList();
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    public boolean createMenu(MenuInfo menu) {
        Guard.notNull(menu, "the parameter [menu] is required.");
        SysMenu sysMenu = Transformer.toSysMenu(menu);
        return createMenu(sysMenu);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    public boolean updateMenu(MenuInfo menu) {
        Guard.notNull(menu, "the parameter [menu] is required.");
        SysMenu sysMenu = Transformer.toSysMenu(menu);
        return updateMenu(sysMenu);
    }

    // #endregion

    public List<SysMenu> getAllMenus(Map<String, Object> params) {
        List<SysMenu> menus = menuDAO.selectMenu(params);

        if (menus == null) {
            return new ArrayList<>(0);
        }
        List<UUID> functionIdList = new ArrayList<>(menus.size());
        menus.forEach(i -> {
            functionIdList.add(i.getFunctionId());
        });

        Map<UUID, Set<Object>> functionRoleMap = permissionDAO.selectByFunctions(functionIdList, 1);
        if (functionRoleMap != null) {
            menus.forEach(i -> {
                Set<Object> roleSet = functionRoleMap.get(i.getFunctionId());
                if (roleSet != null) {
                    for (Object item : roleSet) {
                        i.addRole((AuthRole) item);
                    }
                }
            });
        }
        return menus;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    public boolean createMenu(SysMenu menu) {
        Guard.notNull(menu, "the SysMenu is required.");
        if(menu.getFunctionId() == null) {
            menu.setFunctionId(UUID.randomUUID());
        }

        int effectRows = 0;

        effectRows += functionDAO.insert(menu);
        effectRows += updateRoles(menu, false);
        
        if(StringUtils.equals(menu.getType(), ResourceType.MENU)) {
            effectRows += menuDAO.insert(menu);
        }

        boolean result = effectRows > 0;
        if (result) {
            onFunctionChanged(menu);
        }
        return result;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    public boolean updateMenu(SysMenu menu) {
        Guard.notNull(menu, "the SysMenu is required.");
        Guard.notNull(menu.getFunctionId(), "the SysMenu.functionId is required.");

        int effectRows = 0;

        effectRows += functionDAO.update(menu);
        effectRows += updateRoles(menu, true);

        boolean result = effectRows > 0;
        if (result) {
            onFunctionChanged(menu);
        }
        return result;
    }

    public void updateFunctionStore(String roleId, List<String> functionIdList) {
        Guard.notEmpty(roleId, "the parameter roleId is required.");
        Guard.notEmpty(functionIdList, "the parameter functionIdList can not be empty.");

        Set<UUID> functionIdSet = new HashSet<>();
        functionIdList.forEach(i -> functionIdSet.add(UUID.fromString(i)));

        // 加载新的菜单数据
        Map<UUID, Set<Object>> functionRoleMap = 
            permissionDAO.selectByFunctions(functionIdSet, FunctionStatusEnum.ENABLED.Value);
        
        synchronized (locker) {
            sequence.forEach(functionId -> {
                FunctionInfo functionInfo = functionStore.get(functionId);
                if (functionInfo == null) {
                    return;
                }
                UUID functionGuid = UUID.fromString(functionId);
                if (!functionIdSet.contains(functionGuid)) {
                    // 移除取消的菜单权限
                    List<String> roles = functionInfo.getAllowRoles();
                    if (roles != null) {
                        List<String> newRoles = new ArrayList<>(roles.size());
                        for (String role : roles) {
                            if (!role.equals(roleId)) {
                                newRoles.add(role);
                            }
                        }
                        functionInfo.setAllowRoles(newRoles);
                    }
                } else {
                    // 更新菜单的可用角色列表
                    List<String> newRoles = new ArrayList<>();
                    Set<Object> roleSet = functionRoleMap.get(functionGuid);
                    if (roleSet != null) {
                        for (Object item : roleSet) {
                            newRoles.add(item.toString());
                        }
                    }
                    functionInfo.setAllowRoles(newRoles);
                }
            });
        }
    }

    private int updateRoles(SysMenu menu, boolean isUpdate) {
        int effectRows = 0;
        Collection<AuthRole> roles = menu.getRoles();
        if (!CollectionUtils.isEmpty(roles)) {
            if(isUpdate) {
                permissionDAO.deleteByFunctionId(menu.getFunctionId());
            }
            for (AuthRole role : roles) {
                AuthPermission roleIdFunctionId = new AuthPermission();
                roleIdFunctionId.setRoleId(role.getRoleId());
                roleIdFunctionId.setFunctionId(menu.getFunctionId());
                effectRows += permissionDAO.insert(roleIdFunctionId);
            }
        }
        return effectRows;
    }

    // #region 事件

    @Override
    public void addFunctionChanged(Consumer<FunctionChangedEvent<SysMenu>> eventHandler) {
        functionChangedDelegate.on(eventHandler);
    }

    @Override
    public void removeFunctionChanged(Consumer<FunctionChangedEvent<SysMenu>> eventHandler) {
        functionChangedDelegate.off(eventHandler);
    }

    protected void onFunctionChanged(SysMenu menu) {
        TransactionHelper.executeAfterCommit(() -> {
            functionChangedDelegate.trigger(new FunctionChangedEvent<SysMenu>(menu));
        });
    }

    //#endregion

}