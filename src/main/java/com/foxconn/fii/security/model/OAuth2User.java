package com.foxconn.fii.security.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
public class OAuth2User {

    private Integer id;

    private String username;

    private String name;

    private String chineseName;

    private String email;

    private String bu;

    private String cft;

    private String factory;

    private String department;

    private String title;

    private String level;

    private Date pwdExpiredTime;

    private String cardId;

    private String ouCode;

    private String ouName;

    private String upperOuCode;

    private String allManagers;

    private String siteAllManagers;

    private String buALlManagers;

    private Date hireDate;

    private Date leaveDate;
}
