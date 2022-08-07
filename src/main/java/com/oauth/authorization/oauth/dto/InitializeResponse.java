package com.oauth.authorization.oauth.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class InitializeResponse {
    private String email;
    private String name;
    private String userId;
    private String clientId;
    private String redirectUri;
    private String state;
    private String scope;
}
