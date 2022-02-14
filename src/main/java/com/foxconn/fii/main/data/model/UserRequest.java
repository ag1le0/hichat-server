package com.foxconn.fii.main.data.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserRequest {

    private String name;

    private String chineseName;

    private String nickName;

    private String email;

    private MultipartFile avatar;

    private String bu;

    private String cft;

    private String factory;

    private String department;

    private String title;

    private String level;

}
