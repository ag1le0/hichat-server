package com.foxconn.fii.main.data.model;

import com.foxconn.fii.common.utils.BeanUtils;
import com.foxconn.fii.main.data.entity.GroupMessage;
import lombok.Data;

import java.util.Date;

@Data
public class GroupMessageResponse {

    private long id;

    private String uuid;

    private GroupSnapshotResponse group;

    private UserSnapshotResponse author;

    private GroupMessage.Type type;

    private Date publishedTime;

    private GroupMessageTextResponse textMessage;

    private GroupMessageMediaResponse mediaMessage;


    private GroupMessageResponse replyMessage;

    public static GroupMessageResponse of(GroupMessage message) {
        return of(message, true, true);
    }

    public static GroupMessageResponse of(GroupMessage message, boolean includeGroup) {
        return of(message, includeGroup, true);
    }

    public static GroupMessageResponse of(GroupMessage message, boolean includeGroup, boolean includeReply) {
        GroupMessageResponse ins = new GroupMessageResponse();
        BeanUtils.copyPropertiesIgnoreNull(message, ins);

        if (message.getGroup() != null && includeGroup) {
            ins.setGroup(GroupSnapshotResponse.of(message.getGroup(), false));
        }

        if (message.getAuthor() != null) {
            ins.setAuthor(UserSnapshotResponse.of(message.getAuthor()));
        }

        if (message.getTextMessage() != null) {
            ins.setTextMessage(GroupMessageTextResponse.of(message.getTextMessage()));
        }
        if (message.getMediaMessage() != null) {
            ins.setMediaMessage(GroupMessageMediaResponse.of(message.getMediaMessage()));
        }

        if(message.getReplyMessage() != null && includeReply) {
            ins.setReplyMessage(of(message.getReplyMessage(), false, false));
        }

        return ins;
    }
}
