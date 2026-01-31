package com.ems.alga.posts.text.processor.service.api.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostOutput {

    private String postId;
    private Integer wordCount;
    private Double calculatedValue;

}
