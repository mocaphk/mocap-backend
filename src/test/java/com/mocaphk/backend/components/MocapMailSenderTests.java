package com.mocaphk.backend.components;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@ActiveProfiles(value = "test")
@SpringBootTest
@Slf4j
public class MocapMailSenderTests {
    @Autowired
    private MocapMailSender mailSender;

    @Value("${spring.mail.username}")
    private String mail;

    @Test
    public void testSendMail() throws Exception {
        mailSender.sendMail("[MOCAP] Sender", mail, "Test", "testSendMail");
    }

    @Test
    public void testScheduleMail() throws Exception {
        LocalDateTime time = LocalDateTime.now().plusSeconds(10);
        mailSender.scheduleMail("[MOCAP] Sender", mail, "Test", "testScheduleMail", time);
        TimeUnit.SECONDS.sleep(20);
    }
}
