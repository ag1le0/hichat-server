package com.foxconn.fii.main.data.model;

import com.foxconn.fii.common.utils.BeanUtils;
import com.foxconn.fii.main.data.entity.Media;
import lombok.Data;

@Data
public class MediaResponse {

    private String uuid;

    private Media.Type type;

    private String url;

    private String thumbUrl;

    public static MediaResponse of (Media media){
        MediaResponse ins = new MediaResponse();
        BeanUtils.copyPropertiesIgnoreNull(media, ins);
        return ins;
    }
}
