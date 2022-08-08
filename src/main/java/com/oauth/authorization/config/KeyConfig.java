package com.oauth.authorization.config;

import lombok.Builder;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;

@Component
@Configuration
@Getter
public class KeyConfig {
//    private RSAPrivateKey privateKey;
//    private RSAPublicKey publicKey;
    private String HMAC_SECRET = "MY_OAUTH_SERVER_SECRET_KEY_V2.1";

    public KeyConfig(){

    }
}
