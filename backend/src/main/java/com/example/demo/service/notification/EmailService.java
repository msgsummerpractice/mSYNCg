package com.example.demo.service.notification;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.demo.dto.response.EventDetailsResponse;
import com.example.demo.model.Location;
import com.example.demo.model.User;
import com.example.demo.service.EventService;
import com.example.demo.service.UserService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

@Service
@RequiredArgsConstructor
public class EmailService {

        private static final String POSTER_CID = "eventPoster";
        private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        private static final ZoneId BUCHAREST_ZONE = ZoneId.of("Europe/Bucharest");

        private final EventService eventService;
        private final UserService userService;
        private final EmailSender emailSender;

        @Value("classpath:templates/event-invitation.html")
        private Resource templateResource;

        @Value("classpath:templates/reset-password.html")
        private Resource passwordResetTemplateResource;

        @Value("${app.frontend-url}/events")
        private String eventsUrl;

        @Value("${app.frontend-url}/reset-password")
        private String resetPasswordUrl;

        private String htmlTemplate;
        private String passwordResetHtmlTemplate;

        @PostConstruct
        public void init() throws IOException {
                htmlTemplate = new String(templateResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                passwordResetHtmlTemplate = new String(passwordResetTemplateResource.getInputStream().readAllBytes(),
                                StandardCharsets.UTF_8);
        }

        public EmailContent createEmail(Integer eventId) {
                EventDetailsResponse event = eventService.getById(eventId);

                String registrationStartTime = event.getRegistrationStart() != null
                                ? event.getRegistrationStart().atZone(BUCHAREST_ZONE).format(DATE_FORMAT)
                                : "TBA";
                String registrationEndTime = event.getRegistrationEnd() != null
                                ? event.getRegistrationEnd().atZone(BUCHAREST_ZONE).format(DATE_FORMAT)
                                : "TBA";
                String locationLabel = event.getLocation() != null
                                ? event.getLocation().getDisplayValue()
                                : "TBA";

                byte[] poster = event.getImage() != null && !event.getImage().isEmpty()
                                ? Base64.getDecoder().decode(event.getImage())
                                : null;

                String posterHtml = poster != null
                                ? "<img src=\"cid:" + POSTER_CID + "\" alt=\"Event poster\""
                                                + " style=\"max-width:100%;max-height:400px;width:auto;height:auto;"
                                                + "display:block;margin:0 auto 16px;object-fit:contain;\" />"
                                : "";

                String eventLink = eventsUrl + "?eventId=" + eventId;

                String html = htmlTemplate.formatted(
                                posterHtml, event.getName(), registrationStartTime, registrationEndTime, locationLabel,
                                eventLink);

                String subject = "You're invited: " + event.getName();
                return new EmailContent(subject, html, poster, event.getLocation());
        }

        @Async("emailExecutor")
        public void sendEmail(Integer eventId) {
                EmailContent content = createEmail(eventId);

                List<User> recipients = content.location() == Location.ALL
                                ? userService.findAll()
                                : userService.findAllByLocation(content.location());

                for (User user : recipients) {
                        emailSender.sendOne(user.getEmail(), content);
                }
        }

        @Async("emailExecutor")
        public void sendPasswordResetEmail(String email, String rawToken) {

                String resetLink = resetPasswordUrl + "?token=" + rawToken;

                String html = passwordResetHtmlTemplate.formatted(resetLink);

                EmailContent content = new EmailContent(
                                "Reset your password",
                                html,
                                null,
                                null);

                emailSender.sendOne(email, content);
        }

        public record EmailContent(String subject, String html, byte[] poster, Location location) {
        }
}
