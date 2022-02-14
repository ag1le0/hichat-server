package com.foxconn.fii.main.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "pea_group_user_follow")
public class GroupUserFollow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_user_id")
    private User member;

    @Column(name = "follow_pattern")
    private String followPattern;

    @Column(name = "bu")
    private String bu;

    @Column(name = "factory")
    private String factory;

    @Column(name = "cft")
    private String cft;

    @Column(name = "stage")
    private String stage;

    @Column(name = "team")
    private String team;

    @Column(name = "floor")
    private String floor;

    @Column(name = "line")
    private String line;

    @Column(name = "station")
    private String station;

    @Column(name = "model")
    private String model;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    public static GroupUserFollow of(long id) {
        GroupUserFollow ins = new GroupUserFollow();
        ins.setId(id);
        return ins;
    }
}
