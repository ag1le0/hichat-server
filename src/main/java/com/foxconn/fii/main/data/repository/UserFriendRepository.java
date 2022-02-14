package com.foxconn.fii.main.data.repository;

import com.foxconn.fii.main.data.entity.User;
import com.foxconn.fii.main.data.entity.UserFriend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserFriendRepository extends JpaRepository<UserFriend, Long> {

    List<UserFriend> findByUserId(long userId);

    Optional<UserFriend> findByUserAndFriend(User user, User friend);

    boolean existsByUserAndFriend(User user, User friend);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserFriend WHERE (user = :user AND friend = :friend) OR (user = :friend AND friend = :user)")
    void deleteByUserAndFriend(@Param("user") User user, @Param("friend") User friend);
}
