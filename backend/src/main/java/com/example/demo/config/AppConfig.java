package com.example.demo.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.dto.response.EventDetailsResponse;
import com.example.demo.model.Event;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        
        modelMapper.typeMap(Event.class, EventDetailsResponse.class)
                .addMappings(mapper -> mapper.skip(EventDetailsResponse::setImage));

        return modelMapper;
    }

}
