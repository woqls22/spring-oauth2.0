package com.oauth.authorization.oauth.service;

import com.oauth.authorization.entity.Client;
import com.oauth.authorization.entity.User;
import com.oauth.authorization.oauth.dto.AuthorizeRequest;
import com.oauth.authorization.oauth.dto.InitializeResponse;
import com.oauth.authorization.oauth.dto.RevokeRequest;
import com.oauth.authorization.oauth.dto.SigninRequest;
import com.oauth.authorization.service.ClientService;
import com.oauth.authorization.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.time.Duration;
import java.util.*;
//        stringStringValueOperations.set("LoginSession:"+request.getSession().getId(), "true");
//final ValueOperations<String, String> stringStringValueOperations = redisTemplate.opsForValue();
//        stringStringValueOperations.set(key, "1");1// redis set 명령어
//final String result_1 = stringStringValueOperations.get("LoginSession:"+request.getSession().getId()); // redis get 명령어

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthService {
    private final RedisTemplate<String, String> redisTemplate;
    private final SecretService secretService;
    private final UserService userService;
    private final ClientService clientService;
    private final TokenService tokenService;
    public String[] convertToScopeArray(String scopeString){
        return scopeString.split("\\+");
    }

    public AuthorizeRequest makeSession(String clientId, String scopeString, String redirectUri, String state,
                              HttpServletRequest request, HttpServletResponse response) throws IOException {
        //Client 정보 validate
        clientService.validateClient(clientId, redirectUri);
        //현재 세션 저장
        String sessionId = saveAuthReqWithSession(clientId, scopeString, redirectUri, state,request);
        // 이미 로그인 된 세션일 경우, authcode발급하도록 redirect
        if (redisTemplate.opsForValue().get("LoginSession:" + request.getSession().getId()) != null) {
            signInSSO(request, response);
        }

        ResponseCookie cookie = ResponseCookie.from("request-id", sessionId)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(false)
                .build();

        response.setHeader("Set-Cookie", cookie.toString());

        response.sendRedirect("http://localhost:3000/login");

        return AuthorizeRequest.builder()
                .clientId(clientId)
                .scope(convertToScopeArray(scopeString))
                .redirectUri(redirectUri)
                .state(state)
                .build();
    }
    // redis에 현재 세션 저장 (하루)
    public void saveRedisSession(HttpServletRequest request, String userId){
        redisTemplate.opsForValue().set("LoginSession:"+request.getSession().getId(), userId, Duration.ofSeconds(3600L));
    }
    // 현재 로그인된 세션인지 확인
    public boolean isLoginedSession(HttpServletRequest request){
        return redisTemplate.opsForValue().get("LoginSession:"+request.getSession().getId())!=null;
    }
    // 로그인 처리,authCode발급과 동시에 세션 로그인 처리
    public void signIn(HttpServletRequest request, HttpServletResponse response, SigninRequest signinRequest, String requestId) throws InvalidParameterException, IOException {
        String clientId = (String) redisTemplate.opsForHash().get(requestId, "clientId");
        String redirectUri = (String) redisTemplate.opsForHash().get(requestId, "redirectUri");
        String state = (String) redisTemplate.opsForHash().get(requestId, "state");
        String scope = (String) redisTemplate.opsForHash().get(requestId, "scope");
        // userValidate
        try {
            userService.isValidUser(signinRequest.getEmail(), signinRequest.getPassword()).orElseThrow(Exception::new);
        } catch (Exception e) {
            log.info("[Invalid User Authorization]");
            e.printStackTrace();
        }
        saveRedisSession(request, "true");
        String authCode = secretService.generateAuthorizationCode();
        secretService.setAuthorizationCode(authCode, redirectUri);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.sendRedirect(redirectUri+"?code="+authCode+"&state="+state);
    }
    // 이미 로그인 된 경우에 바로 authorization_code redirect
    public void signInSSO(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String redirectUri = (String) session.getAttribute("redirectUri");
        String state = (String) session.getAttribute("state");
        response.sendRedirect(redirectUri+"?code="+secretService.generateAuthorizationCode()+"&state="+state);
    }

    // session에 현재 Client 정보 저장
    public String saveAuthReqWithSession(String clientId, String scopeString, String redirectUri, String state,
                                              HttpServletRequest request){
        HttpSession session = request.getSession();
        String sessionId= session.getId();

        redisTemplate.opsForHash().put(sessionId,"clientId",clientId);
        redisTemplate.opsForHash().put(sessionId,"redirectUri",redirectUri);
        redisTemplate.opsForHash().put(sessionId,"scope",scopeString);
        redisTemplate.opsForHash().put(sessionId,"state",state);
        redisTemplate.expire(sessionId,Duration.ofDays(1));
        return sessionId;
    }

    public InitializeResponse initialize(){
        Client client = clientService.enrollMockClient();
        User user = userService.enrollMockUser();

        return InitializeResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .redirectUri(client.getRedirectUri())
                .clientId(client.getClientId().toString())
                .userId(user.getUserId().toString())
                .scope(client.getScopeString())
                .state("test")
                .build();
    }
    public void revokeToken(RevokeRequest revokeRequest, String requestId){
        // session delete.
        redisTemplate.delete(requestId);
        // enroll token blackList
        tokenService.revokeToken(revokeRequest.getAccessToken());
    }
}
