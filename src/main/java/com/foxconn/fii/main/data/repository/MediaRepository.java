package com.foxconn.fii.main.data.repository;

import com.foxconn.fii.main.data.entity.Media;
import com.foxconn.fii.main.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    Optional<Media> findByUuid(String uuid);
}
