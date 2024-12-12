package com.soonsoft.uranus.services.membership.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.soonsoft.uranus.core.error.BusinessException;
import com.soonsoft.uranus.core.model.data.IPagingList;
import com.soonsoft.uranus.core.model.data.Page;
import com.soonsoft.uranus.core.model.data.PagingList;
import com.soonsoft.uranus.security.authentication.IUserManager;
import com.soonsoft.uranus.security.entity.PasswordInfo;
import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.services.membership.constant.PasswordStatusEnum;
import com.soonsoft.uranus.services.membership.constant.UserStatusEnum;
import com.soonsoft.uranus.services.membership.dao.AuthPasswordDAO;
import com.soonsoft.uranus.services.membership.dao.AuthPrivilegeDAO;
import com.soonsoft.uranus.services.membership.dao.AuthUserDAO;
import com.soonsoft.uranus.services.membership.dao.AuthUserRoleRelationDAO;
import com.soonsoft.uranus.services.membership.model.Transformer;
import com.soonsoft.uranus.services.membership.po.AuthPassword;
import com.soonsoft.uranus.services.membership.po.AuthPrivilege;
import com.soonsoft.uranus.services.membership.po.AuthRole;
import com.soonsoft.uranus.services.membership.po.AuthUser;
import com.soonsoft.uranus.services.membership.po.AuthUserRoleRelation;
import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.collection.CollectionUtils;
import com.soonsoft.uranus.core.common.collection.MapUtils;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


public class UserService implements IUserManager {

    private AuthUserDAO userDAO;

    private AuthPasswordDAO passwordDAO;

    private PasswordEncoder passwordEncoder;

    private AuthUserRoleRelationDAO userRoleRelationDAO;

    private AuthPrivilegeDAO privilegeDAO;

    public UserService(
            AuthUserDAO userDAO, 
            AuthPasswordDAO passwordDAO, 
            AuthUserRoleRelationDAO userRoleRelationDAO, 
            AuthPrivilegeDAO privilegeDAO) {

        this.userDAO = userDAO;
        this.passwordDAO = passwordDAO;
        this.userRoleRelationDAO = userRoleRelationDAO;
        this.privilegeDAO = privilegeDAO;

    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    //#region IUserManager methods

    @Override
    public IPagingList<UserInfo> queryUsers(Map<String, Object> params, int pageIndex, int pageSize) {
        Page page = new Page();
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);

        List<AuthUser> userList = queryUsers(params, page);
        List<UserInfo> userInfoList = 
            userList.stream()
                .map(u -> Transformer.toUserInfo(u))
                .toList();
        
        PagingList<UserInfo> result = new PagingList<>(userInfoList, page.getTotal());
        result.setPageIndex(page.getPageIndex());
        result.setPageSize(page.getPageSize());

        return result;
    }

    @Override
    public UserInfo getUser(String username) {
        AuthUser authUser = userDAO.getUser(username);
        if(authUser == null) {
            throw new NullPointerException("the user is null.");
        }

        AuthPassword password = passwordDAO.getEnabledPassword(authUser.getUserId());
        if(password == null) {
            throw new NullPointerException("the password is null.");
        }

        List<AuthRole> roles = userRoleRelationDAO.selectByUserId(authUser.getUserId());
        List<AuthPrivilege> privileges = privilegeDAO.selectUserPrivileges(authUser.getUserId());

        return Transformer.toUserInfo(authUser, password, roles, privileges);
    }

