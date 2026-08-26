package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecretConfigurationVerifier {

    private final Environment environment;

    private static final List<String> REQUIRED_PROPERTIES = List.of(
            "SPRING_DATASOURCE_PASSWORD",
            "SPRING_DATASOURCE_URL",
            "SPRING_DATASOURCE_USERNAME",
            "SPRING_JWT_EXPIRATION",
            "SPRING_JWT_SECRET",
            "SPRING_MAIL_FROM",
            "SPRING_MAIL_HOST",
            "SPRING_MAIL_PASSWORD",
            "SPRING_MAIL_PORT",
            "SPRING_MAIL_USERNAME",
            "WEB_URL"
    );

    @EventListener(ApplicationReadyEvent.class)
    public void verifyConfiguration() {
        List<String> missingProperties = REQUIRED_PROPERTIES.stream()
                .filter(property ->
                        !StringUtils.hasText(environment.getProperty(property)))
                .toList();

        if (missingProperties.isEmpty()) {
            log.info("All required configuration values were successfully loaded.");
        } else {
            log.error(
                    "Some required Key Vault-backed configuration values are missing: {}",
                    missingProperties
            );
        }
    }
}