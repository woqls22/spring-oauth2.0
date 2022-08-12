package com.oauth.authorization.oauth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Component
public class SecretService {

    private final RedisTemplate redisTemplate;

    public String generateAuthorizationCode(){
        int leftLimit = 48;
        int righntLimit = 90;

        Random random = new Random();
        return random.ints(leftLimit, righntLimit+1)
                .filter(i->(i<=57 || i>=65)&&(i<=90))
                .limit(30)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }
    public String generateRefreshToken(){
        int leftLimit = 48;
        int righntLimit = 90;

        Random random = new Random();
        return random.ints(leftLimit, righntLimit+1)
                .filter(i->(i<=57 || i>=65)&&(i<=90))
                .limit(10)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }


    public void setAuthorizationCode(String authorizationCode, String redirectUri){
        // 발급된 인증코드를 redis에 저장
        redisTemplate.opsForValue().set("authCode:"+authorizationCode, authorizationCode, Duration.ofSeconds(60));
        // TTL 1분
    }

    public void checkValidAuthorizationCode(String authorizationCode, String redirectUri){
        // auth code 확인
        if(redisTemplate.opsForValue().get("authCode:"+authorizationCode).equals(null)){
            throw new InvalidParameterException();
        }
        // auth code 만료 처리
        redisTemplate.delete("authCode:"+authorizationCode);
    }
}
