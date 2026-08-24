package com.example.demo.service;

import com.example.demo.exceptions.AlreadyCheckedInException;
import com.example.demo.exceptions.EventAlreadyCompletedException;
import com.example.demo.exceptions.EventCheckInExpiredException;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AttendanceServiceImplTests {

    @Mock
    private CheckInRepository checkInRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private AttendanceRecordRepository attendanceRecordRepository;

    @InjectMocks
    private AttendanceServiceImpl attendanceService;

    @Test
    void checkIn_whenValidQrCode_createsAttendanceRecord() {
        String qrCode = "Event:Test Event|ID:1";
        CheckIn checkIn = createCheckIn(1, qrCode, 123456L);
        Event event = createEvent(1, EventStatus.PUBLISHED, LocalDateTime.now().plusHours(2));
        User user = createUser(1, "test@example.com");
        checkIn.setEvent(event);

        mockAuthentication(user.getEmail());
        when(checkInRepository.findByQrCode(qrCode)).thenReturn(Optional.of(checkIn));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(registrationRepository.existsByUserIdAndEventIdAndStatus(
                user.getId(), event.getId(), RegistrationStatus.REGISTERED)).thenReturn(true);
        when(attendanceRecordRepository.existsByUserIdAndCheckInId(user.getId(), checkIn.getId()))
                .thenReturn(false);

        ArgumentCaptor<AttendanceRecord> captor = ArgumentCaptor.forClass(AttendanceRecord.class);

        attendanceService.checkIn(qrCode);

        verify(attendanceRecordRepository).save(captor.capture());
        AttendanceRecord saved = captor.getValue();
        assertNotNull(saved);
        assertEquals(checkIn, saved.getCheckIn());
        assertEquals(user, saved.getUser());
    }

    @Test
    void checkIn_whenValidSixDigitCode_createsAttendanceRecord() {
        String code = "123456";
        CheckIn checkIn = createCheckIn(1, "Event:Test Event|ID:1", 123456L);
        Event event = createEvent(1, EventStatus.PUBLISHED, LocalDateTime.now().plusHours(2));
        User user = createUser(1, "test@example.com");
        checkIn.setEvent(event);

        mockAuthentication(user.getEmail());
        when(checkInRepository.findByQrCode(code)).thenReturn(Optional.empty());
        when(checkInRepository.findByCode(123456L)).thenReturn(Optional.of(checkIn));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(registrationRepository.existsByUserIdAndEventIdAndStatus(
                user.getId(), event.getId(), RegistrationStatus.REGISTERED)).thenReturn(true);
        when(attendanceRecordRepository.existsByUserIdAndCheckInId(user.getId(), checkIn.getId()))
                .thenReturn(false);

        attendanceService.checkIn(code);

        verify(attendanceRecordRepository).save(any(AttendanceRecord.class));
    }

    @Test
    void checkIn_whenInvalidCode_throwsInvalidCheckInException() {
        String invalidCode = "invalid123";

        when(checkInRepository.findByQrCode(invalidCode)).thenReturn(Optional.empty());

        assertThrows(InvalidCheckInException.class, () -> attendanceService.checkIn(invalidCode));

        verify(attendanceRecordRepository, never()).save(any(AttendanceRecord.class));
    }

    @Test
    void checkIn_whenCodeNotFound_throwsInvalidCheckInException() {
        String code = "123456";

        when(checkInRepository.findByQrCode(code)).thenReturn(Optional.empty());
        when(checkInRepository.findByCode(123456L)).thenReturn(Optional.empty());

        assertThrows(InvalidCheckInException.class, () -> attendanceService.checkIn(code));

        verify(attendanceRecordRepository, never()).save(any(AttendanceRecord.class));
    }

    @Test
    void checkIn_whenUserNotFound_throwsInvalidCheckInException() {
        String qrCode = "Event:Test Event|ID:1";
        CheckIn checkIn = createCheckIn(1, qrCode, 123456L);
        Event event = createEvent(1, EventStatus.PUBLISHED, LocalDateTime.now().plusHours(2));
        checkIn.setEvent(event);

        mockAuthentication("test@example.com");
        when(checkInRepository.findByQrCode(qrCode)).thenReturn(Optional.of(checkIn));
        when(userRepository.findByEmail("test@example.com")).thenReturn(null);

        assertThrows(InvalidCheckInException.class, () -> attendanceService.checkIn(qrCode));

        verify(attendanceRecordRepository, never()).save(any(AttendanceRecord.class));
    }

    @Test
    void checkIn_whenUserNotRegistered_throwsUserNotRegisteredException() {
        String qrCode = "Event:Test Event|ID:1";
        CheckIn checkIn = createCheckIn(1, qrCode, 123456L);
        Event event = createEvent(1, EventStatus.PUBLISHED, LocalDateTime.now().plusHours(2));
        User user = createUser(1, "test@example.com");
        checkIn.setEvent(event);

        mockAuthentication(user.getEmail());
        when(checkInRepository.findByQrCode(qrCode)).thenReturn(Optional.of(checkIn));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(registrationRepository.existsByUserIdAndEventIdAndStatus(
                user.getId(), event.getId(), RegistrationStatus.REGISTERED)).thenReturn(false);

        assertThrows(UserNotRegisteredException.class, () -> attendanceService.checkIn(qrCode));

        verify(attendanceRecordRepository, never()).save(any(AttendanceRecord.class));
    }

    @Test
    void checkIn_whenEventCompleted_throwsEventAlreadyCompletedException() {
        String qrCode = "Event:Test Event|ID:1";
        CheckIn checkIn = createCheckIn(1, qrCode, 123456L);
        Event event = createEvent(1, EventStatus.COMPLETED, LocalDateTime.now().plusHours(2));
        User user = createUser(1, "test@example.com");
        checkIn.setEvent(event);

        mockAuthentication(user.getEmail());
        when(checkInRepository.findByQrCode(qrCode)).thenReturn(Optional.of(checkIn));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(registrationRepository.existsByUserIdAndEventIdAndStatus(
                user.getId(), event.getId(), RegistrationStatus.REGISTERED)).thenReturn(true);

        assertThrows(EventAlreadyCompletedException.class, () -> attendanceService.checkIn(qrCode));

        verify(attendanceRecordRepository, never()).save(any(AttendanceRecord.class));
    }

    @Test
    void checkIn_whenEventExpired_throwsEventCheckInExpiredException() {
        String qrCode = "Event:Test Event|ID:1";
        CheckIn checkIn = createCheckIn(1, qrCode, 123456L);
        Event event = createEvent(1, EventStatus.PUBLISHED, LocalDateTime.now().minusHours(1));
        User user = createUser(1, "test@example.com");
        checkIn.setEvent(event);

        mockAuthentication(user.getEmail());
        when(checkInRepository.findByQrCode(qrCode)).thenReturn(Optional.of(checkIn));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(registrationRepository.existsByUserIdAndEventIdAndStatus(
                user.getId(), event.getId(), RegistrationStatus.REGISTERED)).thenReturn(true);

        assertThrows(EventCheckInExpiredException.class, () -> attendanceService.checkIn(qrCode));

        verify(attendanceRecordRepository, never()).save(any(AttendanceRecord.class));
    }

    @Test
    void checkIn_whenAlreadyCheckedIn_throwsAlreadyCheckedInException() {
        String qrCode = "Event:Test Event|ID:1";
        CheckIn checkIn = createCheckIn(1, qrCode, 123456L);
        Event event = createEvent(1, EventStatus.PUBLISHED, LocalDateTime.now().plusHours(2));
        User user = createUser(1, "test@example.com");
        checkIn.setEvent(event);

        mockAuthentication(user.getEmail());
        when(checkInRepository.findByQrCode(qrCode)).thenReturn(Optional.of(checkIn));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(registrationRepository.existsByUserIdAndEventIdAndStatus(
                user.getId(), event.getId(), RegistrationStatus.REGISTERED)).thenReturn(true);
        when(attendanceRecordRepository.existsByUserIdAndCheckInId(user.getId(), checkIn.getId()))
                .thenReturn(true);

        assertThrows(AlreadyCheckedInException.class, () -> attendanceService.checkIn(qrCode));

        verify(attendanceRecordRepository, never()).save(any(AttendanceRecord.class));
    }

    @Test
    void checkIn_whenCodeHasLeadingZeros_parsesCorrectly() {
        String code = "000123";
        CheckIn checkIn = createCheckIn(1, "Event:Test Event|ID:1", 123L);
        Event event = createEvent(1, EventStatus.PUBLISHED, LocalDateTime.now().plusHours(2));
        User user = createUser(1, "test@example.com");
        checkIn.setEvent(event);

        mockAuthentication(user.getEmail());
        when(checkInRepository.findByQrCode(code)).thenReturn(Optional.empty());
        when(checkInRepository.findByCode(123L)).thenReturn(Optional.of(checkIn));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);
        when(registrationRepository.existsByUserIdAndEventIdAndStatus(
                user.getId(), event.getId(), RegistrationStatus.REGISTERED)).thenReturn(true);
        when(attendanceRecordRepository.existsByUserIdAndCheckInId(user.getId(), checkIn.getId()))
                .thenReturn(false);

        attendanceService.checkIn(code);

        verify(checkInRepository).findByCode(123L);
        verify(attendanceRecordRepository).save(any(AttendanceRecord.class));
    }

    private void mockAuthentication(String email) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContext);
    }

    private CheckIn createCheckIn(Integer id, String qrCode, Long code) {
        CheckIn checkIn = new CheckIn();
        checkIn.setId(id);
        checkIn.setQrCode(qrCode);
        checkIn.setCode(code);
        return checkIn;
    }

    private Event createEvent(Integer id, EventStatus status, LocalDateTime endTime) {
        Event event = new Event();
        event.setId(id);
        event.setStatus(status);
        event.setEndTime(endTime);
        return event;
    }

    private User createUser(Integer id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        return user;
    }
}
