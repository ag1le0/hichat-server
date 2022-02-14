package com.foxconn.fii.main.data.model;

import lombok.Data;

import java.util.List;

@Data
public class GroupMessageTextRequest {

    private String uuid;

    private String securityCode;

    private String routingKey;

    private long groupId;

    private String text;

    private Long replyMessageId;
}
