package com.soonsoft.uranus.security.authorization.voter;

import java.util.Collection;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

public interface IVoter {
    /**
     * 权限投票（检查）
     * @param authentication 当前登录的角色信息
     * @param object 访问的资源对象
     * @param attributes 资源对象所对应的访问权限规则
     * @return true - 有权限，false - 无权限
     */
    boolean vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes);
}
