package com.soonsoft.uranus.api.controller;

import com.soonsoft.uranus.web.mvc.model.IResultData;
import com.soonsoft.uranus.web.mvc.model.JsonResult;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultController {

    @RequestMapping(value="/", method = RequestMethod.GET)
    public IResultData index() {
        return JsonResult.create("Welcome to URANUS API.");
    }

}
