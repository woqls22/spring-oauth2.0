package com.oauth.authorization.oauth.controller;

import com.oauth.authorization.oauth.dto.SigninRequest;
import com.oauth.authorization.oauth.dto.TokenRequest;
import com.oauth.authorization.oauth.dto.TokenResponse;
import com.oauth.authorization.oauth.service.OAuthService;
import com.oauth.authorization.oauth.service.TokenService;
import com.oauth.authorization.service.ClientService;
import com.oauth.authorization.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


//http://localhost:8080/swagger-ui/index.html

@RestController
@RequestMapping("api/v1/oauth2")
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthService oAuthService;
    private final UserService userService;
    private final TokenService tokenService;
    private final ClientService clientService;
    @GetMapping("/session")
    public ResponseEntity<?> createSession(
            @RequestParam(value = "client_id", required = true) String clientId,
            @RequestParam(value = "scope", required = true) String scopeString,
            @RequestParam(value = "redirectUri", required = true) String redirectUri,
            @RequestParam(value = "state", required = true) String state,
            HttpServletRequest request, HttpServletResponse response
    ) throws IOException {
        return ResponseEntity.ok(oAuthService.makeSession(clientId, scopeString, redirectUri, state, request, response));
    }

    @PostMapping("/signin")
    public void signin(HttpServletRequest request, HttpServletResponse response, @RequestBody SigninRequest signInRequest) throws IOException {
        oAuthService.signIn(request, response, signInRequest);
    }

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> issueToken(@RequestBody TokenRequest tokenRequest, HttpServletRequest request){
        return ResponseEntity.ok(tokenService.generateTokenResponse(tokenRequest, request));
    }

    @PostMapping("/revoke")
    public void revokeToken(){}

    @PostMapping("/client")
    public void enrollClient(){
        clientService.enrollMockClient();
    }
    @PostMapping("/user")
    public void enrollUser(){
        userService.enrollMockUser();
    }
}
