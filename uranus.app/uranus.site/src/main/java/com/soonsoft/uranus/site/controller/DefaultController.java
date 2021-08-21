package com.soonsoft.uranus.site.controller;

import com.soonsoft.uranus.site.controller.base.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.View;

/**
 * Created by Soon on 2017/10/2.
 */
@Controller
public class DefaultController extends BaseController {

    @RequestMapping(value="/", method = RequestMethod.GET)
    public View root() {
        return redirect("index");
    }

    @RequestMapping(value = "/building", method = RequestMethod.GET)
    public String building() {
        return "home/building";
    }

}
