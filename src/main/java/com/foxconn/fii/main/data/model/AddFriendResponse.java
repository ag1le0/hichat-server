package com.foxconn.fii.main.data.model;

import com.foxconn.fii.common.utils.BeanUtils;
import com.foxconn.fii.main.data.entity.UserFriendRequest;
import lombok.Data;

@Data
public class AddFriendResponse {

    private long id;

    private UserSnapshotResponse user;

    private UserSnapshotResponse friend;

    private String friendNickName;

    private String content;

    public static AddFriendResponse of(UserFriendRequest src) {
        AddFriendResponse ins = new AddFriendResponse();
        BeanUtils.copyPropertiesIgnoreNull(src, ins);
        ins.setUser(UserSnapshotResponse.of(src.getUser()));
        ins.setFriend(UserSnapshotResponse.of(src.getFriend()));
        return ins;
    }
}
