package com.foxconn.fii.main.data.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Entity
@Table(name = "pea_group")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "avatar_media_id")
    @Fetch(FetchMode.JOIN)
    private Media avatar;

    @Column(name = "type")
    private Type type;

    @ManyToOne
    @JoinColumn(name = "owner1_user_id")
    private User owner1;

    @ManyToOne
    @JoinColumn(name = "owner2_user_id")
    private User owner2;

//    @JsonBackReference
//    @ManyToOne
//    @JoinColumn(name = "pinned_message_id")
//    private GroupMessage pinnedMessage;

    @JsonBackReference
    @ManyToMany
    @JoinTable(
            name = "pea_group_user",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "member_user_id"))
    List<User> memberList;

//    @JsonBackReference
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "latest_message_id")
//    private GroupMessage latestMessage;

    @Column(name = "member_number")
    private int memberNumber = 0;

    @Column(name = "message_number")
    private long messageNumber = 0;

    @Column(name = "active", columnDefinition = "TINYINT(2)")
    private boolean active = true;

    @Column(name = "security_code")
    private String securityCode;

    @Column(name = "follow_pattern")
    private String followPattern;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @Transient
    private User currentUser;

    @Transient
    private Date latestSeenTime;

    @Transient
    private long unreadMessageNumber;

    @Transient
    private GroupMessage latestMessage;

    @Transient
    private boolean followed = false;

    public enum Type {
        FRIEND,
        NORMAL,
        OFFICIAL
    }

    public static Group of(long id) {
        Group ins = new Group();
        ins.setId(id);
        return ins;
    }

}
