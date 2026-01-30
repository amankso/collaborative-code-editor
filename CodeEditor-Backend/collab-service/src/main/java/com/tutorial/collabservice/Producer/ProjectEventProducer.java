package com.tutorial.collabservice.Producer;

import com.tutorial.common.model.Project;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProjectEventProducer {

    private final KafkaTemplate<String, Project> kafkaTemplate;

    public ProjectEventProducer(KafkaTemplate<String, Project> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(Project event) {
        kafkaTemplate.send("project-events", event.getRoom(), event);
    }
}
