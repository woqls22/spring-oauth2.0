package com.oauth.authorization.service;

import com.oauth.authorization.entity.Client;
import com.oauth.authorization.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    public void enrollMockClient(){
        Client client = Client.builder()
                .clientId(UUID.randomUUID())
                .applicationName("medical_link")
                .redirectUri("http://woqls22.github.io")
                .grantType("code_grant")
                .scopeString("email+profile+open_id+name")
                .build();
        clientRepository.save(client);
    }
    public void validateClient(String clientId, String redirectUri){
        Client client = clientRepository.findById(UUID.fromString(clientId)).get();
        if(!Objects.equals(client.getRedirectUri(), redirectUri)){
            throw new InvalidParameterException();
        }
    }
}
