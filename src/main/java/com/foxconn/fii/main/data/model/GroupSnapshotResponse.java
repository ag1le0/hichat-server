package com.foxconn.fii.main.data.model;

import com.foxconn.fii.common.utils.BeanUtils;
import com.foxconn.fii.main.data.entity.Group;
import lombok.Data;

@Data
public class GroupSnapshotResponse {

    private long id;

    private String name;

    private String uuid;

    private Group.Type type;

    private MediaResponse avatar;

    private long unreadMessageNumber = 0;

    private GroupMessageResponse latestMessage;

    private long messageNumber = 0;

    private boolean followed;

    public static GroupSnapshotResponse of(Group group) {
        return of(group, true);
    }

    public static GroupSnapshotResponse of(Group group, boolean includeLatestMessage) {
        GroupSnapshotResponse ins = new GroupSnapshotResponse();
        BeanUtils.copyPropertiesIgnoreNull(group, ins);

//        if (group.getLatestMessage() != null && includeLatestMessage) {
//            ins.setLatestMessage(GroupMessageResponse.of(group.getLatestMessage(), false));
//        }

        if (group.getAvatar() != null) {
            ins.setAvatar(MediaResponse.of(group.getAvatar()));
        }

        if (group.getType() == Group.Type.FRIEND) {
            if (group.getCurrentUser() != null && group.getCurrentUser().getId() == group.getOwner1().getId()) {
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

        return ins;
    }
}
