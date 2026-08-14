package com.example.demo.filtering.users;

import org.springframework.data.jpa.domain.Specification;

import com.example.demo.model.User;

import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Disjunction;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@Disjunction(value = {
    @And({
            @Spec(path = "firstName", params = "firstName", spec = LikeIgnoreCase.class),
            @Spec(path = "lastName", params = "lastName", spec = LikeIgnoreCase.class),
            @Spec(path = "email", params = "email", spec = LikeIgnoreCase.class),
            @Spec(path = "role", params = "role", spec = Equal.class),
            @Spec(path = "status", params = "status", spec = Equal.class)
        })
})
public interface UserSpec extends Specification<User>{

} 