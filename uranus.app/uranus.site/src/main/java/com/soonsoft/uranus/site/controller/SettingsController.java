package com.soonsoft.uranus.site.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.model.data.IPagingList;
import com.soonsoft.uranus.core.model.data.PagingList;
import com.soonsoft.uranus.security.SecurityManager;
import com.soonsoft.uranus.security.authentication.IUserManager;
import com.soonsoft.uranus.security.authorization.IFunctionManager;
import com.soonsoft.uranus.security.authorization.IRoleManager;
import com.soonsoft.uranus.security.entity.PrivilegeInfo;
import com.soonsoft.uranus.security.entity.RoleInfo;
import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.security.entity.StatusConst.ResourceType;
import com.soonsoft.uranus.services.membership.constant.RoleStatusEnum;
import com.soonsoft.uranus.services.membership.constant.UserStatusEnum;
import com.soonsoft.uranus.site.controller.base.BaseController;
import com.soonsoft.uranus.web.mvc.model.JsonResult;
import com.soonsoft.uranus.web.mvc.model.RequestData;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

/**
 * SettingController
 */
@Controller
public class SettingsController extends BaseController {

    private String defaultPassword = "1";

    private IUserManager getUserService() {
        return SecurityManager.current().getUserManager();
    }

    private IRoleManager getRoleService() {
        return SecurityManager.current().getRoleManager();
    }

    private IFunctionManager getFunctionService() {
        return SecurityManager.current().getFunctionManager();
    }

    //#region 用户管理

    //@PermitAll
    @RequestMapping(value = "/settings/users", method = RequestMethod.GET)
    public String users() {
        return "settings/users";
    }

    @RequestMapping(value = "/settings/users/query", method = RequestMethod.POST)
    public View queryUsers(@RequestBody RequestData parameter) {
        Map<String, Object> params = new HashMap<>();
        params.put("userName", parameter.get("userName"));
        params.put("status", parameter.get("status"));

        int pageIndex = parameter.getInteger("pageIndex", 1);
        int pageSize = parameter.getInteger("pageSize", 10);

        IPagingList<UserInfo> users = getUserService().queryUsers(params, pageIndex, pageSize);
        return json(JsonResult.create(users, users.getPageTotal()));
    }

    @RequestMapping(value = "/settings/users/save", method = RequestMethod.POST)
    public View saveUser(@RequestBody RequestData parameter) {
        UserInfo user = new UserInfo();
        user.setUserId(parameter.get("userId"));
        user.setUserName(parameter.get("userName"));
        user.setNickName(parameter.get("nickName"));
        user.setCellPhone(parameter.get("cellPhone"));
        user.setEmail(parameter.get("email"));
        user.setStatus(UserStatusEnum.valueOf(parameter.getInteger("status")).name());
        user.setCreateTime(parameter.getJsonDate("createTime"));


        List<Object> roles = parameter.getObject("roles");
        if(roles != null) {
            user.setRoles(roles.stream().map(i -> new RoleInfo((String) i, null)).collect(Collectors.toSet()));
        }

        List<Object> privileges = parameter.getObject("privileges");
        if(privileges != null) {
            user.setPrivileges(
                privileges.stream()
                    .map(i -> new PrivilegeInfo(user.getUserId(), (String) i))
                    .collect(Collectors.toSet()));
        }

        if(user.getUserId() == null) {
            String passwordValue = parameter.get("password");
            if(StringUtils.isBlank(passwordValue)) {
                passwordValue = defaultPassword;
            }
            user.setPassword(passwordValue, null);
            getUserService().createUser(user);
        } else {
            getUserService().updateUser(user);
        }

        return json(JsonResult.getTrue());
    }

    //#endregion

    //#region 角色管理

    @RequestMapping(value = "/settings/roles", method = RequestMethod.GET)
    public String roles() {
        return "settings/roles";
    }

    @RequestMapping(value = "/settings/roles/query", method = RequestMethod.POST)
    public View queryRoles(@RequestBody RequestData parameter) {
        Map<String, Object> params = new HashMap<>();
        params.put("roleName", parameter.get("roleName"));
        params.put("status", parameter.get("status"));

        int pageIndex = parameter.getInteger("pageIndex", 1);
        int pageSize = parameter.getInteger("pageSize", 10);

        PagingList<RoleInfo> roles = (PagingList<RoleInfo>) getRoleService().queryRoles(params, pageIndex, pageSize);
        return json(JsonResult.create(roles, roles.getPageTotal()));
    }

    @RequestMapping(value = "/settings/roles/save", method = RequestMethod.POST)
    public View saveRole(@RequestBody RequestData parameter) {
        RoleInfo role = new RoleInfo();
        role.setRoleCode(parameter.get("roleId"));
        role.setRoleName(parameter.get("roleName"));
        role.setRoleStatus(RoleStatusEnum.valueOf(parameter.getInteger("status")).name());
        role.setDescription(parameter.get("description"));
        List<Object> menus = parameter.getObject("menus");
        if(menus != null) {
            role.setResourceCodeList(menus.stream().map(i -> (String) i).collect(Collectors.toList()));
        }

        if(role.getRoleCode() == null) {
            getRoleService().createRole(role);
        } else {
            getRoleService().updateRole(role);
        }
        
        return json(JsonResult.getTrue());
    }

    //#endregion

    //#region 系统菜单管理

    @RequestMapping(value = "/settings/menus", method = RequestMethod.GET)
    public String menus() {
        return "settings/menus";
    }

    @RequestMapping(value = "/settings/menus/query", method = RequestMethod.POST)
    public View queryMenus() {
        List<?> menus = getFunctionService().queryFunctions(null, ResourceType.MENU);
        return json(menus);
    }

    //#endregion
    
}