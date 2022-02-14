package com.foxconn.fii.main.data.model;

import com.foxconn.fii.common.utils.BeanUtils;
import com.foxconn.fii.main.data.entity.GroupMessageText;
import lombok.Data;

@Data
public class GroupMessageTextResponse {

    private long id;

    private String text;

    public static GroupMessageTextResponse of(GroupMessageText textMessage) {
        GroupMessageTextResponse ins = new GroupMessageTextResponse();
        BeanUtils.copyPropertiesIgnoreNull(textMessage, ins);
        return ins;
    }
}
