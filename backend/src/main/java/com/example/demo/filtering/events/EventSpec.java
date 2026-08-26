package com.example.demo.filtering.events;

import org.springframework.data.jpa.domain.Specification;
import net.kaczmarzyk.spring.data.jpa.domain.EqualDay;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@And({
    @Spec(path = "name", params = "name", spec = LikeIgnoreCase.class),
    @Spec(path = "type", params = "type", spec = In.class),
    @Spec(path = "status", params = "status", spec = In.class),
    @Spec(path = "startTime", params = "startTime", config = "yyyy-MM-dd", spec = EqualDay.class)})
    
public interface EventSpec extends Specification<com.example.demo.model.Event> {
}