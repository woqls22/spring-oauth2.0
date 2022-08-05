package com.oauth.authorization.oauth.dto;

import lombok.*;

@Getter
@NoArgsConstructor
public class SigninRequest {
    @NonNull
    private String userId;
    @NonNull
    private String password;
    @NonNull
    private String redirectUri;
}
