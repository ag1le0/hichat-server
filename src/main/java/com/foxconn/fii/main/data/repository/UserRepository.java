package com.foxconn.fii.main.data.repository;

import com.foxconn.fii.main.data.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.avatar WHERE u.username = :username")
    Optional<User> findByUsername(String username);

    @Query(value = "SELECT TOP 15 u.*, m.url as avatar_media_url, m.thumb_url as avatar_media_thumb_url, f.user_id as friend_id, f.friend_nick_name, r.id as request_id, fr.id as friend_request_id FROM pea_user u \n" +
            "LEFT JOIN pea_media as m ON u.avatar_media_id = m.id \n" +
            "LEFT JOIN (SELECT * FROM pea_user_friend uf WHERE uf.user_id = :user_id) as f ON u.id = f.friend_id \n" +
            "LEFT JOIN (SELECT * FROM pea_user_friend_request ufr1 WHERE ufr1.user_id = :user_id and ufr1.status = 0) as r ON u.id = r.friend_id \n" +
            "LEFT JOIN (SELECT * FROM pea_user_friend_request ufr2 WHERE ufr2.friend_id = :user_id and ufr2.status = 0) as fr ON u.id = fr.user_id \n" +
            "WHERE u.id != :user_id AND (u.username LIKE :query OR u.name LIKE :query OR u.chinese_name LIKE :query)", nativeQuery = true)
    List<Map<String, Object>> searchUser(@Param("user_id") Long userId, @Param("query") String query);

    @Query("SELECT u FROM User u WHERE u.id != :userId AND (u.username LIKE :query OR u.name LIKE :query OR u.chineseName LIKE :query)")
    Page<User> searchUser(Long userId, String query, Pageable pageable);

    List<User> findByLevel(String level);
}
