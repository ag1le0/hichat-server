package com.foxconn.fii.main.service.impl;

import com.foxconn.fii.common.exception.CommonException;
import com.foxconn.fii.common.exception.ForbiddenException;
import com.foxconn.fii.common.utils.BeanUtils;
import com.foxconn.fii.main.config.ApplicationConstant;
import com.foxconn.fii.main.data.entity.Group;
import com.foxconn.fii.main.data.entity.GroupUserFollow;
import com.foxconn.fii.main.data.entity.Media;
import com.foxconn.fii.main.data.entity.User;
import com.foxconn.fii.main.data.entity.UserChannel;
import com.foxconn.fii.main.data.entity.UserFriend;
import com.foxconn.fii.main.data.entity.UserFriendRequest;
import com.foxconn.fii.main.data.entity.UserMedia;
import com.foxconn.fii.main.data.model.*;
import com.foxconn.fii.main.data.repository.GroupUserFollowRepository;
import com.foxconn.fii.main.data.repository.UserChannelRepository;
import com.foxconn.fii.main.data.repository.UserFriendRepository;
import com.foxconn.fii.main.data.repository.UserFriendRequestRepository;
import com.foxconn.fii.main.data.repository.UserMediaRepository;
import com.foxconn.fii.main.data.repository.UserRepository;
import com.foxconn.fii.main.service.GroupService;
import com.foxconn.fii.main.service.MediaService;
import com.foxconn.fii.main.service.UserService;
import com.foxconn.fii.rabbitmq.service.RabbitmqService;
import com.foxconn.fii.security.jwt.JwtAuthenticationToken;
import com.foxconn.fii.security.jwt.RawToken;
import com.foxconn.fii.security.model.OAuth2User;
import com.foxconn.fii.security.service.OAuth2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private static final String SOURCE = "pea";

    @Autowired
    private GroupService groupService;

    @Autowired
    private OAuth2Service oauth2Service;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private RabbitmqService rabbitmqService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMediaRepository userMediaRepository;

    @Autowired
    private UserFriendRepository friendRepository;

    @Autowired
    private UserFriendRequestRepository requestRepository;

    @Autowired
    private UserChannelRepository channelRepository;

    @Autowired
    private GroupUserFollowRepository groupUserFollowRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof OAuth2Authentication) {
            OAuth2Authentication principal = (OAuth2Authentication) authentication;
            if (principal.getPrincipal() != null) {
                String username = (String) principal.getPrincipal();

                User user = userRepository.findByUsername(username).orElseGet(() -> {
                    OAuth2User oauth2User = oauth2Service.getCurrentUserInformation();
                    return insertDefaultUser(oauth2User);
                });

                OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
                user.setSessionId(details.getSessionId());

                return user;
            }
        } else if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken principal = (JwtAuthenticationToken) authentication;
            if (principal.getPrincipal() != null) {
                String username = (String) principal.getPrincipal();

                User user = userRepository.findByUsername(username).orElseGet(() -> {
                    OAuth2User oauth2User = oauth2Service.getCurrentUserInformation();
                    return insertDefaultUser(oauth2User);
                });

                user.setSessionId(((RawToken) principal.getCredentials()).getToken());

                return user;
            }
        }

        throw CommonException.of("Get current user not supported {}", authentication);
    }

    private User insertDefaultUser(OAuth2User oauth2User) {
        User ins = new User();
        BeanUtils.copyPropertiesIgnoreNull(oauth2User, ins);
        ins.setUuid(UUID.randomUUID().toString().replace("-", ""));
        userRepository.save(ins);
        return ins;
    }

    @Override
    public User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> CommonException.of("User id {} not found", userId));
    }

    @Override
    public User updateUser(UserRequest request) {
        User currentUser = getCurrentUser();
        BeanUtils.copyPropertiesIgnoreNull(request, currentUser);

        if (request.getAvatar() != null) {
            MediaRequest mediaRequest = new MediaRequest();
            mediaRequest.setFile(request.getAvatar());
            mediaRequest.setType(Media.Type.IMAGE);
            mediaRequest.setPrivacy(Media.Privacy.PUBLIC);
            Media media = mediaService.uploadMedia(mediaRequest);
            currentUser.setAvatar(media);

            UserMedia userMedia = new UserMedia();
            userMedia.setUser(currentUser);
            userMedia.setMedia(media);
            userMediaRepository.save(userMedia);
        }

        userRepository.save(currentUser);
        return currentUser;
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        oauth2Service.changePassword(oldPassword, newPassword);

        User currentUser = getCurrentUser();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 3);
        currentUser.setPwdExpiredTime(calendar.getTime());
        save(currentUser);
    }

    @Override
    public List<User> getFriendList() {
        User user = getCurrentUser();
        List<UserFriend> userFriendList = friendRepository.findByUserId(user.getId());

        return userFriendList.stream().map(uf -> {
            uf.getFriend().setNickName(uf.getFriendNickName());
            return uf.getFriend();
        }).collect(Collectors.toList());
    }

    @Override
    public List<User> searchUser(String query) {
        User currentUser = getCurrentUser();
        String sqlQuery = '%' + query + '%';
        return userRepository.searchUser(currentUser.getId(), sqlQuery).stream().map(User::of).collect(Collectors.toList());
    }

    @Override
    public Integer getAddFriendRequestNumber() {
        User currentUser = getCurrentUser();
        return requestRepository.countByFriendAndStatus(currentUser, UserFriendRequest.Status.SEND);
    }

    @Override
    public List<UserFriendRequest> getAddFriendRequestList() {
        User currentUser = getCurrentUser();
        return requestRepository.findByFriendAndStatus(currentUser, UserFriendRequest.Status.SEND);
    }

    @Override
    public UserFriendRequest createFriendRequest(AddFriendRequest addFriendRequest) {
        User currentUser = getCurrentUser();

        if (currentUser.getId() == addFriendRequest.getFriendId()) {
            throw CommonException.of("You can't add yourself");
        }

        User friend = getUser(addFriendRequest.getFriendId());

        friendRepository.findByUserAndFriend(currentUser, friend)
                .ifPresent((uf -> {
                    throw CommonException.of("You and {} were friends", friend.getNickName());
                }));

        friendRepository.findByUserAndFriend(friend, currentUser)
                .ifPresent((uf -> {
                    throw CommonException.of("You and {} were friends", friend.getNickName());
                }));

        requestRepository.findByUserAndFriend(currentUser, friend)
                .ifPresent((request -> {
                    if (request.getStatus() == UserFriendRequest.Status.SEND) {
                        throw CommonException.of("You have send add friend request", friend.getNickName());
                    }
                }));

        requestRepository.findByUserAndFriend(friend, currentUser)
                .ifPresent((request) -> {
                    if (request.getStatus() == UserFriendRequest.Status.SEND) {
                        throw CommonException.of("{} have send add friend request", friend.getNickName());
                    }
                });

        UserFriendRequest request = requestRepository.findByUserAndFriend(currentUser, friend)
                .orElseGet(() -> {
                    UserFriendRequest ins = new UserFriendRequest();
                    ins.setUser(currentUser);
                    ins.setFriend(friend);
                    ins.setFriendNickName(addFriendRequest.getFriendNickName());
                    ins.setContent(addFriendRequest.getContent());
                    return ins;
                });

        request.setStatus(UserFriendRequest.Status.SEND);
        request = requestRepository.save(request);

        String exchange = String.format("%s.%s", SOURCE, friend.getUuid());
        rabbitmqService.publish(exchange, NotifyResponse.of(request.getUser().getName() + " Has requested to be friend", AddFriendResponse.of(request)));

        return request;
    }

    @Override
    public void acceptFriendRequest(long requestId) {
        User currentUser = getCurrentUser();

        UserFriendRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> CommonException.of("Friend request {} not found", requestId));

        if (request.getFriend().getId() != currentUser.getId() && request.getStatus() == UserFriendRequest.Status.SEND) {
            throw ForbiddenException.of("You don't have permission");
        }

        Optional<UserFriend> userFriendOptional1 = friendRepository.findByUserAndFriend(request.getFriend(), request.getUser());
        if (!userFriendOptional1.isPresent()) {
            UserFriend userFriend = new UserFriend();
            userFriend.setUser(request.getFriend());
            userFriend.setFriend(request.getUser());
            friendRepository.save(userFriend);
        } else {
            throw CommonException.of("You and {} were friends", request.getUser().getNickName());
        }

        Optional<UserFriend> userFriendOptional2 = friendRepository.findByUserAndFriend(request.getUser(), request.getFriend());
        if (!userFriendOptional2.isPresent()) {
            UserFriend userFriend = new UserFriend();
            userFriend.setUser(request.getUser());
            userFriend.setFriend(request.getFriend());
            userFriend.setFriendNickName(request.getFriendNickName());
            friendRepository.save(userFriend);
        } else {
            throw CommonException.of("You and {} were friends", request.getUser().getNickName());
        }

        request.setStatus(UserFriendRequest.Status.ACCEPT);

        String exchange = String.format("%s.%s", SOURCE, request.getUser().getUuid());
        rabbitmqService.publish(exchange, NotifyResponse.of(request.getFriend().getName() + " Has agreed to be friend", UserSnapshotResponse.of(request.getFriend())));
        requestRepository.save(request);
    }

    @Override
    public void rejectFriendRequest(long requestId) {
        User currentUser = getCurrentUser();

        UserFriendRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> CommonException.of("Friend request {} not found", requestId));

        if (request.getFriend().getId() != currentUser.getId() && request.getStatus() == UserFriendRequest.Status.SEND) {
            throw ForbiddenException.of("You don't have permission");
        }

        request.setStatus(UserFriendRequest.Status.REJECT);
        requestRepository.save(request);
    }

    @Override
    public void changeNickNameFriend(long friendId, String friendNickName) {
        User currentUser = getCurrentUser();
        User friend = getUser(friendId);
        UserFriend userFriend = friendRepository.findByUserAndFriend(currentUser, friend)
                .orElseThrow(() -> CommonException.of("You and {} are not friends", friend.getNickName()));
        userFriend.setFriendNickName(friendNickName);
        friendRepository.save(userFriend);
    }

    @Override
    public void removeFriend(long friendId) {
        User currentUser = getCurrentUser();
        User friend = getUser(friendId);
        friendRepository.deleteByUserAndFriend(currentUser, friend);
    }

    @Override
    public Optional<UserChannel> getChannel(User user, String deviceCode) {
        return channelRepository.findByUserAndDeviceCode(user, deviceCode);
    }

    @Override
    public UserChannel saveChannel(UserChannel channel) {
        return channelRepository.save(channel);
    }

    @Override
    public List<UserChannel> getChannels(User user) {
        return channelRepository.findByUser(user);
    }

    @Override
    public UserChannel registerChannel(String deviceCode, String fcmToken) {
        if (StringUtils.isEmpty(deviceCode)) {
            throw CommonException.of("Register channel error device code is blank");
        }

        User currentUser = getCurrentUser();

        Optional<UserChannel> channelOptional = channelRepository.findByUserAndDeviceCode(currentUser, deviceCode);
        if (channelOptional.isPresent()) {
            channelOptional.get().setFcmToken(fcmToken);
            channelOptional.get().setLatestActiveTime(new Date());
            channelRepository.save(channelOptional.get());

            return channelOptional.get();
        }

        String messageChannelName = String.format("%s.%s.%s", SOURCE, currentUser.getUuid(), deviceCode);
        Queue messageChannel;
        if (rabbitmqService.getQueueProperties(messageChannelName) == null) {
            messageChannel = rabbitmqService.createQueue(messageChannelName);
        } else {
            messageChannel = rabbitmqService.buildQueue(messageChannelName);
        }

        String notifyChannelName = String.format("%s.%s.%s.notify", SOURCE, currentUser.getUuid(), deviceCode);
        Queue notifyChannel;
        if (rabbitmqService.getQueueProperties(notifyChannelName) == null) {
            notifyChannel = rabbitmqService.createQueue(notifyChannelName);
        } else {
            notifyChannel = rabbitmqService.buildQueue(notifyChannelName);
        }

        List<Group> groupList = groupService.getGroupList(currentUser);
        for (Group group : groupList) {
            String exchange = String.format("%s.%s", SOURCE, group.getUuid());

            if (group.getType() == Group.Type.OFFICIAL) {
                List<GroupUserFollow> followList = groupUserFollowRepository.findByGroupAndMember(group, currentUser);

                rabbitmqService.binding(exchange, messageChannel, currentUser.getUsername());
                rabbitmqService.binding(exchange, notifyChannel, currentUser.getUsername());

                rabbitmqService.binding(exchange, messageChannel, "ALL");
                rabbitmqService.binding(exchange, notifyChannel, "ALL");

                for (GroupUserFollow follow : followList) {
                    if (!StringUtils.isEmpty(follow.getFollowPattern())) {
                        rabbitmqService.binding(exchange, messageChannel, follow.getFollowPattern().replace("%", "*"));
                        rabbitmqService.binding(exchange, notifyChannel, follow.getFollowPattern().replace("%", "*"));
                    }
                }
            } else {
                rabbitmqService.binding(exchange, messageChannel, "#");
                rabbitmqService.binding(exchange, notifyChannel, "#");
            }
        }

        String exchange = String.format("%s.%s", SOURCE, currentUser.getUuid());
        rabbitmqService.createExchange(exchange);

        rabbitmqService.binding(exchange, notifyChannelName, "#");


        UserChannel channel = new UserChannel();
        channel.setMessageChannelName(messageChannelName);
        channel.setNotifyChannelName(notifyChannelName);
        channel.setUser(currentUser);
        channel.setLatestSessionId(currentUser.getSessionId());
        channel.setDeviceCode(deviceCode);
        channel.setFcmToken(fcmToken);
        channel.setLatestActiveTime(new Date());
        channelRepository.save(channel);

        return channel;
    }

    @Override
    public void unregisterChannel(String deviceCode, String fcmToken) {
        User currentUser = getCurrentUser();

        String messageChannelName = String.format("%s.%s.%s", SOURCE, currentUser.getUuid(), deviceCode);
        if (rabbitmqService.getQueueProperties(messageChannelName) != null) {
            rabbitmqService.deleteQueue(messageChannelName);
        }

        String notifyChannelName = String.format("%s.%s.%s.notify", SOURCE, currentUser.getUuid(), deviceCode);
        if (rabbitmqService.getQueueProperties(notifyChannelName) != null) {
            rabbitmqService.deleteQueue(notifyChannelName);
        }
        String exchange = String.format("%s.%s", SOURCE, currentUser.getUuid());
        rabbitmqService.deleteExchange(exchange);

        channelRepository.findByUserAndDeviceCode(currentUser, deviceCode)
                .ifPresent(channel -> channelRepository.save(channel));
    }

    @Override
    public void heartbeatChannel() {
        User currentUser = getCurrentUser();
        channelRepository.updateLatestActiveTime(currentUser, currentUser.getSessionId());
    }

    @Override
    public List<User> findByLevel(String level) {
        return userRepository.findByLevel(level);
    }
}
