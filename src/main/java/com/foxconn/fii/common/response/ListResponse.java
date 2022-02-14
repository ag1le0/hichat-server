package com.foxconn.fii.common.response;

import lombok.Data;
import lombok.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
public class ListResponse<T> {

    private HttpStatus status;

    private ResponseCode code;

    private String message;

    private List<T> data;

    private long size;

    public static <T> ListResponse<T> of(HttpStatus status, ResponseCode code, String message, List<T> data, long size) {
        ListResponse<T> ins = new ListResponse<>();
        ins.setStatus(status);
        ins.setCode(code);
        ins.setMessage(message);
        ins.setData(data);
        ins.setSize(size);
        return ins;
    }

    public static <T> ListResponse<T> of (HttpStatus status, ResponseCode code, String message, List<T> data) {
        return ListResponse.of(status, code, message, data, data.size());
    }

    public static <T> ListResponse<T> success(List<T> data) {
        return ListResponse.of(HttpStatus.OK, ResponseCode.SUCCESS, "success", data, data.size());
    }

    public static <T> ListResponse<T> success(List<T> data, long size) {
        return ListResponse.of(HttpStatus.OK, ResponseCode.SUCCESS, "success", data, size);
    }

    public static <T> ListResponse<T> success(Page<T> data) {
        return ListResponse.of(HttpStatus.OK, ResponseCode.SUCCESS, "success", data.getContent(), data.getTotalElements());
    }
}

