package com.oauth.authorization.entity;

import jdk.jfr.Name;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class LoginedSession {
    @GeneratedValue @Id
    @Column(name="SESSION_ID")
    private Long SessionId;
    private String refreshToken;
    private Date expiredDate;

    @Builder
    public LoginedSession(String refreshToken, Date expiredDate) {
        this.refreshToken = refreshToken;
        this.expiredDate = expiredDate;
    }
}
