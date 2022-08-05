package com.oauth.authorization.oauth.service;

import com.oauth.authorization.oauth.dto.AuthorizeRequest;
import com.oauth.authorization.oauth.dto.SigninRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.time.Duration;
//        stringStringValueOperations.set("LoginSession:"+request.getSession().getId(), "true");
//final ValueOperations<String, String> stringStringValueOperations = redisTemplate.opsForValue();
//        stringStringValueOperations.set(key, "1");1// redis set 명령어
//final String result_1 = stringStringValueOperations.get("LoginSession:"+request.getSession().getId()); // redis get 명령어

@Service
@RequiredArgsConstructor
public class OAuthService {
    private final RedisTemplate<String, String> redisTemplate;
    private final SecretService secretService;

    public String[] convertToScopeArray(String scopeString){
        return scopeString.split("\\+");
    }

    public AuthorizeRequest makeSession(String clientId, String scopeString, String redirectUri, String state,
                              HttpServletRequest request, HttpServletResponse response) throws IOException {
        // redis login session 확인

        saveAuthReqWithSession(clientId, scopeString, redirectUri, state,request);

        if (redisTemplate.opsForValue().get("LoginSession:" + request.getSession().getId()) != null) {
            signInSSO(request, response);
        }else{
        }
        // 존재 할 경우 바로 authorization_code발급하도록 redirect

        // CLIENT 정보 validate

        // session values 생성


        return AuthorizeRequest.builder()
                .clientId(clientId)
                .scope(convertToScopeArray(scopeString))
                .redirectUri(redirectUri)
                .state(state)
                .build();
    }

    // redis에 현재 세션 저장 (하루)
    public void saveRedisSession(HttpServletRequest request, String clientId){
        redisTemplate.opsForValue().set("LoginSession:"+request.getSession().getId(), clientId, Duration.ofSeconds(3600L));
    }
    // 현재 로그인된 세션인지 확인
    public boolean isLoginedSession(HttpServletRequest request){
        return redisTemplate.opsForValue().get("LoginSession:"+request.getSession().getId())!=null;
    }

    // 로그인 처리,authCode발급과 동시에 세션 로그인 처리
    public void signIn(HttpServletRequest request, HttpServletResponse response, SigninRequest signinRequest) throws InvalidParameterException, IOException {
        // userValidate

        HttpSession session = request.getSession();
        String clientId = (String) session.getAttribute("clientId");
        String redirectUri = (String) session.getAttribute("redirectUri");
        String[] scope = (String[]) session.getAttribute("scope");
        String state = (String) session.getAttribute("state");
        // redirect
        saveRedisSession(request, clientId);


        response.sendRedirect(redirectUri+"?code="+secretService.generateAuthorizationCode()+"&state="+state);
    }
    // 이미 로그인 된 경우에 바로 authorization_code redirect
    public void signInSSO(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession();
        String redirectUri = (String) session.getAttribute("redirectUri");
        String state = (String) session.getAttribute("state");

        response.sendRedirect(redirectUri+"?code="+secretService.generateAuthorizationCode()+"&state="+state);
    }

    // session에 현재 Client 정보 저장
    public void saveAuthReqWithSession(String clientId, String scopeString, String redirectUri, String state,
                                              HttpServletRequest request){
        HttpSession session = request.getSession();
        session.setAttribute("clientId", clientId);
        session.setAttribute("redirectUri", redirectUri);
        session.setAttribute("scope", convertToScopeArray(scopeString));
        session.setAttribute("state", state);
    }
}
