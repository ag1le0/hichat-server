package com.foxconn.fii.main.data.repository;

import com.foxconn.fii.main.data.entity.Group;
import com.foxconn.fii.main.data.entity.GroupMessage;
import com.foxconn.fii.main.data.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {

    @Query("SELECT gm FROM GroupMessage gm WHERE gm.group = :group ORDER BY gm.id DESC")
    Page<GroupMessage> findByGroup(@Param("group") Group group, Pageable pageable);

    @Query("SELECT gm FROM GroupMessage gm WHERE gm.group = :group AND gm.id <= :latestId ORDER BY gm.id DESC")
    Page<GroupMessage> findByGroupAndLatestId(@Param("group") Group group, @Param("latestId") long latestId, Pageable pageable);

    @Query("SELECT gm " +
            "FROM GroupMessage gm, GroupUserFollow guf " +
            "WHERE gm.group = guf.group " +
            "AND gm.routingKey LIKE guf.followPattern " +
            "AND gm.group = :group " +
            "AND guf.member = :member " +
            "ORDER BY gm.id DESC")
    Page<GroupMessage> findByGroupAndMember(@Param("group") Group group, @Param("member") User member, Pageable pageable);

    @Query("SELECT gm " +
            "FROM GroupMessage gm, GroupUserFollow guf " +
            "WHERE gm.group = guf.group " +
            "AND gm.routingKey LIKE guf.followPattern " +
            "AND gm.group = :group " +
            "AND guf.member = :member " +
            "AND gm.id <= :latestId " +
            "ORDER BY gm.id DESC")
    Page<GroupMessage> findByGroupAndMemberAndLatestId(@Param("group") Group group, @Param("member") User member, @Param("latestId") long latestId, Pageable pageable);



}