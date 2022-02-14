package com.foxconn.fii.main.controller;

import com.foxconn.fii.common.response.CommonResponse;
import com.foxconn.fii.common.response.ListResponse;
import com.foxconn.fii.main.data.entity.User;
import com.foxconn.fii.main.data.entity.UserChannel;
import com.foxconn.fii.main.data.entity.UserFriendRequest;
import com.foxconn.fii.main.data.model.*;
import com.foxconn.fii.main.service.UserService;
import com.foxconn.fii.security.model.JwtTokenResponse;
import com.foxconn.fii.security.service.OAuth2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class ApiUserController {

    @Autowired
    private UserService userService;

    @PostMapping("/channel/heartbeat")
    public CommonResponse<Boolean> heartbeatChannel() {
        userService.heartbeatChannel();
        return CommonResponse.success(true);
    }

    @PostMapping("/channel/register")
    public CommonResponse<UserChannelResponse> registerChannel(
            @RequestParam String deviceCode,
            @RequestParam(required = false, defaultValue = "") String fcmToken) {
        UserChannel channel = userService.registerChannel(deviceCode, fcmToken);
        return CommonResponse.success(UserChannelResponse.of(channel));
    }

    @PostMapping("/channel/unregister")
    public CommonResponse<Boolean> unregisterChannel(
            @RequestParam String deviceCode,
            @RequestParam(required = false, defaultValue = "") String fcmToken) {
        userService.unregisterChannel(deviceCode, fcmToken);
        return CommonResponse.success(true);
    }

    @GetMapping("/{id}")
    public CommonResponse<UserDetailResponse> getUserInformation(
            @PathVariable long id) {
        User detail = userService.getUser(id);
        return CommonResponse.success(UserDetailResponse.of(detail));
    }

    @PostMapping("/search")
    public ListResponse<UserSnapshotResponse> searchFriend(
            @RequestParam String query) {
        List<UserSnapshotResponse> friendList = userService.searchUser(query)
                .stream().map(UserSnapshotResponse::of).collect(Collectors.toList());
        return ListResponse.success(friendList);
    }

    @GetMapping("/friend")
    public ListResponse<UserSnapshotResponse> getListFriend(
            @RequestParam(required = false, defaultValue = "false") boolean supportCall) {
        List<UserSnapshotResponse> friendList = userService.getFriendList()
                .stream().filter(user -> !supportCall)
                .map(UserSnapshotResponse::of).collect(Collectors.toList());
        return ListResponse.success(friendList);
    }

    @GetMapping("/add-friend-request-number")
    public CommonResponse<Integer> getAddFriendRequestNumber() {
        Integer number = userService.getAddFriendRequestNumber();
        return CommonResponse.success(number);
    }

    @GetMapping("/add-friend-request-list")
    public ListResponse<AddFriendResponse> getAddFriendRequestList() {
        List<AddFriendResponse> response = userService.getAddFriendRequestList()
                .stream().map(AddFriendResponse::of).collect(Collectors.toList());
        return ListResponse.success(response);
    }

    @PostMapping("/add-friend")
    public CommonResponse<AddFriendResponse> addFriend(
            @RequestBody AddFriendRequest request) {
        UserFriendRequest ufRequest = userService.createFriendRequest(request);
        return CommonResponse.success(AddFriendResponse.of(ufRequest));
    }

    @PostMapping("/accept-friend/{requestId}")
    public CommonResponse<Boolean> acceptFriend(
            @PathVariable long requestId) {
        userService.acceptFriendRequest(requestId);
        return CommonResponse.success(true);
    }

    @PostMapping("/reject-friend/{requestId}")
    public CommonResponse<Boolean> rejectFriend(
            @PathVariable long requestId) {
        userService.rejectFriendRequest(requestId);
        return CommonResponse.success(true);
    }

    @PostMapping("/change-nick-name-friend")
    public CommonResponse<Boolean> changeNickNameFriend(
            @RequestParam long friendId,
            @RequestParam String nickName) {
        userService.changeNickNameFriend(friendId, nickName);
        return CommonResponse.success(true);
    }

    @PostMapping("/remove-friend")
    public CommonResponse<Boolean> removeFriend(
            @RequestParam long friendId) {
        userService.removeFriend(friendId);
        return CommonResponse.success(true);
    }
}
