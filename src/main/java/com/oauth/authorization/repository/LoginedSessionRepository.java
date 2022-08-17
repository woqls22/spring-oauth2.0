package com.oauth.authorization.repository;

import com.oauth.authorization.entity.LoginedSession;
import com.oauth.authorization.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginedSessionRepository extends JpaRepository<LoginedSession, Long> {
}
