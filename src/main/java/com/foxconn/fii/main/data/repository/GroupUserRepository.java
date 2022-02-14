package com.foxconn.fii.main.data.repository;

import com.foxconn.fii.main.data.entity.Group;
import com.foxconn.fii.main.data.entity.GroupMessage;
import com.foxconn.fii.main.data.entity.GroupUser;
import com.foxconn.fii.main.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, Long> {

    @Query("SELECT gu.group FROM GroupUser gu WHERE gu.member = :member")
    List<Group> findGroupByMember(@Param("member") User user);

    @Query("SELECT gu.member FROM GroupUser gu LEFT JOIN FETCH gu.member.avatar WHERE gu.group = :group")
    List<User> findMemberByGroup(@Param("group") Group group);

    @Query("SELECT gu FROM GroupUser gu JOIN FETCH gu.group WHERE gu.member = :member ORDER BY gu.updatedAt DESC")
    List<GroupUser> findByMember(@Param("member") User user);

    List<GroupUser> findByGroup(Group group);

    boolean existsByGroupAndMember(Group group, User member);

    Optional<GroupUser> findByGroupAndMember(Group group, User member);

//    @Modifying
//    @Transactional
//    @Query(value = "UPDATE pea_group_user SET message_number = (SELECT message_number FROM pea_group g WHERE g.id = :groupId) WHERE group_id = :groupId AND member_user_id = :userId", nativeQuery = true)
//    void updateLatestMessage(@Param("groupId") Long groupId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE GroupUser gu SET gu.unreadMessageNumber = 0, gu.latestSeenTime = CURRENT_TIMESTAMP WHERE gu.group = :group AND gu.member = :member")
    void resetUnreadMessageNumber(@Param("group") Group group, @Param("member") User member);

    @Modifying
    @Transactional
    @Query("UPDATE GroupUser gu SET gu.latestMessage = :latestMessage, gu.unreadMessageNumber = unreadMessageNumber + 1, gu.updatedAt = CURRENT_TIMESTAMP WHERE gu.group = :group")
    void updateLatestMessage(@Param("group") Group group, @Param("latestMessage") GroupMessage latestMessage);

    @Modifying
    @Transactional
//    @Query(value = "UPDATE pea_group_user SET latest_message_id = :latestMessageId, unread_message_number = unread_message_number + 1, updated_at = CURRENT_TIMESTAMP WHERE group_id = :groupId AND (SELECT COUNT(*) FROM pea_group_user_follow guf WHERE guf.group_id = :groupId AND guf.member_user_id = member_user_id AND :latestMessageRoutingKey LIKE guf.follow_pattern) > 0", nativeQuery = true)
    @Query(value = "UPDATE gu SET latest_message_id = :latestMessageId, unread_message_number = unread_message_number + 1, updated_at = CURRENT_TIMESTAMP FROM pea_group_user gu, pea_group_user_follow guf WHERE gu.group_id = guf.group_id AND gu.member_user_id = guf.member_user_id AND gu.group_id = :groupId AND :latestMessageRoutingKey LIKE guf.follow_pattern", nativeQuery = true)
    void updateLatestMessageOfficial(@Param("groupId") Long groupId, @Param("latestMessageId") Long latestMessageId, @Param("latestMessageRoutingKey") String latestMessageRoutingKey);

    @Query("SELECT SUM(gu.unreadMessageNumber) FROM GroupUser gu WHERE gu.member = :member")
    Long countUnreadMessageNumberByMember(@Param("member") User user);

    @Query("SELECT SUM(gu.unreadMessageNumber) FROM GroupUser gu WHERE gu.member = :member AND gu.group.type != :groupType")
    Long countNormalUnreadMessageNumberByMember(@Param("member") User user, @Param("groupType") Group.Type groupType);

    @Query("SELECT SUM(gu.unreadMessageNumber) FROM GroupUser gu WHERE gu.member = :member AND gu.group.type = :groupType")
    Long countOfficialUnreadMessageNumberByMember(@Param("member") User user, @Param("groupType") Group.Type groupType);
}
