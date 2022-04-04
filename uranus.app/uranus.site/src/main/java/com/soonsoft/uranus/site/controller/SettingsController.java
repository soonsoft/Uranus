package com.soonsoft.uranus.site.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.soonsoft.uranus.data.entity.Page;
import com.soonsoft.uranus.security.SecurityManager;
import com.soonsoft.uranus.services.membership.po.AuthRole;
import com.soonsoft.uranus.services.membership.po.AuthUser;
import com.soonsoft.uranus.services.membership.po.SysMenu;
import com.soonsoft.uranus.services.membership.service.FunctionService;
import com.soonsoft.uranus.services.membership.service.RoleService;
import com.soonsoft.uranus.services.membership.service.UserService;
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

    private UserService getUserService() {
        return (UserService) SecurityManager.current().getUserManager();
    }

    private RoleService getRoleService() {
        return (RoleService) SecurityManager.current().getRoleManager();
    }

    private FunctionService getFunctionService() {
        return (FunctionService) SecurityManager.current().getFunctionManager();
    }

    //#region 用户管理

    @RequestMapping(value = "/settings/users", method = RequestMethod.GET)
    public String users() {
        return "settings/users";
    }

    @RequestMapping(value = "/settings/users/query", method = RequestMethod.POST)
    public View queryUsers(@RequestBody RequestData parameter) {
        Map<String, Object> params = new HashMap<>();
        params.put("userName", parameter.get("userName"));
        params.put("status", parameter.getInteger("status"));

        Page page = new Page(
            parameter.getInteger("pageIndex"), 
            parameter.getInteger("pageSize"));

        List<AuthUser> users = getUserService().queryUsers(params, page);
        return json(JsonResult.create(users, page.getTotal()));
    }

    @RequestMapping(value = "/settings/users/save", method = RequestMethod.POST)
    public View saveUser(@RequestBody RequestData parameter) {
        AuthUser user = new AuthUser();
        user.setUserId(parameter.getUUID("userId"));
        user.setUserName(parameter.get("userName"));
        user.setNickName(parameter.get("nickName"));
        user.setNickName(parameter.get("cellPhone"));
        user.setEmail(parameter.get("email"));
        user.setStatus(parameter.getInteger("status"));
        user.setCreateTime(parameter.getJsonDate("createTime"));

        List<Object> roles = parameter.getObject("roles");
        if(roles != null) {
            user.setRoles(roles.stream().map(i -> UUID.fromString((String) i)).collect(Collectors.toList()));
        }

        List<Object> functions = parameter.getObject("functions");
        if(functions != null) {
            user.setFunctions(functions.stream().map(i -> UUID.fromString((String) i)).collect(Collectors.toList()));
        }

        UserService userService = getUserService();
        if(user.getUserId() == null) {
            String passwordValue = "1";
            userService.create(user, passwordValue);
        } else {
            userService.update(user);
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
        params.put("status", parameter.getInteger("status"));

        Page page = new Page(
            parameter.getInteger("pageIndex"), 
            parameter.getInteger("pageSize"));

        List<AuthRole> roles = getRoleService().queryRoles(params, page);
        return json(JsonResult.create(roles, page.getTotal()));
    }

    @RequestMapping(value = "/settings/roles/save", method = RequestMethod.POST)
    public View saveRole(@RequestBody RequestData parameter) {
        AuthRole role = new AuthRole();
        role.setRoleId(parameter.getUUID("roleId"));
        role.setRoleName(parameter.get("roleName"));
        role.setStatus(parameter.getInteger("status"));
        role.setDescription(parameter.get("description"));
        List<Object> menus = parameter.getObject("menus");
        if(menus != null) {
            role.setMenus(menus.stream().map(i -> UUID.fromString((String) i)).collect(Collectors.toList()));
        }

        if(role.getRoleId() == null) {
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
        Map<String, Object> params = new HashMap<>();
        List<SysMenu> menus = getFunctionService().getAllMenus(params);

        return json(menus);
    }

    //#endregion
    
}