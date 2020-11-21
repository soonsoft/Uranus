package com.soonsoft.uranus.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Soon on 2017/10/2.
 */
@Controller
@RequestMapping(value="/")
public class HomeController {

    @RequestMapping(value = "index", method = RequestMethod.GET)
    public String index() {
        return "home/index";
    }

}
