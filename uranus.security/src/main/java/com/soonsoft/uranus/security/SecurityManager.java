package com.soonsoft.uranus.security;

import com.soonsoft.uranus.security.authentication.IUserManager;
import com.soonsoft.uranus.security.authorization.IFunctionManager;
import com.soonsoft.uranus.security.authorization.IRoleManager;
import com.soonsoft.uranus.security.entity.AnonymousUser;
import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.security.profile.IUserProfile;
import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class SecurityManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityManager.class);

    private static final String ANONYMOUS_USER = "anonymousUser";

    private static final SecurityManager INSTANCE = new SecurityManager();

    private boolean initialzed = false;

    private IUserManager userManager;

    private IRoleManager roleManager;

    private IFunctionManager functionManager;

    private IUserProfile userProfile;

    /**
     * @return the userManager
     */
    public IUserManager getUserManager() {
        return userManager;
    }

    /**
     * @param userManager the userManager to set
     */
    private void setUserManager(IUserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * @return the roleManager
     */
    public IRoleManager getRoleManager() {
        return roleManager;
    }

    /**
     * @param roleManager the roleManager to set
     */
    private void setRoleManager(IRoleManager roleManager) {
        this.roleManager = roleManager;
    }

    /**
     * @return the functionManager
     */
    public IFunctionManager getFunctionManager() {
        return functionManager;
    }

    /**
     * @param functionManager the functionManager to set
     */
    private void setFunctionManager(IFunctionManager functionManager) {
        this.functionManager = functionManager;
    }

    /**
     * @return the userProfile
     */
    public IUserProfile getUserProfile() {
        return userProfile;
    }

    /**
     * @param userProfile the userProfile to set
     */
    public void setUserProfile(IUserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public UserInfo getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) {
            return new AnonymousUser(ANONYMOUS_USER);
        }
        Object user = authentication.getPrincipal();
        if(user instanceof UserInfo) {
            return (UserInfo) user;
        } else {
            String username = (String) user;
            if(StringUtils.equals(username, ANONYMOUS_USER)) {
                return new AnonymousUser(username);
            } else {
                return new UserInfo(username);
            }
        }
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return isAuthenticated(authentication);
    }

    public boolean isAuthenticated(Authentication authentication) {
        return authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }

    public static SecurityManager current() {
        return INSTANCE;
    }

    public static boolean isAnonymousUser(UserInfo user) {
        return user instanceof AnonymousUser;
    }

    /**
     * 初始化SecurityManager的实例
     * 注意：只能执行一次
     * @param applicationContext
     */
    public static void init(ApplicationContext applicationContext) {
        Guard.notNull(applicationContext, "the ApplicationContext is required.");
        Guard.isTrue(!INSTANCE.initialzed, "the instance of SecurityManager is initialzed.");

        synchronized(INSTANCE) {
            INSTANCE.setUserManager(applicationContext.getBean(IUserManager.class));
            INSTANCE.setRoleManager(applicationContext.getBean(IRoleManager.class));
            INSTANCE.setFunctionManager(applicationContext.getBean(IFunctionManager.class));

            String userProfileBeanName = IUserProfile.class.getSimpleName();
            if(applicationContext.containsBean(userProfileBeanName)) {
                INSTANCE.setUserProfile((IUserProfile) applicationContext.getBean(userProfileBeanName));
            }

            INSTANCE.initialzed = true;
        }
    }

}