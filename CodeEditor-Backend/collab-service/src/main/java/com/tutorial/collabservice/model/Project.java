package com.tutorial.collabservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    private String id;
    private String room;

    private String html;
    private String css;
    private String js;

    private String type;
}
