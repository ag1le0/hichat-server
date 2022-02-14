package com.foxconn.fii.main.data.model;

import com.foxconn.fii.common.utils.BeanUtils;
import com.foxconn.fii.main.data.entity.GroupUserFollow;
import lombok.Data;

import java.util.Date;

@Data
public class GroupUserFollowResponse {

    private long id;

    private String followPattern;

    private Date createdAt;

    public static GroupUserFollowResponse of(GroupUserFollow src) {
        GroupUserFollowResponse ins = new GroupUserFollowResponse();
        BeanUtils.copyPropertiesIgnoreNull(src, ins);
        return ins;
    }
}
