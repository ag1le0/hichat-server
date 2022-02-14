package com.foxconn.fii.security.service;

import com.foxconn.fii.security.model.JwtTokenResponse;
import com.foxconn.fii.security.model.OAuth2User;

public interface OAuth2Service {

    JwtTokenResponse getToken(String username, String password, String uuid);

    OAuth2User getCurrentUserInformation();

    void changePassword(String oldPassword, String newPassword);
}
