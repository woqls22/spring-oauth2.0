package com.oauth.authorization.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity(name="CLIENT_USER_MAP")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClientUserMap {
    @Column
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name="CLIENT_ID")
    private Client client;

    @ManyToOne
    @JoinColumn(name="USER_ID")
    private User user;

    @Builder
    public ClientUserMap(Long id, Client client, User user) {
        this.id = id;
        this.client = client;
        this.user = user;
    }
}
