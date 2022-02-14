package com.foxconn.fii.main.data.model;

import lombok.Data;

import java.util.Map;

@Data
public class RootCauseAndActionResponse {

    private String rootCause;

    private String action;

    public static RootCauseAndActionResponse of(Map<String, Object> objectMap) {
        RootCauseAndActionResponse ins = new RootCauseAndActionResponse();
        ins.setRootCause((String)objectMap.get("root_cause"));
        ins.setAction((String)objectMap.get("action"));
        return ins;
    }
}
