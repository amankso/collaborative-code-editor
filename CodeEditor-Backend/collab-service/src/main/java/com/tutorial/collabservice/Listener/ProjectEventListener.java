package com.tutorial.collabservice.Listener;

import com.corundumstudio.socketio.SocketIOServer;
import com.tutorial.collabservice.model.Project;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProjectEventListener {

    private final SocketIOServer server;

    public ProjectEventListener(SocketIOServer server) {
        this.server = server;
    }

    @KafkaListener(topics = "project-events", groupId = "collab-service")
    public void handle(Project event) {

        if ("PROJECT_SAVED".equals(event.getType())) {

            log.info("Broadcasting saved project to room {}", event.getRoom());

            server.getRoomOperations(event.getRoom())
                    .sendEvent("project_updated", event);
        }
    }
}
