package com.example.parser.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ConsumerService {
    @Autowired
    private ParserService parserService;

    @KafkaListener(topics = "metadata", groupId="mygroup")
    public void consume(String id) {
        System.out.println("Received: " + id);
        parserService.parse(Long.parseLong(id));
    }
}
