package com.saidelhabhab.orderservice.kafka;

import com.saidelhabhab.commonevents.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final KafkaTemplate<String,Object> kafkaTemplate;

    public void sendNotification(NotificationEvent event){

        kafkaTemplate.send(
                "notification-events",
                event
        );
    }
}