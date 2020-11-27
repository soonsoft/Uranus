package com.soonsoft.uranus.api.controller;

import com.soonsoft.uranus.security.SecurityManager;
import com.soonsoft.uranus.security.authentication.IUserManager;
import com.soonsoft.uranus.security.authorization.IFunctionManager;
import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.api.controller.base.BaseController;
import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.web.error.HttpActionException;
import com.soonsoft.uranus.web.mvc.model.JsonResult;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;

/**
 * AccountController
 */
@Controller
public class AccountController extends BaseController {

    @RequestMapping(value = "/account/password-changing", method = RequestMethod.POST)
    public View changePassword(
        @RequestParam String originalPassword, 
        @RequestParam String newPassword) {

        Guard.notEmpty(originalPassword, "the originalPassword is required.");
        Guard.notEmpty(newPassword, "the newPassword is required.");

        try {
            String username = getCurrentUser().getUsername();
            IUserManager userManager = SecurityManager.current().getUserManager();
            UserInfo user = new UserInfo(username, newPassword);

            userManager.changeMyPassword(user);
        } catch(Exception e) {
            throw new HttpActionException("更新密码失败。", e);
        }

        return json(JsonResult.create());
    }

    @RequestMapping(value = "/account/menus", method = RequestMethod.POST)
    public View getMenus() {
        IFunctionManager functionManager = SecurityManager.current().getFunctionManager();
        UserInfo user = getCurrentUser();
        return json(functionManager.getMenus(user));
    }
}