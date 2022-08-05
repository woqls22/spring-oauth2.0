package com.oauth.authorization.oauth.service;

import com.oauth.authorization.oauth.entity.AuthorizeRequest;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Service
public class OAuthService {

    public String[] convertToScopeArray(String scopeString){
        return scopeString.split("\\+");
    }

    public AuthorizeRequest makeSession(String clientId, String scopeString, String redirectUri, String state,
                              HttpServletRequest request, HttpServletResponse response) {

        // redis login session 확인
        // 존재 할 경우 바로 authorization_code발급


        // CLIENT 정보 validate

        // session values 생성

        saveAuthReqWithSession(clientId, scopeString, redirectUri, state,request);

        return AuthorizeRequest.builder()
                .clientId(clientId)
                .scope(convertToScopeArray(scopeString))
                .redirectUri(redirectUri)
                .state(state)
                .build();
    }

    public void saveAuthReqWithSession(String clientId, String scopeString, String redirectUri, String state,
                                              HttpServletRequest request){
        HttpSession session = request.getSession();
        session.setAttribute("clientId", clientId);
        session.setAttribute("redirectUri", redirectUri);
        session.setAttribute("scope", convertToScopeArray(scopeString));
        session.setAttribute("state", state);
    }
}
