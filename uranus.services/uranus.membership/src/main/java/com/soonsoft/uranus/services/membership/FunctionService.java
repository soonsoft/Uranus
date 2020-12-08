package com.soonsoft.uranus.services.membership;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.event.IEventListener;
import com.soonsoft.uranus.core.common.event.SimpleEventListener;
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
import com.soonsoft.uranus.services.membership.dao.AuthRolesInFunctionsDAO;
import com.soonsoft.uranus.services.membership.dao.SysFunctionDAO;
import com.soonsoft.uranus.services.membership.dao.SysMenuDAO;
import com.soonsoft.uranus.services.membership.dto.AuthRole;
import com.soonsoft.uranus.services.membership.dto.AuthRoleIdAndFunctionId;
import com.soonsoft.uranus.services.membership.dto.SysMenu;
import com.soonsoft.uranus.services.membership.model.Transformer;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * FunctionService
 */
public class FunctionService implements IFunctionManager, IFunctionChangedListener<SysMenu> {

    private SysFunctionDAO functionDAO;

    private SysMenuDAO menuDAO;

    private AuthRolesInFunctionsDAO rolesInFunctionsDAO;

    private Cache<String, FunctionInfo> functionStore;

    private List<String> sequence;

    private static final Object locker = new Object();

    /** 事件定义 */
    private IEventListener<FunctionChangedEvent<SysMenu>> functionChangedDelegate = new SimpleEventListener<>();

    public FunctionService() {
        this(null);
    }

    public FunctionService(Cache<String, FunctionInfo> functionStore) {
        if (functionStore != null) {
            this.functionStore = functionStore;
        } else {
            this.functionStore = new Cache<>(128);
        }

        // 注册缓存更新
        addFunctionChanged(e -> {
            FunctionInfo functionInfo = Transformer.toFunctionInfo(e.getData());
            FunctionInfo oldFunctionInfo = functionStore.get(functionInfo.getResourceCode());
            functionInfo.setAllowRoles(oldFunctionInfo.getAllowRoles());
            functionStore.put(functionInfo.getResourceCode(), functionInfo);
        });
    }

    public SysFunctionDAO getFunctionDAO() {
        return functionDAO;
    }

    public void setFunctionDAO(SysFunctionDAO functionDAO) {
        this.functionDAO = functionDAO;
    }

    public SysMenuDAO getMenuDAO() {
        return menuDAO;
    }

    public void setMenuDAO(SysMenuDAO menuDAO) {
        this.menuDAO = menuDAO;
    }

    public AuthRolesInFunctionsDAO getRolesInFunctionsDAO() {
        return rolesInFunctionsDAO;
    }

    public void setRolesInFunctionsDAO(AuthRolesInFunctionsDAO rolesInFunctionsDAO) {
        this.rolesInFunctionsDAO = rolesInFunctionsDAO;
    }

    // #region IFunctionManager methods

    @Override
    public List<? extends FunctionInfo> getEnabledFunctions() {
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
        params.put("status", SysMenu.STATUS_ENABLED);
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
            Collection<GrantedAuthority> authorities = user.getAuthorities();
            if (!CollectionUtils.isEmpty(authorities)) {
                Set<String> userRoles = new HashSet<>();
                authorities.forEach(i -> userRoles.add(i.getAuthority()));

                List<MenuInfo> menus = getEnabledMenus();
                List<MenuInfo> userMenus = new ArrayList<>(menus.size());
                for (MenuInfo menu : menus) {
                    List<RoleInfo> roles = menu.getAllowRoles();
                    for (RoleInfo role : roles) {
                        if (userRoles.contains(role.getAuthority())) {
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

    // #endregion

    public List<SysMenu> getAllMenus(Map<String, Object> params) {
        List<SysMenu> menus = menuDAO.select(params);

        if (menus == null) {
            return new ArrayList<>(0);
        }
        List<String> functionIdList = new ArrayList<>(menus.size());
        menus.forEach(i -> {
            functionIdList.add(i.getFunctionId());
        });

        Map<String, Set<Object>> functionRoleMap = rolesInFunctionsDAO.selectByFunctions(functionIdList, 1);
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

    // TODO 创建菜单
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    public boolean createMenu(SysMenu menu) {
        return false;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    public boolean updateMenu(SysMenu menu) {
        Guard.notNull(menu, "the SysMenu is required.");
        Guard.notEmpty(menu.getFunctionId(), "the SysMenu.functionId is required.");

        int effectRows = 0;

        Collection<AuthRole> roles = menu.getRoles();
        if (!CollectionUtils.isEmpty(roles)) {
            rolesInFunctionsDAO.deleteByFunctionId(menu.getFunctionId());
            for (AuthRole role : roles) {
                AuthRoleIdAndFunctionId roleIdFunctionId = new AuthRoleIdAndFunctionId();
                roleIdFunctionId.setRoleId(role.getRoleId());
                roleIdFunctionId.setFunctionId(menu.getFunctionId());
                effectRows += rolesInFunctionsDAO.insert(roleIdFunctionId);
            }
        }

        effectRows += functionDAO.update(menu);

        boolean result = effectRows > 0;
        if (result) {
            onFunctionChanged(menu);
        }
        return result;
    }

    public void updateMenuStore(String roleId, List<String> functionIdList) {
        // 加载新的菜单数据
        Map<String, Set<Object>> functionRoleMap = rolesInFunctionsDAO.selectByFunctions(functionIdList,
                SysMenu.STATUS_ENABLED);

        Set<String> functionIdSet = new HashSet<>();
        functionIdSet.addAll(functionIdList);

        synchronized (locker) {
            sequence.forEach(functionId -> {
                MenuInfo menuInfo = getMenuInfoFromCache(functionId);
                if (menuInfo == null) {
                    return;
                }
                if (!functionIdSet.contains(functionId)) {
                    // 移除取消的菜单权限
                    List<RoleInfo> roles = menuInfo.getAllowRoles();
                    if (roles != null) {
                        List<RoleInfo> newRoles = new ArrayList<>(roles.size());
                        for (RoleInfo role : roles) {
                            if (!role.getRole().equals(roleId)) {
                                newRoles.add(role);
                            }
                        }
                        menuInfo.setAllowRoles(newRoles);
                    }
                } else {
                    // 更新菜单的可用角色列表
                    List<RoleInfo> newRoles = new ArrayList<>();
                    Set<Object> roleSet = functionRoleMap.get(functionId);
                    if (roleSet != null) {
                        for (Object item : roleSet) {
                            newRoles.add(Transformer.toRoleInfo((AuthRole) item));
                        }
                    }
                    menuInfo.setAllowRoles(newRoles);
                }
            });
        }
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
        functionChangedDelegate.trigger(new FunctionChangedEvent<SysMenu>(menu));
    }

    //#endregion


    private MenuInfo getMenuInfoFromCache(String functionId) {
        FunctionInfo functionInfo = functionStore.get(functionId);
        if(functionInfo != null) {
            return functionInfo.isType(FunctionInfo.MENU_TYPE) ? (MenuInfo) functionInfo : null;
        }
        return null;
    }
}