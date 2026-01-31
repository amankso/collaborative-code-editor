package com.tutorial.projectservice.Listener;

import com.tutorial.common.model.Project;
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

    @KafkaListener(topics = "project-commands", groupId = "project-service")
    public void handle(Project event) {

        if (event == null || event.getType() == null) return;

        switch (event.getType()) {

            case "PROJECT_GET" -> {
                Project fetched =
                        projectService.getOrCreateByDefaultValues(event.getRoom());
                fetched.setType("PROJECT_SAVED");

                kafkaTemplate.send("project-events", fetched.getRoom(), fetched);
            }

            case "PROJECT_SAVE" -> {
                if ("DB".equals(event.getSource())) return;
                Project saved = projectService.update(event);

                saved.setType("PROJECT_SAVED"); // now means DONE
                saved.setSource("DB");

                kafkaTemplate.send("project-events", saved.getRoom(), saved);
            }


            case "PROJECT_WRITE" -> {
                // just relay to other clients
                kafkaTemplate.send("project-events", event.getRoom(), event);
            }
        }
    }
}
