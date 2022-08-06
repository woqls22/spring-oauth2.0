package com.oauth.authorization.service;

import com.oauth.authorization.entity.User;
import com.oauth.authorization.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> isValidUser(String email, String password){
        User user = userRepository.findByEmail(email).get();
        if(Objects.equals(user.getPassword(), password)){
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public void enrollMockUser(){
        User build = User.builder()
                .name("leejaebeen")
                .email("jaebeen_lee@hyundai.com")
                .password("admin")
                .userId(UUID.randomUUID())
                .mobileNumber("010-1234-5678")
                .gender("male")
                .build();
        userRepository.save(build);
    }
}
