package com.oauth.authorization.oauth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.oauth.authorization.config.KeyConfig;
import com.oauth.authorization.entity.LoginedSession;
import com.oauth.authorization.oauth.dto.TokenRequest;
import com.oauth.authorization.oauth.dto.TokenResponse;
import com.oauth.authorization.repository.LoginedSessionRepository;
import com.oauth.authorization.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final KeyConfig keyConfig;
    private final UserService userService;
    private final SecretService secretService;
    private final LoginedSessionRepository refreshTokenRepository;
    @Value("oauth2.client.medical_link.token_info.iss")
    private String ISSUER;

    @Value("oauth2.client.medical_link.token_info.sub")
    private String SUBJECT;

    @Value("oauth2.client.medical_link.token_info.aud")
    private String AUDIENCE;

    @Value("oauth2.client.medical_link.token_info.scope")
    private String SCOPE_STRING;

    private final RedisTemplate redisTemplate;


    public TokenResponse generateTokenResponse(TokenRequest tokenRequest){

        // authorization Code verify
        secretService.checkValidAuthorizationCode(tokenRequest.getAuthorization_code(), tokenRequest.getRedirectUri());

        String refreshToken = generateRefreshToken();
        LoginedSession loginedSession = LoginedSession.builder()
                .refreshToken(refreshToken)
                .expiredDate(getNextYearTimeStamp())
                .build();

        refreshTokenRepository.save(loginedSession);

        return TokenResponse.builder()
                .accessToken(generateAccessToken())
                .refreshToken(refreshToken)
                .createAt(new Date())
                .expiresAt(getTomorrowTimeStamp())
                .issuer(ISSUER)
                .build();
    }

    public String generateAccessToken(){
        Algorithm algorithm = Algorithm.HMAC256(keyConfig.getHMAC_SECRET());
        return JWT.create()
                .withArrayClaim("scope", SCOPE_STRING.split("\\+"))
                .withAudience(AUDIENCE)
                .withSubject(SUBJECT)
                .withIssuer(ISSUER)
                .withExpiresAt(getTomorrowTimeStamp())
                .sign(algorithm);
    }
    public static Date getTomorrowTimeStamp(){
        return new Date(new Date().getTime()+(1000 * 60 * 60 * 24));
    }
    public static Date getNextYearTimeStamp(){return new Date(new Date().getTime()+(1000 * 60 * 60 * 24*365));}
    public String generateRefreshToken(){
        return secretService.generateRefreshToken();
    }
    public int removeRefreshToken(){
        return 200;
    }

    public String revokeToken(String accessToken){
        SetOperations setOperations = redisTemplate.opsForSet();
        setOperations.add("TOKEN_BLACKLIST", accessToken);

        return accessToken;
    }

    public boolean isRevokedToken(String accessToken){
        SetOperations setOperations = redisTemplate.opsForSet();
        return setOperations.isMember("TOKEN_BLACKLIST", accessToken).booleanValue();
    }
}
