package com.tutorial.collabservice.Listener;
import com.tutorial.common.model.Project;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
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

            log.info("Forwarding PROJECT_SAVED to room {}", event.getRoom());

            for (SocketIOClient client :
                    server.getRoomOperations(event.getRoom()).getClients()) {

                client.sendEvent("project_retrieved",
                        String.format("{\"html\":\"%s\",\"css\":\"%s\",\"js\":\"%s\"}",
                                safe(event.getHtml()),
                                safe(event.getCss()),
                                safe(event.getJs())
                        )
                );
            }
        }
    }

    private String safe(String v) {
        return v == null ? "" : v.replace("\"", "\\\"");
    }
}
