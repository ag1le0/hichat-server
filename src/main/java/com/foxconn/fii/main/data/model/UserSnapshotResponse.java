package com.foxconn.fii.main.data.model;

import com.foxconn.fii.common.utils.BeanUtils;
import com.foxconn.fii.main.data.entity.User;
import lombok.Data;

@Data
public class UserSnapshotResponse {

    private long id;

    private String username;

    private String name;

    private String chineseName;

    private String nickName;

    private String callNumber;

    private MediaResponse avatar;

    private boolean isFriend;

    private Long requestId;

    private Long friendRequestId;

    public static UserSnapshotResponse of(User src) {
        UserSnapshotResponse ins = new UserSnapshotResponse();
        BeanUtils.copyPropertiesIgnoreNull(src, ins);
        ins.setNickName(src.getNickName());


        if (src.getAvatar() != null) {
            ins.setAvatar(MediaResponse.of(src.getAvatar()));
        }

        return ins;
    }
}
