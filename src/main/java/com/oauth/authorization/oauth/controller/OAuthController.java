package com.oauth.authorization.oauth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


//http://localhost:8080/swagger-ui/index.html

@RestController
@RequestMapping("api/v1/oauth2")
public class OAuthController {
    @PostMapping("/authorize")
    public void createSession(){

    }

    @PostMapping("/signin")
    public void signin(){}

    @PostMapping("/token")
    public void issueToken(){}

    @PostMapping("/revoke")
    public void revokeToken(){}

}
