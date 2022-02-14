package com.foxconn.fii.security.controller;

import com.foxconn.fii.common.response.CommonResponse;
import com.foxconn.fii.main.data.entity.User;
import com.foxconn.fii.main.data.model.UserDetailResponse;
import com.foxconn.fii.main.data.model.UserRequest;
import com.foxconn.fii.main.service.UserService;
import com.foxconn.fii.security.model.JwtTokenResponse;
import com.foxconn.fii.security.service.OAuth2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class SecurityApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private OAuth2Service oauth2Service;

    @PostMapping("/api/user/sign-in")
    public JwtTokenResponse signIn(String username, String password, String uuid) {
        return oauth2Service.getToken(username, password, uuid);
    }

    @GetMapping("/api/user/me")
    public CommonResponse<UserDetailResponse> getCurrentUserInformation() {
        User detail = userService.getCurrentUser();
        return CommonResponse.success(UserDetailResponse.of(detail));
    }

    @PostMapping("/api/user/change-password")
    public CommonResponse<Boolean> changePasswordUser(
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        userService.changePassword(oldPassword, newPassword);
        return CommonResponse.success(true);
    }

    @PostMapping("/api/user/update")
    public CommonResponse<UserDetailResponse> updateUser(
            @ModelAttribute UserRequest request) {
        User user = userService.updateUser(request);
        return CommonResponse.success(UserDetailResponse.of(user));
    }
}
