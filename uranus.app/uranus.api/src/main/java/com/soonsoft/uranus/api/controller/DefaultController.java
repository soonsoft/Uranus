package com.soonsoft.uranus.api.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class DefaultController {
    
    @GetMapping(value="/")
    public String index() {
        return "Welcome to URANUS API.";
    }

}
