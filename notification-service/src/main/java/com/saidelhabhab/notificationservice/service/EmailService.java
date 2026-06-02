package com.saidelhabhab.notificationservice.service;

import com.saidelhabhab.commonevents.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender sender;

    public void sendEmail(NotificationEvent event) {

        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setTo(event.getEmail());
        mail.setSubject(event.getTitle());
        mail.setText(event.getMessage());

        sender.send(mail);
    }
}