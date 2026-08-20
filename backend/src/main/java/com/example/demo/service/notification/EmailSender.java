package com.example.demo.service.notification;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import com.example.demo.service.notification.EmailService.EmailContent;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSender {

    private static final String POSTER_CID = "eventPoster";

    private final JavaMailSender mailSender;

    @Retryable(
            retryFor = { MailException.class },
            noRetryFor = { MailParseException.class, MailAuthenticationException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2.0)
    )
    public void sendOne(String to, EmailContent content) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, MimeMessageHelper.MULTIPART_MODE_RELATED, "UTF-8");
            helper.setTo(to);
            helper.setSubject(content.subject());
            helper.setText(content.html(), true);

            if (content.poster() != null) {
                helper.addInline(POSTER_CID,
                        new ByteArrayResource(content.poster()),
                        detectImageMime(content.poster()));
            }
        } catch (MessagingException e) {
            throw new MailPreparationException("Failed to build message for " + to, e);
        }

        mailSender.send(message);
    }

    @Recover
    public void recover(MailException e, String to, EmailContent content) {
        log.warn("Giving up on email to {} after retries: {}", to, e.getMessage());
    }

    private static String detectImageMime(byte[] bytes) {
        if (bytes == null || bytes.length < 4) {
            return "application/octet-stream";
        }
        if ((bytes[0] & 0xFF) == 0xFF
                && (bytes[1] & 0xFF) == 0xD8
                && (bytes[2] & 0xFF) == 0xFF) {
            return "image/jpeg";
        }
        if ((bytes[0] & 0xFF) == 0x89
                && bytes[1] == 'P' && bytes[2] == 'N' && bytes[3] == 'G') {
            return "image/png";
        }
        return "application/octet-stream";
    }
}
