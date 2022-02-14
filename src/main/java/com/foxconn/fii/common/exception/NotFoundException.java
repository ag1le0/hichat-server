package com.foxconn.fii.common.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotFoundException extends RuntimeException {

    private String message;

    public NotFoundException(String message) {
        super();
        this.message = message;
    }

    public static NotFoundException of (String message, Object... data) {
        String msg = message;
        for (Object obj : data) {
            msg = msg.replaceFirst("[{][}]", obj.toString());
        }
        return new NotFoundException(msg);
    }
}
