package com.tutorial.collabservice.Controller;

import com.tutorial.common.model.Project;

import com.tutorial.collabservice.Producer.ProjectEventProducer;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class KafkaTestController {

    private final ProjectEventProducer producer;

    public KafkaTestController(ProjectEventProducer producer) {
        this.producer = producer;
    }

    @GetMapping("/send")
    public String send() {

        producer.send(
                Project.builder()
                        .room("room1")
                        .html("<h1>Hello Kafka</h1>")
                        .css("body{}")
                        .js("console.log('hi')")
                        .type("PROJECT_UPDATED")
                        .build()
        );

        return "sent";
    }
}
