package com.soonsoft.uranus.api.controller;

import com.soonsoft.uranus.api.controller.base.BaseController;
import com.soonsoft.uranus.web.mvc.model.JsonResult;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

@Controller
public class DefaultController extends BaseController {

    @RequestMapping(value="/", method = RequestMethod.GET)
    public View root() {
        return json(JsonResult.create("Welcome to URANUS API."));
    }

}
