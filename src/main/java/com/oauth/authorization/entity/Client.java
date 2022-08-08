package com.oauth.authorization.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity(name="CLIENT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Client {
    @Id
    @Column(columnDefinition = "BINARY(16)", name="CLIENT_ID")
    private UUID clientId;

    @Column(name="REDIRECT_URI")
    private String redirectUri;
    @Column(name="GRANT_TYPE")
    private String grantType;
    @Column(name="SCOPE")
    private String scopeString;
    @Column(name="NAME")
    private String applicationName;
    @OneToMany(mappedBy = "client")
    private List<ClientUserMap> clientUserMapList;

    @Builder
    public Client(UUID clientId, String redirectUri, String grantType, String scopeString, String applicationName, List<ClientUserMap> clientUserMapList) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.grantType = grantType;
        this.scopeString = scopeString;
        this.applicationName = applicationName;
        this.clientUserMapList = clientUserMapList;
    }
}
