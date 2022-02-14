package com.foxconn.fii.main.data.repository;

import com.foxconn.fii.main.data.entity.Group;
import com.foxconn.fii.main.data.entity.GroupUserFollow;
import com.foxconn.fii.main.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Member;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupUserFollowRepository extends JpaRepository<GroupUserFollow, Long> {

    List<GroupUserFollow> findByGroupAndMember(Group group, User member);

    List<GroupUserFollow> findByGroupAndMemberAndBuAndFactory(Group group, User member, String bu, String factory);

    Optional<GroupUserFollow> findByGroupAndMemberAndFollowPattern(Group group, User member, String followPattern);

    @Query("SELECT DISTINCT guf.bu " +
            "FROM GroupUserFollow guf " +
            "WHERE guf.group = :group AND guf.member = :member AND guf.bu IS NOT NULL")
    List<String> findBuByGroupAndMember(Group group, User member);

    @Query("SELECT DISTINCT guf.factory " +
            "FROM GroupUserFollow guf " +
            "WHERE guf.group = :group AND guf.member = :member AND guf.bu = :bu AND guf.factory IS NOT NULL")
    List<String> findFactoryByGroupAndMember(Group group, User member, String bu);

    @Query("SELECT DISTINCT guf.cft " +
            "FROM GroupUserFollow guf " +
            "WHERE guf.group = :group AND guf.member = :member AND guf.bu = :bu AND guf.factory = :factory AND guf.cft IS NOT NULL")
    List<String> findCftByGroupAndMember(Group group, User member, String bu, String factory);

    @Query("SELECT DISTINCT guf.stage " +
            "FROM GroupUserFollow guf " +
            "WHERE guf.group = :group AND guf.member = :member AND guf.bu = :bu AND guf.factory = :factory AND guf.stage IS NOT NULL")
    List<String> findStageByGroupAndMember(Group group, User member, String bu, String factory);

    @Query("SELECT DISTINCT guf.floor " +
            "FROM GroupUserFollow guf " +
            "WHERE guf.group = :group AND guf.member = :member AND guf.bu = :bu AND guf.factory = :factory AND guf.floor IS NOT NULL")
    List<String> findFloorByGroupAndMember(Group group, User member, String bu, String factory);

    @Query("SELECT DISTINCT guf.team " +
            "FROM GroupUserFollow guf " +
            "WHERE guf.group = :group AND guf.member = :member AND guf.bu = :bu AND guf.factory = :factory AND guf.cft = :cft AND guf.team IS NOT NULL")
    List<String> findTeamByGroupAndMember(Group group, User member, String bu, String factory, String cft);

    @Query("SELECT DISTINCT guf.model " +
            "FROM GroupUserFollow guf " +
            "WHERE guf.group = :group AND guf.member = :member AND guf.bu = :bu AND guf.factory = :factory AND guf.cft = :cft AND guf.model IS NOT NULL")
    List<String> findModelByGroupAndMember(Group group, User member, String bu, String factory, String cft);

    @Query("SELECT DISTINCT guf.line " +
            "FROM GroupUserFollow guf " +
            "WHERE guf.group = :group AND guf.member = :member AND guf.bu = :bu AND guf.factory = :factory AND guf.stage = :stage AND guf.floor = :floor AND guf.line IS NOT NULL")
    List<String> findLineByGroupAndMember(Group group, User member, String bu, String factory, String stage, String floor);

    @Query("SELECT DISTINCT guf.station " +
            "FROM GroupUserFollow guf " +
            "WHERE guf.group = :group AND guf.member = :member AND guf.bu = :bu AND guf.factory = :factory AND guf.stage = :stage AND guf.floor = :floor AND guf.line = :line AND guf.station IS NOT NULL")
    List<String> findStationByGroupAndMember(Group group, User member, String bu, String factory, String stage, String floor, String line);
}
