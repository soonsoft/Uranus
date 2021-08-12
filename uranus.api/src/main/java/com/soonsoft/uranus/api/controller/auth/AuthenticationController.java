package com.soonsoft.uranus.api.controller.auth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {


    @PostMapping("/auth/refresh-token")
    public void refreshToken(String refreshToken) {

    }


    
}
