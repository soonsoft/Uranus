package com.soonsoft.uranus.api.interceptor;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.soonsoft.uranus.security.SecurityManager;
import com.soonsoft.uranus.security.entity.RoleInfo;
import com.soonsoft.uranus.security.entity.security.SecurityUser;
import com.soonsoft.uranus.core.common.collection.CollectionUtils;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.web.util.HttpRequestUtils;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 用户信息ViewModel处理拦截器
 */
public class UserInfoInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(
            @NonNull HttpServletRequest request, 
            @NonNull HttpServletResponse response, 
            @NonNull Object handler,
            @Nullable ModelAndView modelAndView) throws Exception {

        if(!StringUtils.equals(request.getMethod(), "GET")) {
            return;
        }
        if(HttpRequestUtils.isAjax(request)) {
            return;
        }

        SecurityUser user = SecurityManager.current().getCurrentUser();
        if(user != null) {
            request.setAttribute("userName", user.getNickName());
            Collection<RoleInfo> roles = user.getRoles();
            if(!CollectionUtils.isEmpty(roles)) {
                RoleInfo firstRole = roles.stream().findFirst().get();
                request.setAttribute("roleName", firstRole.getRoleName());
            }
        }
    }
    
}