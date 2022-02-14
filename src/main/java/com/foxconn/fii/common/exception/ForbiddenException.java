package com.foxconn.fii.common.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForbiddenException extends RuntimeException {

    private String message;

    public ForbiddenException(String message) {
        super();
        this.message = message;
    }

    public static ForbiddenException of (String message, Object... data) {
        String msg = message;
        for (Object obj : data) {
            msg = msg.replaceFirst("[{][}]", obj.toString());
        }
        return new ForbiddenException(msg);
    }
}
