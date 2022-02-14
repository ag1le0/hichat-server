package com.foxconn.fii.main.data.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AddFollowPatternRequest {

    private long groupId;

    private List<FollowPatternRequest> followPatternList = new ArrayList<>();

    private List<String> bu = new ArrayList<>();

    private List<String> factory = new ArrayList<>();

    private List<String> cft = new ArrayList<>();

    private List<String> stage = new ArrayList<>();

    private List<String> floor = new ArrayList<>();

    private List<String> line = new ArrayList<>();

    private List<String> team = new ArrayList<>();

    private List<String> model = new ArrayList<>();

    private List<String> station = new ArrayList<>();

    @Data
    public static class FollowPatternRequest {

        private String followPattern;

        private String bu;

        private String factory;

        private String cft;

        private String stage;

        private String floor;

        private String team;

        private String line;

        private String model;

        private String station;
    }
}
