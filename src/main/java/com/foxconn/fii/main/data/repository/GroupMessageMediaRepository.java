package com.foxconn.fii.main.data.repository;

import com.foxconn.fii.main.data.entity.Group;
import com.foxconn.fii.main.data.entity.GroupMessageMedia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMessageMediaRepository extends JpaRepository<GroupMessageMedia, Long> {

    @Query("SELECT m FROM GroupMessageMedia m WHERE m.group = :group ORDER BY m.id DESC")
    Page<GroupMessageMedia> findByGroup(Group group, Pageable pageable);
}
