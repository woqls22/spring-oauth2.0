package com.oauth.authorization.oauth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TokenRequest {
    private String authorization_code;
    private String redirectUri;
}
