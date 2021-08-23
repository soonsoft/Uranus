package com.soonsoft.uranus.api.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class DefaultController {
    
    @GetMapping(value="/")
    public String index() {
        return "Welcome to URANUS API.";
    }

    @GetMapping(value="/value/boolean")
    public Boolean getBoolean() {
        return Boolean.TRUE;
    }

    @GetMapping(value="/value/bytes")
    public byte[] getByteArray() {
        return "The Text Value.".getBytes();
    }

}
