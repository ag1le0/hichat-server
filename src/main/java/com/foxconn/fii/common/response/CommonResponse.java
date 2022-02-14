package com.foxconn.fii.common.response;

import lombok.Data;
import lombok.Value;
import org.springframework.http.HttpStatus;

@Data
public class CommonResponse<T> {

    private HttpStatus status;

    private ResponseCode code;

    private String message;

    private T result;

    public static <T> CommonResponse<T> of(HttpStatus status, ResponseCode code, String message, T data) {
        CommonResponse<T> ins = new CommonResponse<>();
        ins.setStatus(status);
        ins.setCode(code);
        ins.setMessage(message);
        ins.setResult(data);

        return ins;
    }

    public static <T> CommonResponse<T> success(T data) {
        return CommonResponse.of(HttpStatus.OK, ResponseCode.SUCCESS, "success", data);
    }
}
