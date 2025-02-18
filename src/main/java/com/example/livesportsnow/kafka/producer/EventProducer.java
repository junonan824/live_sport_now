package com.example.livesportsnow.kafka.producer;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class EventProducer {

    private final String topicName;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Gson gson;

    public EventProducer(
            @Value("${spring.kafka.topic.match-events}") String topicName,
            KafkaTemplate<String, String> kafkaTemplate) {
        this.topicName = topicName;
        this.kafkaTemplate = kafkaTemplate;
        this.gson = new Gson();
    }

    public void sendEvent(String eventId, Map<String, Object> eventData) {
        try {
            String jsonData = gson.toJson(eventData);
            
            kafkaTemplate.send(topicName, eventId, jsonData)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Event sent successfully: eventId={}, topic={}, partition={}, offset={}",
                                    eventId, result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        } else {
                            log.error("Failed to send event: eventId={}", eventId, ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Error while sending event: eventId={}", eventId, e);
            throw new RuntimeException("Failed to send event", e);
        }
    }
} 