package com.foxconn.fii.main.data.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AddMemberRequest {

    private long groupId;

    private List<Long> memberIdList = new ArrayList<>();
}
