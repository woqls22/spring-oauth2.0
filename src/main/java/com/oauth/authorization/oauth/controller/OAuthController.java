package com.oauth.authorization.oauth.controller;

import com.oauth.authorization.oauth.dto.*;
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
    @GetMapping("/authorize")
    public ResponseEntity<?> createSession(
            @RequestParam(value = "client_id", required = true) String clientId,
            @RequestParam(value = "scope", required = true) String scopeString,
            @RequestParam(value = "redirect_uri", required = true) String redirectUri,
            @RequestParam(value = "state", required = true) String state,
            HttpServletRequest request, HttpServletResponse response
    ) throws IOException {
        return ResponseEntity.ok(oAuthService.makeSession(clientId, scopeString, redirectUri, state, request, response));
    }

    @PostMapping("/signin")
    public void signin(HttpServletRequest request, HttpServletResponse response, @RequestBody SigninRequest signInRequest, @RequestHeader("request-id")String requestId) throws IOException {
        oAuthService.signIn(request, response, signInRequest, requestId);
    }

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> issueToken(@RequestBody TokenRequest tokenRequest, @RequestHeader("request-id")String requestId){
        return ResponseEntity.ok(tokenService.generateTokenResponse(tokenRequest, requestId));
    }

    @PostMapping("/revoke")
    public ResponseEntity<String> revokeToken(@RequestBody RevokeRequest revokeRequest, @RequestHeader("request-id")String requestId){
        oAuthService.revokeToken(revokeRequest, requestId);
        return ResponseEntity.ok("Success Revoke Token");
    }

    @PostMapping("/initialize")
    public ResponseEntity<InitializeResponse> enrollClient(){
        return ResponseEntity.ok(oAuthService.initialize());
    }

    @PostMapping("/verify")
    public ResponseEntity<Boolean> verifyToken(){
        return ResponseEntity.ok(true);
    }
}
