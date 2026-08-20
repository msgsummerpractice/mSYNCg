package com.example.demo.service.notification;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm");

    private final EventService eventService;
    private final UserService userService;
    private final EmailSender emailSender;

    @Value("classpath:templates/event-invitation.html")
    private Resource templateResource;

    private String htmlTemplate;

    @PostConstruct
    public void init() throws IOException {
        htmlTemplate = new String(templateResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    public EmailContent createEmail(Integer eventId) {
        EventDetailsResponse event = eventService.getById(eventId);

        String registrationStartTime = event.getRegistrationStart() != null ? event.getRegistrationStart().format(DATE_FORMAT) : "TBA";
        String registrationEndTime   = event.getRegistrationEnd()   != null ? event.getRegistrationEnd().format(DATE_FORMAT)   : "TBA";
        String locationLabel = event.getLocation() != null
                ? event.getLocation().getDisplayValue()
                : "TBA";

        byte[] poster = event.getImage() != null && !event.getImage().isEmpty()
                ? Base64.getDecoder().decode(event.getImage())
                : null;

        String posterHtml = poster != null
                ? "<img src=\"cid:" + POSTER_CID + "\" alt=\"Event poster\""
                        + " style=\"max-width:100%;height:auto;display:block;margin-bottom:16px;\" />"
                : "";

        String html = htmlTemplate.formatted(
                posterHtml, event.getName(), registrationStartTime, registrationEndTime, locationLabel
        );

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

    public record EmailContent(String subject, String html, byte[] poster, Location location) {}
}
