package com.foxconn.fii.main.data.model;

import com.foxconn.fii.common.utils.BeanUtils;
import com.foxconn.fii.main.data.entity.Media;
import com.foxconn.fii.main.data.entity.UserChannel;
import lombok.Data;

@Data
public class UserChannelResponse {

    private String messageChannelName;

    private String notifyChannelName;

    public static UserChannelResponse of (UserChannel src){
        UserChannelResponse ins = new UserChannelResponse();
        BeanUtils.copyPropertiesIgnoreNull(src, ins);
        return ins;
    }
}
