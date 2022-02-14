package com.foxconn.fii.main.data.repository;

import com.foxconn.fii.main.data.entity.User;
import com.foxconn.fii.main.data.entity.UserFriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFriendRequestRepository extends JpaRepository<UserFriendRequest, Long> {

    Optional<UserFriendRequest> findByUserAndFriend(User user, User friend);

    Integer countByFriendAndStatus(User friend, UserFriendRequest.Status status);

    List<UserFriendRequest> findByFriendAndStatus(User friend, UserFriendRequest.Status status);

    List<UserFriendRequest> findByUserAndStatus(User user, UserFriendRequest.Status status);

    boolean existsByUserAndFriend(User user, User friend);
}
