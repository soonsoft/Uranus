package com.soonsoft.uranus.security.authentication;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.soonsoft.uranus.security.entity.PasswordInfo;
import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.security.entity.security.SecurityUser;

@Deprecated
public class WebUserDetailsService implements UserDetailsService {

    private IUserManager userManager;

    /**
     * @return the userManager
     */
    public IUserManager getUserManager() {
        return userManager;
    }

    /**
     * @param userManager the userManager to set
     */
    public void setUserManager(IUserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userInfo = userManager.getUser(username);
        if(userInfo.getPasswordInfo() == null) {
            PasswordInfo passwordInfo = userManager.getEnabledPassword(userInfo.getUserId());
            userInfo.setPasswordInfo(passwordInfo);
        }

        SecurityUser user = new SecurityUser(userInfo);
        return user;
    }
    
}