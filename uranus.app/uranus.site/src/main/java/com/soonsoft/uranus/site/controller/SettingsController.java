package com.soonsoft.uranus.site.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.data.entity.Page;
import com.soonsoft.uranus.security.SecurityManager;
import com.soonsoft.uranus.services.membership.service.FunctionService;
import com.soonsoft.uranus.services.membership.service.RoleService;
import com.soonsoft.uranus.services.membership.service.UserService;
import com.soonsoft.uranus.services.membership.dto.AuthPassword;
import com.soonsoft.uranus.services.membership.dto.AuthRole;
import com.soonsoft.uranus.services.membership.dto.AuthUser;
import com.soonsoft.uranus.services.membership.dto.SysMenu;
import com.soonsoft.uranus.site.controller.base.BaseController;
import com.soonsoft.uranus.core.common.lang.StringUtils;
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
    public View saveUser(@RequestBody AuthUser user) {
        UserService userService = getUserService();
        if(StringUtils.isEmpty(user.getUserId())) {
            String defaultPassword = "1";
            String salt = "";

            String passwordValue = userService.encryptPassword(defaultPassword, salt);
            AuthPassword password = new AuthPassword();
            password.setPasswordValue(passwordValue);
            password.setPasswordSalt(salt);
            userService.create(user, password);
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
    public View saveRole(@RequestBody AuthRole role) {
        if(StringUtils.isEmpty(role.getRoleId())) {
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