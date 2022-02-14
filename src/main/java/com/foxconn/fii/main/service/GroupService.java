package com.foxconn.fii.main.service;

import com.foxconn.fii.main.data.entity.*;
import com.foxconn.fii.main.data.model.*;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface GroupService {

    long countUnreadMessageNumber();

    long countNormalUnreadMessageNumber();

    List<GroupUser> getGroupUserList(User currentUser);

    List<Group> getGroupList(User currentUser);

    Group getGroup(long groupId);

    Group getGroup(long groupId, User currentUser);

    Group createGroup(long friendId);

    Group createGroup(GroupRequest request);

    List<User> getMemberList(long groupId);

    boolean existsByGroupAndMember(Group group, User member);

    void addMember(long groupId, List<Long> userIdList);

    void removeMember(long groupId, List<Long> userIdList);

    Group updateGroup(GroupRequest request);

    void changeOwnerGroup(long groupId, long memberId);

    void leaveGroup(long groupId);

    void removeGroup(long groupId);

    List<GroupMessage> getMessageList(long groupId, Long latestMessageId, int page, int size);

    void readMessage(long groupId);

    GroupMessage getMessage(long messageId);

    GroupMessage sendTextMessage(GroupMessageTextRequest request);

    GroupMessage sendMediaMessage(GroupMessageMediaRequest request);

    Page<GroupMessageMedia> getMediaMessageList(long groupId, int page, int size);

}
