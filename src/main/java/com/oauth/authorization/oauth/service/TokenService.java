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
import javax.transaction.Transactional;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

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


    @Transactional
    public TokenResponse generateTokenResponse(TokenRequest tokenRequest, String requestId){

        // authorization Code verify
        secretService.checkValidAuthorizationCode(tokenRequest.getAuthorization_code(), tokenRequest.getRedirectUri());

        String refreshToken = generateRefreshToken();
        LoginedSession loginedSession = LoginedSession.builder()
                .refreshToken(refreshToken)
                .expiredDate(getNextYearTimeStamp())
                .build();

        refreshTokenRepository.save(loginedSession);
        String accessToken = generateAccessToken();
        // 현재 세션에서 사용하고 있는 토큰 ADD Operation. 추후 로그아웃 기능 시, 일괄 토큰 만료처리 하기 위함.
        redisTemplate.opsForSet().add("USED_TOKENS:"+requestId,accessToken);

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

    public String revokeToken(String accessToken, String requestId){
        SetOperations setOperations = redisTemplate.opsForSet();
        Set<String> tokens = setOperations.members("USED_TOKEN:" + requestId);
        for(String token : tokens){
            setOperations.add("TOKEN_BLACKLIST", token);
        }
        return accessToken;
    }

    public boolean isRevokedToken(String accessToken){
        SetOperations setOperations = redisTemplate.opsForSet();
        return setOperations.isMember("TOKEN_BLACKLIST", accessToken).booleanValue();
    }
}
