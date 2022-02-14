package com.foxconn.fii.main.controller;

import com.foxconn.fii.common.response.CommonResponse;
import com.foxconn.fii.common.response.ListResponse;
import com.foxconn.fii.main.data.entity.*;
import com.foxconn.fii.main.data.model.*;
import com.foxconn.fii.main.service.GroupService;
import com.foxconn.fii.main.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/group")
public class ApiGroupController {

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @GetMapping("/unread-message-number")
    public CommonResponse<Long> getUnreadMessageNumber() {
        long unreadMessageNumber = groupService.countUnreadMessageNumber();
        return CommonResponse.success(unreadMessageNumber);
    }

    @GetMapping("/normal/unread-message-number")
    public CommonResponse<Long> getNormalUnreadMessageNumber() {
        long unreadMessageNumber = groupService.countNormalUnreadMessageNumber();
        return CommonResponse.success(unreadMessageNumber);
    }

    @GetMapping("/list")
    public ListResponse<GroupSnapshotResponse> getGroupList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size) {
        User currentUser = userService.getCurrentUser();

        List<GroupSnapshotResponse> groupList = groupService.getGroupUserList(currentUser)
                .stream().map(gu -> {
                    Group group = gu.getGroup();
                    group.setCurrentUser(currentUser);
                    GroupSnapshotResponse ins = GroupSnapshotResponse.of(group);
//                    ins.setMessageNumber(group.getMessageNumber() >= gu.getMessageNumber() ? group.getMessageNumber() - gu.getMessageNumber() : 0);
                    ins.setUnreadMessageNumber(gu.getUnreadMessageNumber());
                    if (gu.getLatestMessage() != null) {
                        ins.setLatestMessage(GroupMessageResponse.of(gu.getLatestMessage(), false));
                    }
                    return ins;
                }).collect(Collectors.toList());

        return ListResponse.success(groupList);
    }

    @PostMapping("/create-friend")
    public CommonResponse<GroupDetailResponse> createGroup(@RequestParam long friendId) {
        Group group = groupService.createGroup(friendId);
        return CommonResponse.success(GroupDetailResponse.of(group));
    }

    @PostMapping("/create-normal")
    public CommonResponse<GroupDetailResponse> createGroup(@ModelAttribute GroupRequest request) {
        Group group = groupService.createGroup(request);
        return CommonResponse.success(GroupDetailResponse.of(group));
    }

    @GetMapping("/{groupId}")
    public CommonResponse<GroupDetailResponse> getGroup(@PathVariable long groupId) {
        Group group = groupService.getGroup(groupId);
        GroupDetailResponse ins = GroupDetailResponse.of(group);
        if (group.getLatestMessage() != null) {
            ins.setLatestMessage(GroupMessageResponse.of(group.getLatestMessage(), false));
        }
        return CommonResponse.success(ins);
    }

    @PostMapping("/add-member")
    public CommonResponse<Boolean> addMember(@ModelAttribute GroupRequest request) {
        groupService.addMember(request.getId(), request.getMemberIdList());
        return CommonResponse.success(true);
    }

    @PostMapping("/remove-member")
    public CommonResponse<Boolean> removeMember(@ModelAttribute GroupRequest request) {
        groupService.removeMember(request.getId(), request.getMemberIdList());
        return CommonResponse.success(true);
    }

    @PostMapping("/leave")
    public CommonResponse<Boolean> leaveGroup(@RequestParam long groupId) {
        groupService.leaveGroup(groupId);
        return CommonResponse.success(true);
    }

    @PostMapping("/update")
    public CommonResponse<GroupDetailResponse> updateGroup(@ModelAttribute GroupRequest request) {
        Group group = groupService.updateGroup(request);
        return CommonResponse.success(GroupDetailResponse.of(group));
    }

    @PostMapping("/change-owner")
    public CommonResponse<Boolean> changeOwnerGroup(@RequestParam long groupId, @RequestParam long memberId) {
        groupService.changeOwnerGroup(groupId, memberId);
        return CommonResponse.success(true);
    }

    @PostMapping("/remove")
    public CommonResponse<Boolean> removeGroup(@RequestParam long groupId) {
        groupService.removeGroup(groupId);
        return CommonResponse.success(true);
    }

    @GetMapping("/message")
    public ListResponse<GroupMessageResponse> getMessageList(
            @RequestParam long groupId,
            @RequestParam Long latestMessageId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size) {
        List<GroupMessageResponse> messageList = groupService.getMessageList(groupId, latestMessageId, page, size)
                .stream().map(GroupMessageResponse::of).collect(Collectors.toList());
        return ListResponse.success(messageList);
    }

    @GetMapping("/message/{messageId}")
    public CommonResponse<GroupMessageResponse> getMessage(@PathVariable long messageId) {
        GroupMessage message = groupService.getMessage(messageId);
        return CommonResponse.success(GroupMessageResponse.of(message));
    }

    @PostMapping("/read-message")
    public CommonResponse<Boolean> readMessage(@RequestParam long groupId) {
        groupService.readMessage(groupId);
        return CommonResponse.success(true);
    }

    @PostMapping("/send-text")
    public CommonResponse<GroupMessageResponse> sendTextMessage(@RequestBody GroupMessageTextRequest request) {
        GroupMessage message = groupService.sendTextMessage(request);
        return CommonResponse.success(GroupMessageResponse.of(message));
    }

    @PostMapping("/send-media")
    public CommonResponse<GroupMessageResponse> sendMediaMessage(@ModelAttribute GroupMessageMediaRequest request) {
        GroupMessage message = groupService.sendMediaMessage(request);
        return CommonResponse.success(GroupMessageResponse.of(message));
    }

    @GetMapping("/media/list")
    public ListResponse<GroupMessageMediaResponse> getMediaMessageList(
            @RequestParam long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size
    ) {
        Page<GroupMessageMedia> mediaPage = groupService.getMediaMessageList(groupId, page, size);
        List<GroupMessageMediaResponse> mediaList = mediaPage
                .stream().map(GroupMessageMediaResponse::of).collect(Collectors.toList());
        return ListResponse.success(mediaList, mediaPage.getTotalElements());
    }

    @GetMapping("/member/list")
    public ListResponse<UserSnapshotResponse> getMemberList(
            @RequestParam long groupId
    ) {
        List<UserSnapshotResponse> memberList = groupService.getMemberList(groupId)
                .stream().map(UserSnapshotResponse::of).collect(Collectors.toList());
        return ListResponse.success(memberList);
    }

}
