package com.saidelhabhab.notificationservice.consumer;

import com.saidelhabhab.commonevents.NotificationEvent;
import com.saidelhabhab.notificationservice.service.EmailService;
import com.saidelhabhab.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final NotificationService notificationService;
    private final EmailService emailService;

    @KafkaListener(
            topics = "notification-events",
            groupId = "notification-group"
    )
    public void consume(NotificationEvent event){

        notificationService.create(event);

        emailService.sendEmail(event);

        System.out.println(
                "Notification reçue : "
                        + event.getEventType()
        );
    }
}
