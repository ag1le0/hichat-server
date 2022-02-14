package com.foxconn.fii.main.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "pea_group_message")
public class GroupMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "uuid")
    private String uuid;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "group_id")
    @Fetch(FetchMode.JOIN)
    private Group group;

    @ManyToOne
    @JoinColumn(name = "author_user_id")
    @Fetch(FetchMode.JOIN)
    private User author;

    @Column(name = "type")
    private Type type;

    @CreationTimestamp
    @Column(name = "published_time")
    private Date publishedTime;

    @Column(name = "routing_key")
    private String routingKey;

    @ManyToOne/*(fetch = FetchType.LAZY)*/
    @JoinColumn(name = "group_message_text_id")
    private GroupMessageText textMessage;

    @ManyToOne/*(fetch = FetchType.LAZY)*/
    @JoinColumn(name = "group_message_media_id")
    private GroupMessageMedia mediaMessage;

    @ManyToOne/*(fetch = FetchType.LAZY)*/
    @JoinColumn(name = "reply_group_message_id")
    private GroupMessage replyMessage;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    public enum Type {
        TEXT,
        MEDIA,
        HIGHLIGHT,
        REPORT,
        ALARM
    }

    public static GroupMessage of(long id) {
        GroupMessage ins = new GroupMessage();
        ins.setId(id);
        return ins;
    }
}
