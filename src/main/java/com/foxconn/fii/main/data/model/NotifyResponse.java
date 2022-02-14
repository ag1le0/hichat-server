package com.foxconn.fii.main.data.model;

import com.foxconn.fii.common.utils.BeanUtils;
import com.foxconn.fii.main.data.entity.UserFriendRequest;
import lombok.Data;
import lombok.SneakyThrows;

@Data
public class NotifyResponse<T> {
    private T body;

    private String title;

    public static <T> NotifyResponse of(String title, T src) {
        NotifyResponse ins = new NotifyResponse();
        //BeanUtils.copyPropertiesIgnoreNull(src, body);
        ins.setTitle(title);
        ins.setBody(src);
        return ins;
    }
}
