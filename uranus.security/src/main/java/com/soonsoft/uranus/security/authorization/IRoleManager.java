package com.soonsoft.uranus.security.authorization;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.security.entity.RoleInfo;
import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.util.Guard;

import org.springframework.security.core.GrantedAuthority;

/**
 * IRoleManager
 */
public interface IRoleManager {

    boolean createRole(RoleInfo role);

    boolean updateRole(RoleInfo role);

    boolean deleteRole(String role);

    default boolean deleteRole(RoleInfo role) {
        Guard.notNull(role, "the RoleInfo is required.");
        return deleteRole(role.getRole());
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