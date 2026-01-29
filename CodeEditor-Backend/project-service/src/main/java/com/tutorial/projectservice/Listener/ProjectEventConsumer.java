package com.tutorial.projectservice.Listener;

import com.tutorial.projectservice.model.Project;
import com.tutorial.projectservice.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProjectEventConsumer {

    private final ProjectService projectService;
    private final KafkaTemplate<String, Project> kafkaTemplate;

    public ProjectEventConsumer(ProjectService projectService,
                                KafkaTemplate<String, Project> kafkaTemplate) {
        this.projectService = projectService;
        this.kafkaTemplate = kafkaTemplate;
    }


    @KafkaListener(topics = "project-events", groupId = "project-service")
    public void handle(Project event) {

        if ("PROJECT_UPDATED".equals(event.getType())) {

            log.info("Saving project for room {}", event.getRoom());

            Project saved = projectService.update(event);
            saved.setType("PROJECT_SAVED");

            kafkaTemplate.send("project-events", saved);
            log.info("PROJECT_SAVED published to Kafka");
        }

        else if ("PROJECT_GET".equals(event.getType())) {

            log.info("Fetching project for room {}", event.getRoom());

            Project fetched = projectService.getOrCreateByDefaultValues(event.getRoom());
            fetched.setType("PROJECT_SAVED");

            kafkaTemplate.send("project-events", fetched);
            log.info("PROJECT_GET response published to Kafka");
        }
    }

}
