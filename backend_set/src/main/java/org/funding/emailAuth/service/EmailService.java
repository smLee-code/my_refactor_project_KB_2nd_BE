package org.funding.emailAuth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public String sendVerificationEmail(String toEmail) {

        try {
            String code = generateCode();

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("이메일 인증 코드");
            message.setText("인증 코드:" + code);

            javaMailSender.send(message);
            return code;
        } catch (MailException e) {
            throw new RuntimeException("이메일 전송 실패: " + e.getMessage());
        }
    }

    private String generateCode() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(90000));
    }

}
