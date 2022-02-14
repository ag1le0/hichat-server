package com.foxconn.fii.main.data.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "hr_pm_info_user_meta", schema = "dbo", catalog = "uthing")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "emp_no")
    private String employeeId;

    @Column(name = "name_vn")
    private String name;

    @Column(name = "name_cn")
    private String chineseName;

    @Column(name = "NOTES_ID")
    private String email;

    @Column(name = "sex")
    private String sex;

    @Column(name = "JOB_TYPE")
    private String jobType;

    @Column(name = "job_title")
    private String title;

    @Column(name = "USER_LEVEL")
    private Integer level;

    @Column(name = "hire_date")
    private Date hireDate;

    @Column(name = "leave_day")
    private Date leaveDate;

    @Column(name = "CARD_ID")
    private String cardId;

    @Column(name = "location")
    private String location;

    @Column(name = "dept_code")
    private String ouCode;

    @Column(name = "UPPER_OU_CODE")
    private String upperOuCode;

    @Column(name = "all_managers")
    private String allManagers;

    @Column(name = "site_all_managers")
    private String siteAllManagers;

    @Column(name = "BU_ALL_MANAGERS")
    private String buAllManagers;

    @Column(name = "status")
    private int status;

    @Column(name = "shift")
    private String shift;

    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
}
