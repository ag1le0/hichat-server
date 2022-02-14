package com.foxconn.fii.main.data.model;

import com.foxconn.fii.common.utils.BeanUtils;
import com.foxconn.fii.main.data.entity.Group;
import com.foxconn.fii.main.data.entity.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class GroupDetailResponse {

    private long id;

    private String name;

    private String uuid;

    private Group.Type type;

    private MediaResponse avatar;

    private UserSnapshotResponse owner;

    private int memberNumber = 0;

    private String followPattern;

    private long messageNumber = 0;

    private long unreadMessageNumber = 0;

    private GroupMessageResponse latestMessage;

    private Date latestSeenTime;

    public static GroupDetailResponse of(Group group) {
        return of(group, true);
    }

    public static GroupDetailResponse of(Group group, boolean includeLatestMessage) {
        GroupDetailResponse ins = new GroupDetailResponse();
        BeanUtils.copyPropertiesIgnoreNull(group, ins);

        if (group.getOwner1() != null) {
            ins.setOwner(UserSnapshotResponse.of(group.getOwner1()));
        }

//        if (group.getMemberList() != null) {
//            ins.setMemberNumber(group.getMemberList().size());
//        }

        if (group.getAvatar() != null) {
            ins.setAvatar(MediaResponse.of(group.getAvatar()));
        }

        if (group.getType() == Group.Type.FRIEND && group.getCurrentUser() != null) {
            if (group.getOwner1().getId() == group.getCurrentUser().getId()) {
                ins.setName(group.getOwner2().getNickName());
                if (group.getOwner2().getAvatar() != null) {
                    ins.setAvatar(MediaResponse.of(group.getOwner2().getAvatar()));
                }
            } else {
                ins.setName(group.getOwner1().getNickName());
                if (group.getOwner1().getAvatar() != null) {
                    ins.setAvatar(MediaResponse.of(group.getOwner1().getAvatar()));
                }
            }
        }

//        if (group.getLatestMessage() != null && includeLatestMessage) {
//            ins.setLatestMessage(GroupMessageResponse.of(group.getLatestMessage(), false));
//        }

        return ins;
    }

}
