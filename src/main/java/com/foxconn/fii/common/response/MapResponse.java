package com.foxconn.fii.common.response;

import lombok.Data;
import lombok.Value;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@Data
public class MapResponse<T> {

    private HttpStatus status;

    private ResponseCode code;

    private String message;

    private Map<String, T> data;

    private int size;

    public static <T> MapResponse<T> of(HttpStatus status, ResponseCode code, String message, Map<String, T> data, int size) {
        MapResponse<T> ins = new MapResponse<>();
        ins.setStatus(status);
        ins.setCode(code);
        ins.setMessage(message);
        ins.setData(data);
        ins.setSize(size);
        return ins;
    }

    public static <T> MapResponse<T> of(HttpStatus status, ResponseCode code, String message, Map<String, T> data) {
        return MapResponse.of(status, code, message, data, data.size());
    }

    public static <T> MapResponse<T> success(Map<String, T> data) {
        return MapResponse.of(HttpStatus.OK, ResponseCode.SUCCESS, "success", data, data.size());
    }
}
