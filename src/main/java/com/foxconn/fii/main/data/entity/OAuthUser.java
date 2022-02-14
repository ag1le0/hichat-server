package com.foxconn.fii.main.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "oauth_user", schema = "dbo", catalog = "oauth")
public class OAuthUser {

    public static final String DEFAULT_PASSWORD = "Foxconn168!!";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * username is employee id
     */
    @Column(name = "username", unique = true)
    private String username;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "chinese_name")
    private String chineseName;

    @Column(name = "email")
    private String email;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "is_locked", columnDefinition = "TINYINT(1)")
    private boolean locked = false;

    @Column(name = "is_active", columnDefinition = "TINYINT(1)")
    private boolean active = true;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "failed_login_number", columnDefinition = "TINYINT(1)")
    private int failedLoginNumber = 0;

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
    private String buAllManagers;

    @Column(name = "assistant")
    private String assistant;

    @Column(name = "hire_date")
    private Date hireDate;

    @Column(name = "leave_date")
    private Date leaveDate;

    @Column(name = "pwd_expired_time")
    private Date pwdExpiredTime = new Date();

    @Column(name = "otp_code")
    private String otpCode;

    @Column(name = "otp_expired_date")
    private Date otpExpiredDate = new Date();

    @Column(name = "info_expired_date")
    private Date infoExpiredDate = new Date();

    @JsonIgnore
    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;

    @JsonIgnore
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
}
