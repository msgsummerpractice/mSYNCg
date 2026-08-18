package com.example.demo.filtering.users;

import org.springframework.data.jpa.domain.Specification;

import com.example.demo.model.User;

import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;

@And({
    @Spec(path = "firstName", params = "firstName", spec = LikeIgnoreCase.class),
    @Spec(path = "lastName", params = "lastName", spec = LikeIgnoreCase.class),
    @Spec(path = "email", params = "email", spec = LikeIgnoreCase.class),
    @Spec(path = "role", params = "role", spec = In.class),
    @Spec(path = "location", params = "location", spec = In.class),
    @Spec(path = "status", params = "status", spec = In.class)
})
public interface UserSpec extends Specification<User>{

} 