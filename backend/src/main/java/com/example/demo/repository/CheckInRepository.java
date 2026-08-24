package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.CheckIn;

public interface CheckInRepository extends JpaRepository<CheckIn, Integer> {

}
