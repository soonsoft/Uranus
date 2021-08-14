package com.soonsoft.uranus.security.authorization;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.soonsoft.uranus.security.entity.FunctionInfo;
import com.soonsoft.uranus.security.entity.RoleInfo;
import com.soonsoft.uranus.core.common.collection.MapUtils;
import com.soonsoft.uranus.core.common.lang.StringUtils;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * 权限起源加载器
 */
public class WebSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private FilterInvocationSecurityMetadataSource defaultSecurityMetadataSource;

    private Map<RequestMatcher, Collection<ConfigAttribute>> requestMap;

    private Collection<? extends FunctionInfo> functionCollection;

    /**
     * @param configAttributeCollection the configAttributeCollection to set
     */
    public void setConfigAttributeCollection(Collection<? extends FunctionInfo> functionCollection) {
        if(functionCollection == null || functionCollection.isEmpty()) {
            return;
        }
        this.functionCollection = functionCollection;
        Map<String, Collection<ConfigAttribute>> menuMap = new HashMap<>(this.functionCollection.size(), 1);

        for(FunctionInfo function : this.functionCollection) {
            String url = function.getResourceUrl();
            if(!StringUtils.isEmpty(url)) {
                Collection<ConfigAttribute> attributes = menuMap.get(url);
                if(attributes == null) {
                    attributes = new HashSet<>();
                    menuMap.put(url, attributes);
                }
                List<RoleInfo> list = function.getAllowRoles();
                if(list != null) {
                    attributes.addAll(list);
                }
            }
        }

        final Map<RequestMatcher, Collection<ConfigAttribute>> myRequestMap = MapUtils.createHashMap(menuMap.size());
        menuMap.forEach((key, value) -> myRequestMap.put(new MvcRequestMatcher(null, key), value));
        this.requestMap = myRequestMap;
    }

    /**
     * 初始化时加载权限资源
     */
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Set<ConfigAttribute> allAttributes = new HashSet<>();
        
        for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry 
                : requestMap.entrySet()) {
			allAttributes.addAll(entry.getValue());
		}

		return allAttributes;
    }

    /**
     * 根据当前请求获取相关的权限信息
     * 如果返回null，则表示该URL没有需要验证的权限，是一个可以公开访问的资源
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {        
        if(requestMap != null) {
            final HttpServletRequest request = ((FilterInvocation) object).getRequest();
            for (Map.Entry<RequestMatcher, Collection<ConfigAttribute>> entry : requestMap
                    .entrySet()) {
                if (entry.getKey().matches(request)) {
                    return entry.getValue();
                }
            }
        }
        return getDefaultSecurityMetadataSource() != null 
                    ? getDefaultSecurityMetadataSource().getAttributes(object) : null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    //#region getter and setter

    public FilterInvocationSecurityMetadataSource getDefaultSecurityMetadataSource() {
        return defaultSecurityMetadataSource;
    }

    public void setDefaultSecurityMetadataSource(FilterInvocationSecurityMetadataSource defaultSecurityMetadataSource) {
        this.defaultSecurityMetadataSource = defaultSecurityMetadataSource;
    }

    //#endregion
    
}