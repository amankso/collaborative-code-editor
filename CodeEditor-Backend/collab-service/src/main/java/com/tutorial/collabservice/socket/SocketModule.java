package com.tutorial.collabservice.socket;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.*;
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

    private DataListener<Project> projectWrite() {
        return (client, data, ackSender) -> {

            Project event = Project.builder()
                    .room(data.getRoom())
                    .type("PROJECT_WRITE")
                    .field(data.getType())
                    .data(data.getData())
                    .source("SOCKET")
                    .build();

            producer.send(event);
            log.info("PROJECT_WRITE → Kafka [{}]", data.getRoom());
        };
    }

    private DataListener<Project> projectGet() {
        return (client, data, ackSender) -> {
            Project event = Project.builder()
                    .room(data.getRoom())
                    .type("PROJECT_GET")
                    .build();

            producer.send(event);
            log.info("PROJECT_GET → Kafka [{}]", data.getRoom());
        };
    }

    private DataListener<Project> projectSave() {
        return (client, data, ackSender) -> {

            Project event = Project.builder()
                    .room(data.getRoom())
                    .type("PROJECT_SAVE")
                    .html(data.getHtml())
                    .css(data.getCss())
                    .js(data.getJs())
                    .source("SOCKET")
                    .build();

            producer.send(event);
            log.info("PROJECT_SAVED → Kafka [{}]", data.getRoom());
        };
    }

    private ConnectListener onConnected() {
        return client -> {
            String room = client.getHandshakeData().getSingleUrlParam("room");
            client.joinRoom(room);
            log.info("Socket {} joined room {}", client.getSessionId(), room);
        };
    }

    private DisconnectListener onDisconnected() {
        return client ->
                log.info("Socket {} disconnected", client.getSessionId());
    }
}
