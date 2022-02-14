package com.foxconn.fii.main.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Entity
@Table(name = "pea_user", schema = "dbo", catalog = "peachat")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "uuid")
    private String uuid;

    /**
     * username is employee id
     */
    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "name")
    private String name;

    @Column(name = "chinese_name")
    private String chineseName;

    @Column(name = "email")
    private String email;

    @ManyToOne
    @JoinColumn(name = "avatar_media_id")
    @Fetch(FetchMode.JOIN)
    private Media avatar;

    @Column(name = "bu")
    private String bu;

    @Column(name = "cft")
    private String cft;

    @Column(name = "factory")
    private String factory;

    @Column(name = "department")
    private String department;

    @Column(name = "title")
    private String title;

    @Column(name = "level")
    private String level;

    @Column(name = "active", columnDefinition = "TINYINT(2)")
    private boolean active = true;

    @Column(name = "pwd_expired_time")
    private Date pwdExpiredTime;

    @Column(name = "card_id")
    private String cardId;

    @Column(name = "ou_code")
    private String ouCode;

    @Column(name = "ou_name")
    private String ouName;

    @Column(name = "upper_ou_code")
    private String upperOuCode;

    @Column(name = "lower_ou_code")
    private String lowerOuCode;

    @Column(name = "all_managers")
    private String allManagers;

    @Column(name = "site_all_managers")
    private String siteAllManagers;

    @Column(name = "bu_all_managers")
    private String buALlManagers;

    @Column(name = "hire_date")
    private Date hireDate;

    @Column(name = "leave_date")
    private Date leaveDate;

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @Transient
    private boolean isFriend;

    @Transient
    private String nickName;

    @Transient
    private Long requestId;

    @Transient
    private Long friendRequestId;

    @Transient
    private String sessionId;

    public String getNickName() {
        if (StringUtils.isEmpty(nickName)) {
            if (StringUtils.isEmpty(name) && StringUtils.isEmpty(chineseName)) {
                return username;
            }

            if (!StringUtils.isEmpty(name) && StringUtils.isEmpty(chineseName)) {
                return name;
            }

            if (StringUtils.isEmpty(name) && !StringUtils.isEmpty(chineseName)) {
                return chineseName;
            }

            return String.format("%s(%s)", name, chineseName);
        }

        return nickName;
    }

    public static User of(long id) {
        User ins = new User();
        ins.setId(id);
        return ins;
    }

    public static User of(Map<String, Object> map) {
        User ins = new User();
        ins.setId(((Number) map.get("id")).longValue());
        ins.setUsername((String) map.get("username"));
        ins.setName((String) map.get("name"));
        ins.setChineseName((String) map.get("chinese_name"));

        if (map.get("avatar_media_id") != null) {
            Media avatar = new Media();
            avatar.setId(((Number) map.get("avatar_media_id")).longValue());
            avatar.setUrl((String) map.get("avatar_media_url"));
            avatar.setThumbUrl((String) map.get("avatar_media_thumb_url"));
            ins.setAvatar(avatar);
        }

        ins.setEmail((String) map.get("email"));
        ins.setBu((String) map.get("bu"));
        ins.setFactory((String) map.get("factory"));
        ins.setCft((String) map.get("cft"));
        ins.setDepartment((String) map.get("department"));
        ins.setTitle((String) map.get("title"));
        ins.setLevel((String) map.get("level"));
        ins.setFriend(map.get("friend_id") != null);
        ins.setNickName((String) map.get("friend_nick_name"));

        if (map.get("request_id") != null) {
            ins.setRequestId(((Number) map.get("request_id")).longValue());
        }

        if (map.get("friend_request_id") != null) {
            ins.setFriendRequestId(((Number) map.get("friend_request_id")).longValue());
        }
        return ins;
    }
}
