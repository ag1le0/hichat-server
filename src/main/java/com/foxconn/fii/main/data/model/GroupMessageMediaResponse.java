package com.foxconn.fii.main.data.model;

import com.foxconn.fii.common.utils.BeanUtils;
import com.foxconn.fii.main.data.entity.GroupMessageMedia;
import lombok.Data;

@Data
public class GroupMessageMediaResponse {

    private long id;

    private MediaResponse media;

    public static GroupMessageMediaResponse of(GroupMessageMedia mediaMessage) {
        GroupMessageMediaResponse ins = new GroupMessageMediaResponse();
        BeanUtils.copyPropertiesIgnoreNull(mediaMessage, ins);
        ins.setMedia(MediaResponse.of(mediaMessage.getMedia()));
        return ins;
    }
}
