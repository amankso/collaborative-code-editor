package com.tutorial.collabservice.socket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

import com.tutorial.collabservice.model.Message;
import com.tutorial.collabservice.model.Project;


import com.tutorial.collabservice.Producer.ProjectEventProducer;
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
        server.addEventListener("project_write", Message.class, onChatReceived());
        server.addEventListener("project_get", Project.class, projectGet());
        server.addEventListener("project_save", Project.class, projectSave());
    }

    private DataListener<Project> projectGet() {
        return (senderClient, data, ackSender) -> {

            Project event = Project.builder()
                    .room(data.getRoom())
                    .type("PROJECT_GET")
                    .build();

            producer.send(event);

            log.info("PROJECT_GET sent to Kafka for room {}", data.getRoom());
        };
    }


    // Only push to Kafka now
    private DataListener<Project> projectSave() {
        return (senderClient, data, ackSender) -> {

            Project event = Project.builder()
                    .room(data.getRoom())
                    .html(data.getHtml())
                    .css(data.getCss())
                    .js(data.getJs())
                    .type("PROJECT_UPDATED")
                    .build();

            producer.send(event);

            log.info("Project update sent to Kafka for room {}", data.getRoom());
        };
    }

    private DataListener<Message> onChatReceived() {
        return (senderClient, data, ackSender) -> {
            for (SocketIOClient client :
                    senderClient.getNamespace()
                            .getRoomOperations(data.getRoom())
                            .getClients()) {

                if (!client.getSessionId().equals(senderClient.getSessionId())) {
                    client.sendEvent("project_read",
                            Message.builder()
                                    .data(data.getData())
                                    .type(data.getType())
                                    .build());
                }
            }
        };
    }

    private ConnectListener onConnected() {
        return (client) -> {
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
