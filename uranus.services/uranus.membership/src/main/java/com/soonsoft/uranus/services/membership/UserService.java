package com.soonsoft.uranus.services.membership;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.soonsoft.uranus.core.error.BusinessException;
import com.soonsoft.uranus.data.entity.Page;
import com.soonsoft.uranus.security.authentication.IUserManager;
import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.services.membership.dao.AuthPasswordDAO;
import com.soonsoft.uranus.services.membership.dao.AuthUserDAO;
import com.soonsoft.uranus.services.membership.dao.AuthUsersInRolesDAO;
import com.soonsoft.uranus.services.membership.dto.AuthPassword;
import com.soonsoft.uranus.services.membership.dto.AuthRole;
import com.soonsoft.uranus.services.membership.dto.AuthUser;
import com.soonsoft.uranus.services.membership.dto.AuthUserIdAndRoleId;
import com.soonsoft.uranus.services.membership.model.Transformer;
import com.soonsoft.uranus.util.Guard;
import com.soonsoft.uranus.util.collection.CollectionUtils;
import com.soonsoft.uranus.util.collection.MapUtils;
import com.soonsoft.uranus.util.lang.StringUtils;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserService
 */
public class UserService implements IUserManager {

    private AuthUserDAO userDAO;

    private AuthPasswordDAO passwordDAO;

    private PasswordEncoder passwordEncoder;

    private AuthUsersInRolesDAO usersInRolesDAO;

    //#region IUserManager methods

    @Override
    public UserInfo getUser(String username) {
        AuthUser authUser = userDAO.getUser(username);
        if(authUser == null) {
            throw new NullPointerException("the user is null.");
        }

        AuthPassword password = passwordDAO.getUserPassword(authUser.getUserId());
        if(password == null) {
            throw new NullPointerException("the password is null.");
        }

        List<AuthRole> roles = usersInRolesDAO.selectByUserId(authUser.getUserId());

        return Transformer.toUserInfo(authUser, password, roles);
    }

    @Override
    @Transactional
    public boolean createUser(UserInfo userInfo) {
        Guard.notNull(userInfo, "the UserInfo is required.");
        
        AuthUser user = new AuthUser();
        user.setUserName(userInfo.getUsername());
        user.setNickName(userInfo.getNickName());
        user.setCellPhone(userInfo.getCellPhone());
        user.setStatus(AuthUser.ENABLED);
        user.setCreateTime(new Date());

        Collection<GrantedAuthority> roles = userInfo.getAuthorities();
        if(!CollectionUtils.isEmpty(roles)) {
            List<Object> roleIdList = new ArrayList<>(roles.size());
            for(GrantedAuthority role : roles) {
                roleIdList.add(role.getAuthority());
            }
            user.setRoles(roleIdList);
        }

        AuthPassword password = new AuthPassword();
        password.setPasswordValue(userInfo.getPassword());
        password.setPasswordSalt(userInfo.getPasswordSalt());

        return create(user, password);
    }

    @Override
    public String encryptPassword(String password, String salt) {
        Guard.notEmpty(password, "the password is required.");
        
        if(salt == null) {
            salt = "";
        }

        String encodePassword = salt + password + salt;
        if(passwordEncoder != null) {
            encodePassword = passwordEncoder.encode(encodePassword);
        }
        return encodePassword;
    }

    @Override
    @Transactional
    public boolean deleteUser(UserInfo user) {
        Guard.notNull(user, "the user is required.");
        Guard.notEmpty(user.getUserId(), "the user.userId is required.");

        int affectRows = userDAO.delete(user.getUserId());
        return affectRows > 0;
    }

    @Override
    @Transactional
    public boolean deleteUser(String username) {
        Guard.notEmpty(username, "the username is required.");

        int affectRows = userDAO.deleteByUserName(username);
        return affectRows > 0;
    }

    @Override
    @Transactional
    public boolean disableUser(UserInfo user) {
        Guard.notNull(user, "the user is required.");
        Guard.notEmpty(user.getUserId(), "the user.userId is required.");

        AuthUser authUser = new AuthUser();
        authUser.setUserId(user.getUserId());
        authUser.setStatus(AuthUser.DISABLED);

        return update(authUser);
    }

