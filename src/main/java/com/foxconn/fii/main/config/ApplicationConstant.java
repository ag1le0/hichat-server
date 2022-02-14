package com.foxconn.fii.main.config;

public class ApplicationConstant {

    public static final Long DEFAULT_OFFICIAL_GROUP = 98L;

    public static final Long AUTO_CALL_REPAIR_GROUP = 74L;

    public static final Long AUTO_CALL_REPAIR_MODULE = 18L;

    public static final String SIGN_SYSTEM = "Smart Factory";

    public static final String SIGN_FLOW_CODE_MODULE_REQUEST = "SF__MODULE_REQUEST";

    public static final String SIGN_MAIL_HEADER = "Dear user,</br></br>";

    public static final String SIGN_MAIL_MESSAGE = "Please access to <a href=\"%s/home#/sign?orderNo=%s\">%s</a>";

    public static final String SIGN_MAIL_FOOTER =
            "<a style=\"padding: 15px;\">This message is automatically sent, please do not reply directly!</a></br></br>" +
            "Thanks and Best regards,</br>" +
            "<b>-- FII TEAM --</b></br>" +
            "============================</br>" +
            "<b>Ext:</b> 26152</br>" +
            "<b>Email:</b> cpe-vn-fii-sw@mail.foxconn.com";

    public static final String VERSION_AGENT = "agent";

    public static final String VERSION_DESKTOP = "desktop";

    public static final String VERSION_MOBILE = "mobile";
}
