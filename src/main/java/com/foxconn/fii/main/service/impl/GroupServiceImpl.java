package com.foxconn.fii.main.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foxconn.fii.common.TimeSpan;
import com.foxconn.fii.common.exception.CommonException;
import com.foxconn.fii.common.exception.ForbiddenException;
import com.foxconn.fii.common.exception.NotFoundException;
import com.foxconn.fii.common.utils.BeanUtils;
import com.foxconn.fii.common.utils.CommonUtils;
import com.foxconn.fii.common.utils.TestUtils;
import com.foxconn.fii.main.config.ApplicationConstant;
import com.foxconn.fii.main.data.entity.*;
import com.foxconn.fii.main.data.model.*;
import com.foxconn.fii.main.data.repository.*;
import com.foxconn.fii.main.service.GroupService;
import com.foxconn.fii.main.service.MediaService;
import com.foxconn.fii.main.service.UserService;
import com.foxconn.fii.rabbitmq.service.RabbitmqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GroupServiceImpl implements GroupService {

    private static final String SOURCE = "pea";

    @Autowired
    private UserService userService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private RabbitmqService rabbitmqService;


    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMediaRepository groupMediaRepository;

    @Autowired
    private GroupUserRepository groupUserRepository;

    @Autowired
    private GroupUserFollowRepository groupUserFollowRepository;

    @Autowired
    private GroupMessageRepository groupMessageRepository;

    @Autowired
    private GroupMessageTextRepository groupMessageTextRepository;

    @Autowired
    private GroupMessageMediaRepository groupMessageMediaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public long countUnreadMessageNumber() {
        User currentUser = userService.getCurrentUser();
        Long number = groupUserRepository.countUnreadMessageNumberByMember(currentUser);
        return number != null ? number : 0;
    }

    @Override
    public long countNormalUnreadMessageNumber() {
        User currentUser = userService.getCurrentUser();
        Long number = groupUserRepository.countNormalUnreadMessageNumberByMember(currentUser, Group.Type.OFFICIAL);
        return number != null ? number : 0;
    }



    @Override
    public List<GroupUser> getGroupUserList(User currentUser) {
        return groupUserRepository.findByMember(currentUser)
                .stream().filter(gu -> gu.getGroup().getType() != Group.Type.OFFICIAL)
                .collect(Collectors.toList());
    }


    @Override
    public List<Group> getGroupList(User currentUser) {
        return groupUserRepository.findGroupByMember(currentUser);
    }


    @Override
    public Group getGroup(long groupId) {
        User currentUser = userService.getCurrentUser();
        Group group = getGroup(groupId, currentUser);

//        List<User> memberList = groupUserRepository.findMemberByGroup(group);
//        group.setMemberList(memberList);

        groupUserRepository.findByGroupAndMember(group, currentUser)
                .ifPresent(gu -> {
                    group.setUnreadMessageNumber(gu.getUnreadMessageNumber());
                    group.setLatestMessage(gu.getLatestMessage());
                    group.setLatestSeenTime(gu.getLatestSeenTime());
                });

        return group;
    }

    @Override
    public Group getGroup(long groupId, User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> CommonException.of("Group {} is not found", groupId));

        if (!group.isActive()) {
            throw CommonException.of("Group {} is not active", groupId);
        }

        if (!existsByGroupAndMember(group, currentUser)) {
            throw ForbiddenException.of("You don't have permission");
        }

        group.setCurrentUser(currentUser);

        return group;
    }

    @Override
    public Group createGroup(long friendId) {
        User currentUser = userService.getCurrentUser();
        User friend = userService.getUser(friendId);

        List<User> memberList = new ArrayList<>();
        Group group = groupRepository.findByOwner1AndOwner2(currentUser, friend).orElseGet(() -> {
            Group ins = new Group();
            ins.setUuid(UUID.randomUUID().toString().replace("-", ""));
            ins.setType(Group.Type.FRIEND);
            ins.setOwner1(currentUser);
            ins.setOwner2(friend);
            memberList.add(currentUser);
            memberList.add(friend);

            List<GroupUser> groupUserList = new ArrayList<>();

            GroupUser groupUser1 = new GroupUser();
            groupUser1.setGroup(ins);
            groupUser1.setMember(currentUser);
            groupUserList.add(groupUser1);

            GroupUser groupUser2 = new GroupUser();
            groupUser2.setGroup(ins);
            groupUser2.setMember(friend);
            groupUserList.add(groupUser2);

            ins.setMemberNumber(groupUserList.size());
            groupRepository.save(ins);
            groupUserRepository.saveAll(groupUserList);

            String exchange = String.format("%s.%s", SOURCE, ins.getUuid());
            rabbitmqService.createExchange(exchange);

            for (User member : memberList) {
                List<UserChannel> currentChannelList = userService.getChannels(member);
                for (UserChannel channel : currentChannelList) {
                    rabbitmqService.binding(exchange, channel.getMessageChannelName(), "#");
                }
            }

            return ins;
        });

        group.setCurrentUser(currentUser);
        group.setMemberList(memberList);
        return group;
    }

    @Override
    public Group createGroup(GroupRequest request) {
        User currentUser = userService.getCurrentUser();

        Group group = new Group();
        group.setUuid(UUID.randomUUID().toString().replace("-", ""));
        group.setName(request.getName());
        group.setType(Group.Type.NORMAL);
        group.setOwner1(currentUser);

        if (request.getAvatar() != null) {
            MediaRequest mediaRequest = new MediaRequest();
            mediaRequest.setFile(request.getAvatar());
            mediaRequest.setType(Media.Type.IMAGE);
            mediaRequest.setPrivacy(Media.Privacy.PUBLIC);
            Media media = mediaService.uploadMedia(mediaRequest);
            group.setAvatar(media);
        }

        List<GroupUser> groupUserList = new ArrayList<>();
        List<User> memberList = new ArrayList<>();

        GroupUser groupUser = new GroupUser();
        groupUser.setGroup(group);
        groupUser.setMember(currentUser);
        groupUserList.add(groupUser);
        memberList.add(currentUser);

        Set<Long> memberIdSet = new HashSet<>(request.getMemberIdList());
        for (Long memberId : memberIdSet) {
            if (memberId != currentUser.getId()) {
                User member = userService.getUser(memberId);
                GroupUser gu = new GroupUser();
                gu.setGroup(group);
                gu.setMember(member);
                groupUserList.add(gu);
                memberList.add(member);
            }
        }

        if (groupUserList.size() == 1) {
            throw CommonException.of("Create group error with empty member");
        }

        group.setMemberNumber(groupUserList.size());
        groupRepository.save(group);
        groupUserRepository.saveAll(groupUserList);

        GroupMedia groupMedia = new GroupMedia();
        groupMedia.setGroup(group);
        groupMedia.setMedia(group.getAvatar());
        groupMediaRepository.save(groupMedia);

        String exchange = String.format("%s.%s", SOURCE, group.getUuid());
        rabbitmqService.createExchange(exchange);

        for (User member : memberList) {
            List<UserChannel> currentChannelList = userService.getChannels(member);
            for (UserChannel channel : currentChannelList) {
                rabbitmqService.binding(exchange, channel.getMessageChannelName(), "#");
            }
        }

        group.setCurrentUser(currentUser);
        group.setMemberList(memberList);
        return group;
    }

    @Override
    public List<User> getMemberList(long groupId) {
        User currentUser = userService.getCurrentUser();
        Group group = getGroup(groupId, currentUser);

        return group.getMemberList();
    }

    @Override
    public boolean existsByGroupAndMember(Group group, User member) {
        return groupUserRepository.existsByGroupAndMember(group, member);
    }

    @Override
    public void addMember(long groupId, List<Long> userIdList) {
        User currentUser = userService.getCurrentUser();
        Group group = getGroup(groupId, currentUser);

        if (group.getType() != Group.Type.NORMAL) {
            throw CommonException.of("Group {} type is not support", groupId);
        }

        List<GroupUser> groupUserList = new ArrayList<>();
        List<User> memberList = new ArrayList<>();
        Set<Long> memberIdSet = new HashSet<>(userIdList);
        for (Long memberId : memberIdSet) {
            if (memberId != currentUser.getId()) {
                User member = userService.getUser(memberId);
                if (existsByGroupAndMember(group, member)) {
                    throw CommonException.of("{} is member of group {}", memberId, groupId);
                }

                GroupUser gu = new GroupUser();
                gu.setGroup(group);
                gu.setMember(member);
                groupUserList.add(gu);
                memberList.add(member);
            }
        }

        if (memberList.isEmpty()) {
            throw CommonException.of("Member list is empty");
        }

        group.setMemberNumber(group.getMemberNumber() + groupUserList.size());
        groupRepository.save(group);
        groupUserRepository.saveAll(groupUserList);

        String exchange = String.format("%s.%s", SOURCE, group.getUuid());
        for (User member : memberList) {
            List<UserChannel> currentChannelList = userService.getChannels(member);
            for (UserChannel channel : currentChannelList) {
                rabbitmqService.binding(exchange, channel.getMessageChannelName(), "#");
            }
        }
    }

    @Override
    public void removeMember(long groupId, List<Long> userIdList) {
        User currentUser = userService.getCurrentUser();
        Group group = getGroup(groupId, currentUser);

        if (group.getType() != Group.Type.NORMAL) {
            throw CommonException.of("Group {} type is not support", groupId);
        }

        if (group.getOwner1().getId() != currentUser.getId()) {
            throw ForbiddenException.of("You don't have permission");
        }

        List<User> memberList = new ArrayList<>();
        Set<Long> memberIdSet = new HashSet<>(userIdList);
        for (Long memberId : memberIdSet) {
            if (memberId != currentUser.getId()) {
                User member = userService.getUser(memberId);
                GroupUser gu = groupUserRepository.findByGroupAndMember(group, member)
                        .orElseThrow(() -> CommonException.of("{} is not member of group {}", memberId, groupId));
                groupUserRepository.delete(gu);
                memberList.add(member);
            }
        }

        if (memberList.isEmpty()) {
            throw CommonException.of("Member list is empty");
        }

        group.setMemberNumber(group.getMemberNumber() - memberList.size());
        groupRepository.save(group);

        String exchange = String.format("%s.%s", SOURCE, group.getUuid());
        for (User member : memberList) {
            List<UserChannel> currentChannelList = userService.getChannels(member);
            for (UserChannel channel : currentChannelList) {
                rabbitmqService.unbinding(exchange, channel.getMessageChannelName(), "#");
            }
        }
    }

    @Override
    public void changeOwnerGroup(long groupId, long memberId) {
        User currentUser = userService.getCurrentUser();
        Group group = getGroup(groupId, currentUser);
        User member = userService.getUser(memberId);

        if (group.getType() != Group.Type.NORMAL) {
            throw CommonException.of("Group {} type is not support", groupId);
        }

        if (currentUser.getId() != group.getOwner1().getId()) {
            throw ForbiddenException.of("You don't have permission");
        }

        if (!existsByGroupAndMember(group, member)) {
            throw CommonException.of("User {} is not member of group {}", memberId, groupId);
        }

        group.setOwner1(member);
        groupRepository.save(group);
    }

    @Override
    public Group updateGroup(GroupRequest request) {
        User currentUser = userService.getCurrentUser();
        Group group = getGroup(request.getId(), currentUser);

//        if (group.getType() != Group.Type.NORMAL) {
        if (group.getType() == Group.Type.FRIEND) {
            throw CommonException.of("Group {} type is not support", request.getId());
        }

        if (!StringUtils.isEmpty(request.getName())) {
            group.setName(request.getName());
        }

        if (request.getAvatar() != null) {
            MediaRequest mediaRequest = new MediaRequest();
            mediaRequest.setFile(request.getAvatar());
            mediaRequest.setType(Media.Type.IMAGE);
            mediaRequest.setPrivacy(Media.Privacy.PUBLIC);
            Media media = mediaService.uploadMedia(mediaRequest);
            group.setAvatar(media);

            GroupMedia groupMedia = new GroupMedia();
            groupMedia.setGroup(group);
            groupMedia.setMedia(media);
            groupMediaRepository.save(groupMedia);
        }

        groupRepository.save(group);

        List<User> memberList = groupUserRepository.findMemberByGroup(group);
        group.setMemberList(memberList);

        return group;
    }

    @Override
    public void leaveGroup(long groupId) {
        User currentUser = userService.getCurrentUser();
        Group group = getGroup(groupId, currentUser);

        if (group.getType() != Group.Type.NORMAL) {
            throw CommonException.of("Group {} type is not support", groupId);
        }

        if (currentUser.getId() == group.getOwner1().getId()) {
            throw CommonException.of("You are owner of group {}", groupId);
        }

        GroupUser gu = groupUserRepository.findByGroupAndMember(group, currentUser)
                .orElseThrow(() -> CommonException.of("{} is not member of group {}", currentUser.getId(), groupId));
        groupUserRepository.delete(gu);

        group.setMemberNumber(group.getMemberNumber() - 1);
        groupRepository.save(group);
    }

    @Override
    public void removeGroup(long groupId) {
        User currentUser = userService.getCurrentUser();
        Group group = getGroup(groupId, currentUser);

        if (group.getType() != Group.Type.NORMAL) {
            throw CommonException.of("Group {} type is not support", groupId);
        }

        if (currentUser.getId() != group.getOwner1().getId()) {
            throw ForbiddenException.of("You don't have permission");
        }

        group.setActive(false);
        groupRepository.save(group);

        List<GroupUser> groupUserList = groupUserRepository.findByGroup(group);
        groupUserRepository.deleteAll(groupUserList);
    }



    @Override
    public List<GroupMessage> getMessageList(long groupId, Long latestMessageId, int page, int size) {
        User currentUser = userService.getCurrentUser();
        Group group = getGroup(groupId, currentUser);

        if (latestMessageId != null) {
            if (group.getType() == Group.Type.OFFICIAL) {
                Page<GroupMessage> messagePage = groupMessageRepository.findByGroupAndMemberAndLatestId(group, currentUser, latestMessageId, PageRequest.of(page, size));
                return messagePage.getContent();
            }
            Page<GroupMessage> messagePage = groupMessageRepository.findByGroupAndLatestId(group, latestMessageId, PageRequest.of(page, size));
            return messagePage.getContent();
        } else {
            if (group.getType() == Group.Type.OFFICIAL) {
                Page<GroupMessage> messagePage = groupMessageRepository.findByGroupAndMember(group, currentUser, PageRequest.of(page, size));
                return messagePage.getContent();
            }
            Page<GroupMessage> messagePage = groupMessageRepository.findByGroup(group, PageRequest.of(page, size));
            return messagePage.getContent();
        }
    }

    @Override
    public void readMessage(long groupId) {
        User currentUser = userService.getCurrentUser();
        Group group = getGroup(groupId, currentUser);

        groupUserRepository.resetUnreadMessageNumber(group, currentUser);
    }

    @Override
    public GroupMessage getMessage(long messageId) {
        return groupMessageRepository.findById(messageId)
                .orElseThrow(() -> NotFoundException.of("Message {} not found", messageId));
    }

    @Override
    public GroupMessage sendTextMessage(GroupMessageTextRequest request) {
        User currentUser = userService.getCurrentUser();
        Group group = getGroup(request.getGroupId(), currentUser);

        if (group.getType() == Group.Type.OFFICIAL) {
            throw CommonException.of("Group {} type is OFFICIAL", request.getGroupId());
        }

        GroupMessageText textMessage = new GroupMessageText();
        textMessage.setGroup(group);
        textMessage.setText(CommonUtils.htmlEscape(request.getText()));
        groupMessageTextRepository.save(textMessage);

        GroupMessage message = new GroupMessage();
        if (StringUtils.isEmpty(request.getUuid())) {
            message.setUuid(UUID.randomUUID().toString().replace("-", ""));
        } else {
            message.setUuid(request.getUuid());
        }
        message.setGroup(group);
        message.setAuthor(currentUser);
        message.setType(GroupMessage.Type.TEXT);
        message.setTextMessage(textMessage);

        if (request.getReplyMessageId() != null) {
            GroupMessage replyMessage = groupMessageRepository.findById(request.getReplyMessageId())
                    .orElseThrow(() -> NotFoundException.of("Message {} is not found", request.getReplyMessageId()));
            message.setReplyMessage(replyMessage);
        }

        groupMessageRepository.save(message);

        groupRepository.updateLatestMessage(group.getId());

        try {
            message.getGroup().setCurrentUser(null);
            rabbitmqService.publish(String.format("%s.%s", SOURCE, group.getUuid()), GroupMessageResponse.of(message));
        } catch (Exception e) {
            log.error("### send text message error", e);
            throw CommonException.of("Send text message error");
        }

        groupUserRepository.updateLatestMessage(group, message);
        groupUserRepository.resetUnreadMessageNumber(group, currentUser);

        return message;
    }


    @Override
    public GroupMessage sendMediaMessage(GroupMessageMediaRequest request) {
        User currentUser = userService.getCurrentUser();
        Group group = getGroup(request.getGroupId(), currentUser);

        if (group.getType() == Group.Type.OFFICIAL) {
            throw CommonException.of("Group {} type is OFFICIAL", request.getGroupId());
        }

        MediaRequest mediaRequest = new MediaRequest();
        mediaRequest.setFile(request.getMedia());
        mediaRequest.setType(request.getType());
        mediaRequest.setPrivacy(Media.Privacy.PROTECTED);
        Media media = mediaService.uploadMedia(mediaRequest);

        GroupMedia groupMedia = new GroupMedia();
        groupMedia.setGroup(group);
        groupMedia.setMedia(media);
        groupMediaRepository.save(groupMedia);

        GroupMessageMedia mediaMessage = new GroupMessageMedia();
        mediaMessage.setGroup(group);
        mediaMessage.setMedia(media);
        groupMessageMediaRepository.save(mediaMessage);

        GroupMessage message = new GroupMessage();
        if (StringUtils.isEmpty(request.getUuid())) {
            message.setUuid(UUID.randomUUID().toString().replace("-", ""));
        } else {
            message.setUuid(request.getUuid());
        }
        message.setGroup(group);
        message.setAuthor(currentUser);
        message.setType(GroupMessage.Type.MEDIA);
        message.setMediaMessage(mediaMessage);

        if (request.getReplyMessageId() != null) {
            GroupMessage replyMessage = groupMessageRepository.findById(request.getReplyMessageId())
                    .orElseThrow(() -> NotFoundException.of("Message {} is not found", request.getReplyMessageId()));
            message.setReplyMessage(replyMessage);
        }

        groupMessageRepository.save(message);

        groupRepository.updateLatestMessage(group.getId());

        try {
            message.getGroup().setCurrentUser(null);
            rabbitmqService.publish(String.format("%s.%s", SOURCE, group.getUuid()), GroupMessageResponse.of(message));
        } catch (Exception e) {
            log.error("### send text message error", e);
            throw CommonException.of("Send text message error");
        }

        groupUserRepository.updateLatestMessage(group, message);
        groupUserRepository.resetUnreadMessageNumber(group, currentUser);

        return message;
    }


    @Override
    public Page<GroupMessageMedia> getMediaMessageList(long groupId, int page, int size) {
        User currentUser = userService.getCurrentUser();
        Group group = getGroup(groupId, currentUser);

        return groupMessageMediaRepository.findByGroup(group, PageRequest.of(page, size));
    }

}
