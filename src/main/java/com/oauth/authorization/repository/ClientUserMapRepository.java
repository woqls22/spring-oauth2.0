package com.oauth.authorization.repository;

import com.oauth.authorization.entity.ClientUserMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientUserMapRepository extends JpaRepository<ClientUserMap, Long> {
}