    @Override
    public UserInfo getUserByCellPhone(String areaCode, String cellPhone) {
        AuthUser authUser = userDAO.getUserByCellPhone(cellPhone);
        if(authUser != null) {
            return getUser(authUser.getUserName());
        }
        return null;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    public boolean createUser(UserInfo userInfo) {
        Guard.notNull(userInfo, "the parameter [userInfo] is required.");
        
        AuthUser user = Transformer.toAuthUser(userInfo);
        return create(user, userInfo.getPasswordInfo().getPassword());
    }

    @Override
    public boolean updateUser(UserInfo userInfo) {
        Guard.notNull(userInfo, "the parameter [userInfo] is required.");
        
        AuthUser user = Transformer.toAuthUser(userInfo);
        return update(user);
    }

    @Override
    public String encryptPassword(String password, String salt) {
        Guard.notEmpty(password, "the password is required.");

        String encodePassword = encodePassword(password, salt);
        if(passwordEncoder != null) {
            encodePassword = passwordEncoder.encode(encodePassword);
        }
        return encodePassword;
    }

    @Override
    public boolean checkPassword(String password, UserInfo userInfo) {
        if(userInfo != null && userInfo.getPasswordInfo() != null && password != null) {
            String passwordEncode = encodePassword(password, userInfo.getPasswordInfo().getPasswordSalt());
            return getPasswordEncoder().matches(passwordEncode, userInfo.getPasswordInfo().getPassword());
        }
        return false;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    public boolean deleteUser(UserInfo user) {
        Guard.notNull(user, "the user is required.");
        Guard.notEmpty(user.getUserId(), "the user.userId is required.");

        return delete(UUID.fromString(user.getUserId()));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    public boolean deleteUser(String username) {
        Guard.notEmpty(username, "the username is required.");

        AuthUser user = userDAO.getUser(username);
        if(user == null) {
            throw new IllegalStateException("the username [" + username + "] is not exists.");
        }

        return delete(user.getUserId());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    public boolean disableUser(UserInfo user) {
        Guard.notNull(user, "the user is required.");
        Guard.notEmpty(user.getUserId(), "the user.userId is required.");

        AuthUser authUser = new AuthUser();
        authUser.setUserId(UUID.fromString(user.getUserId()));
        authUser.setStatus(UserStatusEnum.DISABLED.Value);

        return update(authUser);
    }

    @Override
    public void resetPassword(UserInfo user) {

    }

    @Override
    public PasswordInfo getEnabledPassword(String userId) {
        AuthPassword password = passwordDAO.getEnabledPassword(UUID.fromString(userId));
        PasswordInfo passwordInfo = Transformer.toPasswordInfo(password);
        return passwordInfo;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    public void changeMyPassword(UserInfo user) {
        Guard.notNull(user, "the user is required.");

        AuthUser authUser = userDAO.getUser(user.getUserName());
        if(authUser == null) {
            throw new BusinessException("can not find AuthUser by username: " + user.getUserName());
        }

        PasswordInfo passwordInfo = user.getPasswordInfo();

        AuthPassword password = new AuthPassword();
        password.setUserId(authUser.getUserId());
        password.setPasswordValue(encryptPassword(passwordInfo.getPassword(), passwordInfo.getPasswordSalt()));
        password.setPasswordSalt(passwordInfo.getPasswordSalt());
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

        List<AuthUser> users = userDAO.selectUser(params, page);
        if(!CollectionUtils.isEmpty(users)) {
            List<UUID> userIdList = new ArrayList<>(users.size());
            for(AuthUser user : users) {
                userIdList.add(user.getUserId());
            }

            // 加载用户关联的角色信息
            Map<UUID, Set<Object>> roleMap = userRoleRelationDAO.selectByUsers(userIdList, null);
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

            // 加载用户关联的功能信息（用户特权）
            List<AuthPrivilege> userPrivileges = privilegeDAO.selectMutilUserPrivileges(userIdList);
            if(!CollectionUtils.isEmpty(userPrivileges)) {
                Map<UUID, Set<AuthPrivilege>> value = MapUtils.createHashMap(32);
                userPrivileges.forEach(p -> {
                    Set<AuthPrivilege> privileges = value.get(p.getUserId());
                    if(privileges == null) {
                        privileges = new HashSet<>();
                        value.put(p.getUserId(), privileges);
                    }
                    privileges.add(p);
                });
                users.forEach(u -> {
                    Set<AuthPrivilege> privileges = value.get(u.getUserId());
                    List<Object> functions = new ArrayList<>();
                    if(!CollectionUtils.isEmpty(privileges)) {
                        functions.addAll(privileges);
                    }
                    u.setFunctions(functions);
                });
            }
        }

        return users;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    public boolean create(AuthUser authUser, String originalPassword) {
        Guard.notNull("authUser", "the authUser is required.");
        Guard.notNull("authPassword", "the authPassword is required.");

        if(authUser.getUserId() == null) {
            authUser.setUserId(UUID.randomUUID());
        }
        if(authUser.getCreateTime() == null) {
            authUser.setCreateTime(new Date());
        }

        AuthPassword authPassword = new AuthPassword();
        authPassword.setUserId(authUser.getUserId());
        authPassword.setPasswordSalt(createSalt());
        authPassword.setPasswordValue(encryptPassword(originalPassword, authPassword.getPasswordSalt()));
        // 无修改时间
        authPassword.setPasswordChangedTime(null);
        authPassword.setStatus(PasswordStatusEnum.ENABLED.Value);
        // 无过期时间
        authPassword.setExpiredTime(null);
        authPassword.setCreateTime(authUser.getCreateTime());

        int effectRows = 0;
        effectRows += userDAO.insert(authUser);
        effectRows += passwordDAO.insert(authPassword);

        List<Object> roles = authUser.getRoles();
        if(!CollectionUtils.isEmpty(roles)) {
            userRoleRelationDAO.deleteByUserId(authUser.getUserId());
            for(Object roleId : roles) {
                AuthUserRoleRelation userIdAndRoleId = new AuthUserRoleRelation();
                userIdAndRoleId.setUserId(authUser.getUserId());
                userIdAndRoleId.setRoleId((UUID) roleId);
                effectRows += userRoleRelationDAO.insert(userIdAndRoleId);
            }
        }

        return effectRows > 0;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    public boolean update(AuthUser authUser) {
        Guard.notNull(authUser, "the authUser is required.");
        Guard.notNull(authUser.getUserId(), "the authUser.userId is required.");

        int effectRows = userDAO.update(authUser);
        return effectRows > 0;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    public boolean delete(UUID userId) {
        Guard.notNull(userId, "the userId is required.");

        int effectRows = 0;

        effectRows += passwordDAO.delete(userId);
        effectRows += userDAO.delete(userId);

        return effectRows > 0;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    public boolean updateUserPrivilege(UUID userId, List<UUID> functionIdList) {
        Guard.notNull(userId, "the userId is required.");
        Guard.notEmpty(functionIdList, "the functionIdList is required.");

        privilegeDAO.deletePrivilegeByUserId(userId);
        int effectRows = 0;
        for(UUID functionId : functionIdList) {
            AuthPrivilege privilege = new AuthPrivilege();
            privilege.setUserId(userId);
            privilege.setFunctionId(functionId);
            effectRows += privilegeDAO.insert(privilege);
        }

        return effectRows > 0;
    }

    protected String createSalt() {
        return UUID.randomUUID().toString();
    }

    protected String encodePassword(String password, String salt) {
        if(salt == null) {
            salt = "";
        }

        String encodePassword = salt + password + salt;
        return encodePassword;
    }

    
}