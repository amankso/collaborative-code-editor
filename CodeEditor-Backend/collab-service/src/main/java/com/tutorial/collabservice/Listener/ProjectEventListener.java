package com.tutorial.collabservice.Listener;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.tutorial.common.model.Project;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
@Slf4j
public class ProjectEventListener {

    private final SocketIOServer server;

    public ProjectEventListener(SocketIOServer server) {
        this.server = server;
    }

    @KafkaListener(topics = "project-events", groupId = "collab-service")
    public void handle(Project event) {

        if (event == null || event.getType() == null) return;

        String room = event.getRoom();

        switch (event.getType()) {

            case "PROJECT_WRITE" -> {
                server.getRoomOperations(room)
                        .sendEvent("project_read", Map.of(
                                "type", event.getField(),
                                "data", event.getData()
                        ));

                log.info("📤 LIVE broadcast {} to {}", event.getField(), room);
            }


            case "PROJECT_SAVED" -> {
                if (!"DB".equals(event.getSource())) return;
                server.getRoomOperations(room)
                        .sendEvent("project_retrieved", event);
            }
        }
    }

}

