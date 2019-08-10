package com.soonsoft.uranus.security.authentication;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * WebUserDetailsService
 */
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
        return userManager.getUser(username);
    }
    
}