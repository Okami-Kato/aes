package com.okami.server.security.repository;

import com.okami.server.security.domain.UserKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserKeyRepository extends JpaRepository<UserKey, Integer> {

}
