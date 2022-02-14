package com.foxconn.fii.main.data.model;

import lombok.Data;

@Data
public class AddFriendRequest {

    private long friendId;

    private String friendNickName;

    private String content;
}
