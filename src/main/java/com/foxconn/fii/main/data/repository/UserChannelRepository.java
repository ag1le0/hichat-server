package com.foxconn.fii.main.data.repository;

import com.foxconn.fii.main.data.entity.User;
import com.foxconn.fii.main.data.entity.UserChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserChannelRepository extends JpaRepository<UserChannel, Long> {

    Optional<UserChannel> findByUserAndDeviceCode(User user, String deviceCode);

    List<UserChannel> findByUser(User user);

    @Modifying
    @Transactional
    @Query("UPDATE UserChannel SET latestActiveTime = CURRENT_TIMESTAMP WHERE user = :user AND latestSessionId = :sessionId")
    void updateLatestActiveTime(@Param("user") User user, @Param("sessionId") String sessionId);
}
