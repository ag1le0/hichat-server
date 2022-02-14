package com.foxconn.fii.main.service;

import com.foxconn.fii.main.data.entity.User;
import com.foxconn.fii.main.data.entity.UserChannel;
import com.foxconn.fii.main.data.entity.UserFriendRequest;
import com.foxconn.fii.main.data.model.AddFriendRequest;
import com.foxconn.fii.main.data.model.UserRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User save(User user);

    Optional<User> findUserByUsername(String username);

    User getCurrentUser();

    User getUser(long userId);

    User updateUser(UserRequest request);

    void changePassword(String oldPassword, String newPassword);

    List<User> getFriendList();

    List<User> searchUser(String query);

    Integer getAddFriendRequestNumber();

    List<UserFriendRequest> getAddFriendRequestList();

    UserFriendRequest createFriendRequest(AddFriendRequest addFriendRequest);

    void acceptFriendRequest(long requestId);

    void rejectFriendRequest(long requestId);

    void changeNickNameFriend(long friendId, String friendNickName);

    void removeFriend(long friendId);

    Optional<UserChannel> getChannel(User user, String deviceCode);

    UserChannel saveChannel(UserChannel channel);

    List<UserChannel> getChannels(User user);

    UserChannel registerChannel(String deviceCode, String fcmToken);

    void unregisterChannel(String deviceCode, String fcmToken);

    void heartbeatChannel();

    List<User> findByLevel(String level);
}
