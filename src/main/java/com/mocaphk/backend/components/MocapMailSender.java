package com.mocaphk.backend.components;

import com.mocaphk.backend.utils.DateUtils;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class MocapMailSender {
    private final JavaMailSender javaMailSender;
    private final TaskScheduler taskScheduler;

    @Value("${spring.mail.username}")
    private String from;

    @RequiredArgsConstructor
    private class SendMailTask implements Runnable {
        private final String fromName;
        private final String to;
        private final String subject;
        private final String body;

        @Override
        public void run() {
            try {
                sendMail(fromName, to, subject, body);
            } catch (MessagingException | UnsupportedEncodingException e) {
                log.error("Failed to send mail", e);
            }
        }
    }

    public MimeMessage makeMail(String fromName, String to, String subject, String body) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;
        helper = new MimeMessageHelper(message, true);
        helper.setFrom(from, fromName);
        helper.setSubject(subject);
        helper.setTo(to);
        helper.setText(body, true);
        return message;
    }

    public void sendMail(MimeMessage message) {
        javaMailSender.send(message);
    }

    public void sendMail(String fromName, String to, String subject, String body) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = makeMail(fromName, to, subject, body);
        javaMailSender.send(message);
    }

    public void scheduleMail(String fromName, String to, String subject, String body, LocalDateTime time) {
        taskScheduler.schedule(new SendMailTask(fromName, to, subject, body), new CronTrigger(DateUtils.toCron(time)));
    }
}
