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
@Table(name = "pea_user_channel")
public class UserChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "device_code")
    private String deviceCode;

    @Column(name = "message_channel_name")
    private String messageChannelName;

    @Column(name = "notify_channel_name")
    private String notifyChannelName;

    @Column(name = "fcm_token")
    private String fcmToken;

//    @Column(name = "active", columnDefinition = "TINYINT(2)")
//    private boolean active = true;

    @Column(name = "latest_session_id")
    private String latestSessionId;

    @Column(name = "latest_active_time")
    private Date latestActiveTime;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
}
