package com.oauth.authorization.oauth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Component
public class SecretService {

    public String generateAuthorizationCode(){
        int leftLimit = 48;
        int righntLimit = 90;

        Random random = new Random();
        return random.ints(leftLimit, righntLimit+1)
                .filter(i->(i<=57 || i>=65)&&(i<=90))
                .limit(50)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }
}
