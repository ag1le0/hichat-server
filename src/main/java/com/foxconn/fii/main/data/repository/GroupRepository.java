package com.foxconn.fii.main.data.repository;

import com.foxconn.fii.main.data.entity.Group;
import com.foxconn.fii.main.data.entity.GroupMessage;
import com.foxconn.fii.main.data.entity.Media;
import com.foxconn.fii.main.data.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    Optional<Group> findByUuid(String uuid);

    @Query("SELECT g FROM Group g WHERE (g.owner1 = :owner1 AND g.owner2 = :owner2) OR (g.owner1 = :owner2 AND g.owner2 = :owner1)")
    Optional<Group> findByOwner1AndOwner2(@Param("owner1") User owner1, @Param("owner2") User owner2);

//    @Modifying
//    @Transactional
//    @Query("UPDATE Group SET latestMessage = :latestMessage, messageNumber = messageNumber + 1, updatedAt = CURRENT_TIMESTAMP WHERE id = :id")
//    void updateLatestMessage(@Param("id") Long id, @Param("latestMessage") GroupMessage latestMessage);

    @Modifying
    @Transactional
    @Query("UPDATE Group SET messageNumber = messageNumber + 1, updatedAt = CURRENT_TIMESTAMP WHERE id = :id")
    void updateLatestMessage(@Param("id") Long id);

    @Query("SELECT g FROM Group g WHERE g.type = :type AND g.name LIKE :query")
    Page<Group> findByTypeAndNameLike(Group.Type type, String query, Pageable pageable);
}
