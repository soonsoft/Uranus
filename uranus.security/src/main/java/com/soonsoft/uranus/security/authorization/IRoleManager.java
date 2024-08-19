package com.soonsoft.uranus.security.authorization;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.security.entity.RoleInfo;
import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.model.data.IPagingList;

import org.springframework.security.core.GrantedAuthority;

public interface IRoleManager {

    /**
     * 查询角色列表
     * @param params 查询参数
     * @param pageIndex 第几页
     * @param pageSize 每页几行
     * @return
     */
    IPagingList<RoleInfo> queryRoles(Map<String, Object> params, int pageIndex, int pageSize);

    /**
     * 创建角色
     * @param role 角色信息
     * @return
     */
    boolean createRole(RoleInfo role);

    /**
     * 更新角色
     * @param role 角色信息
     * @return
     */
    boolean updateRole(RoleInfo role);

    /**
     * 删除角色
     * @param roleCode 角色编码
     * @return
     */
    boolean deleteRole(String roleCode);

    /***
     * 删除角色
     * @param role 角色信息
     * @return
     */
    default boolean deleteRole(RoleInfo role) {
        Guard.notNull(role, "the RoleInfo is required.");
        return deleteRole(role.getRoleCode());
    }

    /**
     * 获取用户绑定的角色列表
     * @param user 用户信息
     * @return 角色列表 List<RoleInfo>
     */
    Collection<GrantedAuthority> getUserRoles(UserInfo user);
    
    /**
     * 获取功能信息对应的Roles
     * @param resourceCodes 功能编号
     * @return key: 功能编号, Set<String>: Roles
     */
    Map<String, List<RoleInfo>> getFunctionRoles(Collection<String> resourceCodes);

}