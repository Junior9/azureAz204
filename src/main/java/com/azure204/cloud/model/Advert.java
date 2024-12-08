package com.azure204.cloud.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class Advert {

    private String id;
    private String userId;
    private String title;
    private String description;

}


