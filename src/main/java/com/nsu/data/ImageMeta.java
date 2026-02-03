package com.nsu.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageMeta {
    public String id;

    @JsonCreator
    public ImageMeta(@JsonProperty("id") String id) {
        this.id = id;
    }
}
