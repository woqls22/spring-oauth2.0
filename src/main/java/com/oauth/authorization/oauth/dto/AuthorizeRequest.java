package com.oauth.authorization.oauth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthorizeRequest {
    private String clientId;
    private String redirectUri;
    private String[] scope;
    private String state;
}
