package com.ems.alga.posts.text.processor.service.api.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostInput {

    private String postId;
    private String postBody;

}
