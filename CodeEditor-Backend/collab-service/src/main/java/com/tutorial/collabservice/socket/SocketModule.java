package com.tutorial.collabservice.socket;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

import com.tutorial.collabservice.Producer.ProjectEventProducer;

import com.tutorial.common.model.Project;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SocketModule {

    private final SocketIOServer server;
    private final ProjectEventProducer producer;

    public SocketModule(SocketIOServer server,
                        ProjectEventProducer producer) {

        this.server = server;
        this.producer = producer;

        server.addConnectListener(onConnected());
        server.addDisconnectListener(onDisconnected());

        server.addEventListener("project_write", Project.class, projectWrite());
        server.addEventListener("project_get", Project.class, projectGet());
        server.addEventListener("project_save", Project.class, projectSave());
    }

    // ========== FRONTEND → KAFKA ONLY ==========
    private DataListener<Project> projectWrite() {
        return (client, data, ackSender) -> {

            data.setType("PROJECT_UPDATED");

            producer.send(data);

            log.info("PROJECT_UPDATED sent to Kafka for room {}", data.getRoom());
        };
    }

    private DataListener<Project> projectGet() {
        return (client, data, ackSender) -> {

            Project event = Project.builder()
                    .room(data.getRoom())
                    .type("PROJECT_GET")
                    .build();

            producer.send(event);

            log.info("PROJECT_GET sent to Kafka for room {}", data.getRoom());
        };
    }

    private DataListener<Project> projectSave() {
        return (client, data, ackSender) -> {

            data.setType("PROJECT_UPDATED");

            producer.send(data);

            log.info("PROJECT_SAVE forwarded as PROJECT_UPDATED for room {}", data.getRoom());
        };
    }

    // ========== SOCKET CONNECTION ==========
    private ConnectListener onConnected() {
        return client -> {
            String room = client.getHandshakeData().getSingleUrlParam("room");
            client.joinRoom(room);
            log.info("Socket ID[{}] - room[{}] Connected",
                    client.getSessionId(), room);
        };
    }

    private DisconnectListener onDisconnected() {
        return client ->
                log.info("Client[{}] - Disconnected", client.getSessionId());
    }
}
