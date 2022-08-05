package com.oauth.authorization.oauth.controller;

import com.oauth.authorization.oauth.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


//http://localhost:8080/swagger-ui/index.html

@RestController
@RequestMapping("api/v1/oauth2")
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthService oAuthService;

    @PostMapping("/session")
    public ResponseEntity<?> createSession(
            @RequestParam(value = "client_id", required = true) String clientId,
            @RequestParam(value = "scope", required = true) String scopeString,
            @RequestParam(value = "redirectUri", required = true) String redirectUri,
            @RequestParam(value = "state", required = true) String state,
            HttpServletRequest request, HttpServletResponse response
    ){
        return ResponseEntity.ok(oAuthService.makeSession(clientId, scopeString, redirectUri, state, request, response));
    }

    @PostMapping("/signin")
    public void signin(){}

    @PostMapping("/token")
    public void issueToken(){}

    @PostMapping("/revoke")
    public void revokeToken(){}

}
