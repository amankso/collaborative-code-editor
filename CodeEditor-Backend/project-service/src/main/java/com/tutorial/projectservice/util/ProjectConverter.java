package com.tutorial.projectservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tutorial.platformservice.exception.GeneralException;
import com.tutorial.projectservice.model.Project;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectConverter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String convertToJsonString(Project project) {
        try {
            return objectMapper.writeValueAsString(project);
        } catch (JsonProcessingException e) {
            throw new GeneralException("json serialization error", HttpStatus.BAD_REQUEST);
        }
    }

    public String convertToJsonString(List<Project> projectList) {
        try {
            return objectMapper.writeValueAsString(projectList);
        } catch (JsonProcessingException e) {
            throw new GeneralException("json serialization error", HttpStatus.BAD_REQUEST);
        }
    }
}
