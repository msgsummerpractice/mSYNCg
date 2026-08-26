package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exceptions.AlreadyCheckedInException;
import com.example.demo.exceptions.EventAlreadyCompletedException;
import com.example.demo.exceptions.EventCheckInExpiredException;
import com.example.demo.exceptions.EventNotStartedException;
import com.example.demo.exceptions.InvalidCheckInException;
import com.example.demo.exceptions.UserNotRegisteredException;
import com.example.demo.model.AttendanceRecord;
import com.example.demo.model.CheckIn;
import com.example.demo.model.Event;
import com.example.demo.model.EventStatus;
import com.example.demo.model.RegistrationStatus;
import com.example.demo.model.User;
import com.example.demo.repository.AttendanceRecordRepository;
import com.example.demo.repository.CheckInRepository;
import com.example.demo.repository.RegistrationRepository;
import com.example.demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final CheckInRepository checkInRepository;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;

    @Override
    @Transactional
    public void checkIn(String value) {

        CheckIn checkIn = resolveCheckIn(value);

        Event event = checkIn.getEvent();
        User user = getCurrentUser();

        validateRegistration(user, event);
        validateEventStatus(event);
        validateEventStartTime(event);
        validateEventEndTime(event);
        validateNoExistingAttendance(user, checkIn);

        AttendanceRecord attendanceRecord = new AttendanceRecord();

        attendanceRecord.setCheckIn(checkIn);
        attendanceRecord.setUser(user);

        attendanceRecordRepository.save(attendanceRecord);
    }

    private CheckIn resolveCheckIn(String value) {

        Optional<CheckIn> byQrCode = checkInRepository.findByQrCode(value);

        if (byQrCode.isPresent()) {
            return byQrCode.get();
        }

        Optional<CheckIn> byCode = findBySixDigitCode(value);

        if (byCode.isPresent()) {
            return byCode.get();
        }

        throw new InvalidCheckInException();
    }

    private Optional<CheckIn> findBySixDigitCode(String value) {

        if (!value.matches("\\d{6}")) {
            return Optional.empty();
        }

        Long code = Long.valueOf(value);

        return checkInRepository.findByCode(code);
    }

    private User getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new InvalidCheckInException();
        }

        return user;
    }

    private void validateRegistration(User user, Event event) {

        Boolean isRegistered = registrationRepository.existsByUserIdAndEventIdAndStatus(
                user.getId(),
                event.getId(),
                RegistrationStatus.REGISTERED);

        if (!isRegistered) {
            throw new UserNotRegisteredException();
        }
    }

    private void validateEventStatus(Event event) {

        if (event.getStatus() == EventStatus.COMPLETED) {
            throw new EventAlreadyCompletedException();
        }
    }

    private void validateEventStartTime(Event event) {

        if (event.getStartTime().isAfter(LocalDateTime.now())) {
            throw new EventNotStartedException();
        }
    }

    private void validateEventEndTime(Event event) {

        if (event.getEndTime().isBefore(LocalDateTime.now())) {
            throw new EventCheckInExpiredException();
        }
    }

    private void validateNoExistingAttendance(User user, CheckIn checkIn) {

        boolean alreadyCheckedIn = attendanceRecordRepository.existsByUserIdAndCheckInId(
                user.getId(),
                checkIn.getId());

        if (alreadyCheckedIn) {
            throw new AlreadyCheckedInException();
        }
    }
}