    @Override
    public void resetPassword(UserInfo user) {

    }

    @Override
    @Transactional
    public void changeMyPassword(UserInfo user) {
        Guard.notNull(user, "the user is required.");

        AuthUser authUser = userDAO.getUser(user.getUsername());
        if(authUser == null) {
            throw new BusinessException("can not find AuthUser by username: " + user.getUsername());
        }

        AuthPassword password = new AuthPassword();
        password.setUserId(authUser.getUserId());
        password.setPasswordValue(encryptPassword(user.getPassword(), user.getPasswordSalt()));
        password.setPasswordSalt(user.getPasswordSalt());
        password.setPasswordChangedTime(new Date());

        passwordDAO.update(password);
    }

    @Override
    public void findMyPassword(UserInfo user) {

    }
    
    //#endregion

    public List<AuthUser> queryUsers(Map<String, Object> params, Page page) {
        if(page == null) {
            page = new Page();
        }

        List<AuthUser> users = userDAO.select(params, page);
        if(!CollectionUtils.isEmpty(users)) {
            List<String> userIdList = new ArrayList<>(users.size());
            for(AuthUser user : users) {
                userIdList.add(user.getUserId());
            }
            Map<String, Set<Object>> roleMap = usersInRolesDAO.selectByUsers(userIdList, null);
            if(!MapUtils.isEmpty(roleMap)) {
                users.forEach(user -> {
                    List<Object> idList = new ArrayList<>();
                    Set<Object> roles = roleMap.get(user.getUserId());
                    if(!CollectionUtils.isEmpty(roles)) {
                        idList.addAll(roles);
                    }
                    user.setRoles(idList);
                });
            }
        }

        return users;
    }

    @Transactional
    public boolean create(AuthUser authUser, AuthPassword authPassword) {
        Guard.notNull("authUser", "the authUser is required.");
        Guard.notNull("authPassword", "the authPassword is required.");

        if(StringUtils.isEmpty(authUser.getUserId())) {
            authUser.setUserId(UUID.randomUUID().toString());
        }
        if(authUser.getCreateTime() == null) {
            authUser.setCreateTime(new Date());
        }

        authPassword.setUserId(authUser.getUserId());
        authPassword.setPasswordChangedTime(null);
        authPassword.setCreateTime(authUser.getCreateTime());

        int effectRows = 0;
        effectRows += userDAO.insert(authUser);
        effectRows += passwordDAO.insert(authPassword);

        List<Object> roles = authUser.getRoles();
        if(!CollectionUtils.isEmpty(roles)) {
            usersInRolesDAO.deleteByUserId(authUser.getUserId());
            for(Object roleId : roles) {
                AuthUserIdAndRoleId userIdAndRoleId = new AuthUserIdAndRoleId();
                userIdAndRoleId.setUserId(authUser.getUserId());
                userIdAndRoleId.setRoleId((String) roleId);
                effectRows += usersInRolesDAO.insert(userIdAndRoleId);
            }
        }

        return effectRows > 0;
    }

    @Transactional
    public boolean update(AuthUser authUser) {
        Guard.notNull(authUser, "the authUser is required.");
        Guard.notEmpty(authUser.getUserId(), "the authUser.userId is required.");

        int effectRows = userDAO.update(authUser);
        return effectRows > 0;
    }

    //#region getter and setter

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public AuthUserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(AuthUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public AuthPasswordDAO getPasswordDAO() {
        return passwordDAO;
    }

    public void setPasswordDAO(AuthPasswordDAO passwordDAO) {
        this.passwordDAO = passwordDAO;
    }

    public AuthUsersInRolesDAO getUsersInRolesDAO() {
        return usersInRolesDAO;
    }

    public void setUsersInRolesDAO(AuthUsersInRolesDAO usersInRolesDAO) {
        this.usersInRolesDAO = usersInRolesDAO;
    }

    //#endregion
}