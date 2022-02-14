package com.foxconn.fii.main.data.repository;

import com.foxconn.fii.main.data.entity.GroupMessageText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMessageTextRepository extends JpaRepository<GroupMessageText, Long> {

}
