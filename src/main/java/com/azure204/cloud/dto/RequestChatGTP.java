package com.azure204.cloud.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class RequestChatGTP {

    private String model;
    private List<Message> messages;
    //private String temperature;
    private int n;
    private int max_tokens;
    private ResponseFormat response_format;

    @Data
    @AllArgsConstructor
    public static class  Message{
        private String role;
        private String content;
    }

    @Data
    @AllArgsConstructor
    public static class  ResponseFormat{
        private String type;
    }
}
