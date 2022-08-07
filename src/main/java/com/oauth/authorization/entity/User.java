package com.oauth.authorization.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.UUID;
@Entity(name="USERS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @Column(columnDefinition = "BINARY(16)", name="USER_ID")
    private UUID userId;

    @Column(name="EMAIL", unique = true)
    private String email;
    @Column(name="MOBILE_NUMBER")
    private String mobileNumber;
    @Column(name="NAME")
    private String name;
    @Column(name="GENDER")
    private String gender;
    @Column(name="password")
    private String password;

    @OneToMany(mappedBy = "user")
    private List<ClientUserMap> clientUserMapList;

    @Builder
    public User(UUID userId, String email, String mobileNumber, String name, String gender, String password, List<ClientUserMap> clientUserMapList) {
        this.userId = userId;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.name = name;
        this.gender = gender;
        this.password = password;
        this.clientUserMapList = clientUserMapList;
    }
}
