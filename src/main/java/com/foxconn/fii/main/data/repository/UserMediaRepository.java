package com.foxconn.fii.main.data.repository;

import com.foxconn.fii.main.data.entity.Media;
import com.foxconn.fii.main.data.entity.User;
import com.foxconn.fii.main.data.entity.UserMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMediaRepository extends JpaRepository<UserMedia, Long> {

    boolean existsByUserAndMedia(User user, Media media);
}
