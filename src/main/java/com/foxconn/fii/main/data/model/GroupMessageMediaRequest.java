package com.foxconn.fii.main.data.model;

import com.foxconn.fii.main.data.entity.Media;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class GroupMessageMediaRequest {

    private String uuid;

    private String securityCode;

    private String routingKey;

    private long groupId;

    private Media.Type type;

    private MultipartFile media;

    private Long replyMessageId;
}
