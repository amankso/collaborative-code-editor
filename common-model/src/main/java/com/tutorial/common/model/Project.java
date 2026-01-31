package com.tutorial.common.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {
    private String room;
    private String html;
    private String css;
    private String js;
    private String type;
    private String field; // HTML / CSS / JS
    private String data;  // actual content
    private String source; // "SOCKET" | "DB"

}
