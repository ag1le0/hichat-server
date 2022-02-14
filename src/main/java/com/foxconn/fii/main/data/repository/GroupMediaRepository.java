package com.foxconn.fii.main.data.repository;

import com.foxconn.fii.main.data.entity.Group;
import com.foxconn.fii.main.data.entity.GroupMedia;
import com.foxconn.fii.main.data.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMediaRepository extends JpaRepository<GroupMedia, Long> {

    boolean existsByGroupAndMedia(Group group, Media media);
}
