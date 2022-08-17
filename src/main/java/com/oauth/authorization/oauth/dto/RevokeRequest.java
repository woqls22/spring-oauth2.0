package com.oauth.authorization.oauth.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RevokeRequest {
    private String accessToken;
}
