package com.oauth.authorization.oauth.dto;

import lombok.*;

@Getter
@NoArgsConstructor
public class SigninRequest {
    @NonNull
    private String email;
    @NonNull
    private String password;
}
