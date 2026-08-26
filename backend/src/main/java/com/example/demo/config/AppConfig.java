package com.example.demo.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.example.demo.dto.request.RegistrationRequest;
import com.example.demo.dto.response.EventDetailsResponse;
import com.example.demo.model.Event;

import com.example.demo.model.Registration;

@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(Event.class, EventDetailsResponse.class)
                .addMappings(mapper -> mapper.skip(EventDetailsResponse::setImage));

        org.modelmapper.config.Configuration strict = modelMapper.getConfiguration().copy()
                .setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.createTypeMap(RegistrationRequest.class, Registration.class, strict);

        return modelMapper;
    }

}
