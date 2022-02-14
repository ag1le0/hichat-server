package com.foxconn.fii.main.data.repository;

import com.foxconn.fii.main.data.entity.OAuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuthUserRepository extends JpaRepository<OAuthUser, Integer> {

    Optional<OAuthUser> findByUsername(String username);
}
