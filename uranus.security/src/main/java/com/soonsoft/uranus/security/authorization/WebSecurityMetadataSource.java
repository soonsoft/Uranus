package com.soonsoft.uranus.security.authorization;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.soonsoft.uranus.security.entity.FunctionInfo;
import com.soonsoft.uranus.security.entity.security.SecurityPermission;
import com.soonsoft.uranus.security.entity.security.SecurityPrivilege;
import com.soonsoft.uranus.core.common.collection.MapUtils;
import com.soonsoft.uranus.core.common.lang.StringUtils;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * 权限元数据加载器
 */
public class WebSecurityMetadataSource implements SecurityMetadataSource {

    private IFunctionManager functionManager;

    public IFunctionManager getFunctionManager() {
        return functionManager;
    }

    public void setFunctionManager(IFunctionManager functionManager) {
        this.functionManager = functionManager;
    }

    /**
     * 获取该SecurityMetadataSource对象中保存的针对所有安全对象的权限信息的集合。
     * 该方法的主要目的是被AbstractSecurityInterceptor用于启动时校验每个ConfigAttribute对象。
     */
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    /**
     * 根据当前请求获取相关的权限信息
     * 如果返回null，则表示该URL没有需要验证的权限，是一个可以公开访问的资源
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        Map<RequestMatcher, Collection<ConfigAttribute>> requestMap = getConfigAttributeCollection();
        if(requestMap != null) {
            final HttpServletRequest request = (HttpServletRequest) object;
            for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry : requestMap
                    .entrySet()) {
                if (entry.getKey().matches(request)) {
                    return entry.getValue();
                }
            }
        }
        return  null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    public Map<RequestMatcher, Collection<ConfigAttribute>> getConfigAttributeCollection() {
        Collection<? extends FunctionInfo> functionCollection = functionManager.getEnabledFunctions();
        if(functionCollection == null || functionCollection.isEmpty()) {
            return null;
        } 
        Map<String, Collection<ConfigAttribute>> menuMap = new HashMap<>(functionCollection.size(), 1);

        for(FunctionInfo function : functionCollection) {
            String url = function.getResourceUrl();
            if(!StringUtils.isEmpty(url)) {
                Collection<ConfigAttribute> attributes = menuMap.get(url);
                if(attributes == null) {
                    attributes = new HashSet<>();
                    menuMap.put(url, attributes);
                }

                final String resourceCode = function.getResourceCode();
                final Collection<ConfigAttribute> attributeSet = attributes;
                // add Privilege
                List<String> users = function.getAllowUsers();
                if(users != null && !users.isEmpty()) {
                    users.forEach(uid -> attributeSet.add(new SecurityPrivilege(uid, resourceCode)));
                }

                // add Premission
                List<String> roles = function.getAllowRoles();
                if(roles != null && !roles.isEmpty()) {
                    roles.forEach(role -> attributeSet.add(new SecurityPermission(role, resourceCode)));
                }
            }
        }

        final Map<RequestMatcher, Collection<ConfigAttribute>> myRequestMap = MapUtils.createHashMap(menuMap.size());
        menuMap.forEach((key, value) -> myRequestMap.put(new MvcRequestMatcher(null, key), value));
        
        return myRequestMap;
    }

    
}