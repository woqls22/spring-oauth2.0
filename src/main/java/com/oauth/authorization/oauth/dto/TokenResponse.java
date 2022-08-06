package com.oauth.authorization.oauth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Builder
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private Date createAt;
    private Date expiresAt;
    private String token_type;
    private String issuer;
}
