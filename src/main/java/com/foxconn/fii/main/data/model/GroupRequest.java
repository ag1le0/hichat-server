package com.foxconn.fii.main.data.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
public class GroupRequest {

    private long id;

    private String name;

    private MultipartFile avatar;

    private List<Long> memberIdList = new ArrayList<>();

    private String securityCode = "";

    private String followPattern = "";

}
