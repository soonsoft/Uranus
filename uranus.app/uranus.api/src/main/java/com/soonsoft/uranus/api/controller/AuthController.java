package com.soonsoft.uranus.api.controller;

import com.soonsoft.uranus.security.SecurityManager;
import com.soonsoft.uranus.security.authentication.IUserManager;
import com.soonsoft.uranus.security.authorization.IFunctionManager;
import com.soonsoft.uranus.security.entity.MenuInfo;
import com.soonsoft.uranus.security.entity.UserInfo;
import com.soonsoft.uranus.api.controller.base.BaseController;
import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.web.error.WebActionException;
import com.soonsoft.uranus.web.mvc.model.IResultData;
import com.soonsoft.uranus.web.mvc.model.JsonResult;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AuthController extends BaseController {

    @PostMapping("/auth/user/change/password")
    public IResultData changePassword(
        @RequestParam String originalPassword, 
        @RequestParam String newPassword) {

        Guard.notEmpty(originalPassword, "the originalPassword is required.");
        Guard.notEmpty(newPassword, "the newPassword is required.");

        try {
            IUserManager userManager = SecurityManager.current().getUserManager();
            userManager.changeMyPassword(getCurrentUser());
        } catch(Exception e) {
            throw new WebActionException("更新密码失败。", e);
        }

        return JsonResult.create();
    }

    @PostMapping("/auth/user/menus")
    public List<MenuInfo> getMenus() {
        IFunctionManager functionManager = SecurityManager.current().getFunctionManager();
        UserInfo user = getCurrentUser();
        return functionManager.getMenus(user);
    }
}