package com.foxconn.fii.common.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonException extends RuntimeException {

    private String message;

    public CommonException(String message) {
        super();
        this.message = message;
    }

    public static CommonException of (String message, Object... data) {
        String msg = message;
        for (Object obj : data) {
            msg = msg.replaceFirst("[{][}]", obj.toString());
        }
        return new CommonException(msg);
    }
}
