package com.foxconn.fii.main.data.model;

import com.foxconn.fii.common.utils.BeanUtils;
import com.foxconn.fii.main.data.entity.User;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

@Data
public class UserDetailResponse {

    private long id;

    private String username;

    private String name;

    private String chineseName;

    private String nickName;

    private String email;

    private String callNumber;

    private MediaResponse avatar;

    private String bu;

    private String cft;

    private String factory;

    private String department;

    private String title;

    private String level;

    private boolean friend;

    private boolean pwdExpired;

    private Date pwdExpiredTime;

    private String cardId;

    private String ouCode;

    private String ouName;

    private String upperOuCode;

    private String lowerOuCode;

    private String allManagers;

    private String siteAllManagers;

    private String buALlManagers;

    private Date hireDate;

    private Date leaveDate;

    public static UserDetailResponse of(User src) {
        UserDetailResponse ins = new UserDetailResponse();
        BeanUtils.copyPropertiesIgnoreNull(src, ins);
        ins.setNickName(src.getNickName());


        if (src.getAvatar() != null) {
            ins.setAvatar(MediaResponse.of(src.getAvatar()));
        }

        ins.setPwdExpired(ins.getPwdExpiredTime() == null || System.currentTimeMillis() > ins.getPwdExpiredTime().getTime());

        return ins;
    }
}
