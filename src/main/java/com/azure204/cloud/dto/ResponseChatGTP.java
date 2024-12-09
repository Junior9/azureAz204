package com.azure204.cloud.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ResponseChatGTP {

    public ResponseChatGTP(){}

    private String id;
    private String object;
    private String created;
    private String model;
    private List<DataUrl> data;
    private List<Choice> choices;

    @Data
    @AllArgsConstructor
    public static class Choice {

        public Choice(){}
        private int index;
        private Message message;
        private String finish_reason;

    }

    @Data
    @AllArgsConstructor
    public static class Message {
        public Message(){}
        private String role;
        private String content;
    }

    @Data
    @AllArgsConstructor
    public static class DataUrl {
        private String url;
    }



}
