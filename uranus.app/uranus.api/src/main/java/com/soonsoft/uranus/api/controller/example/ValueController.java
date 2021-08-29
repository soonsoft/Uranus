package com.soonsoft.uranus.api.controller.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/value")
public class ValueController {


    @GetMapping(value="/boolean")
    public Boolean getBoolean() {
        return Boolean.TRUE;
    }

    @GetMapping(value="/bytes")
    public byte[] getByteArray() {
        return "The Text Value.".getBytes();
    }

    @GetMapping(value = "/string")
    public String getString() {
        return "The String Value.";
    }


    
}
