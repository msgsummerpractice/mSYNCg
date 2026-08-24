INSERT INTO users (first_name, last_name, email, password, location, status, role) VALUES
('Victor', 'Chertes', 'victor.chertes.vc@gmail.com', '$2a$12$tiI8AUJKI5AkcN9OwuQ5yefBDUueYy9taKg5ggqncePWB0mc83bU.', 'CLUJ_NAPOCA', true, 'ADMIN'),
('Raul', 'Simon', 'raulsimonalexas@yahoo.com', '$2a$12$tiI8AUJKI5AkcN9OwuQ5yefBDUueYy9taKg5ggqncePWB0mc83bU.', 'TIMISOARA', true, 'PARTICIPANT'),
('Sonia','Damian','sonjadamian@gmail.com', '$2a$12$tiI8AUJKI5AkcN9OwuQ5yefBDUueYy9taKg5ggqncePWB0mc83bU.','TARGU_MURES',true,'HR_USER'),
('Dora', 'Gyurka', 'dora.gyurka25@gmail.com', '$2a$12$tiI8AUJKI5AkcN9OwuQ5yefBDUueYy9taKg5ggqncePWB0mc83bU.','TARGU_MURES',false, 'MARKETING_ORGANIZER');

INSERT INTO events (name, status, type, location, start_time, end_time, food_provided, registration_start, registration_end, description, created_by) VALUES
('Summer Tech Camp', 'PUBLISHED', 'INTERNAL', 'ALL', '2026-08-20 10:00:00', '2026-08-22 18:00:00', true, '2026-08-01 00:00:00', '2026-08-15 23:59:59', 'Un workshop de vara despre tehnologie Java si React.', 1),
('Teambuilding 2026', 'DRAFT', 'LOCAL', 'TIMISOARA', '2026-09-10 14:00:00', '2026-09-12 12:00:00', true, '2026-08-20 00:00:00', '2026-09-01 23:59:59', 'Teambuilding anual pentru toata compania.', 1),
('Summer Nights 2026', 'DRAFT', 'EXTERNAL', 'TIMISOARA', '2026-09-10 14:00:00', '2026-09-12 12:00:00', false, '2026-08-20 00:00:00', '2026-09-01 23:59:59', 'O ultima seara de vara cu colegii.', 1);

INSERT INTO registrations (user_id, event_id, status, driver_name, driver_phone, food_preference, accommodation_days, gdpr, photo_consent) VALUES
(2, 1, 'REGISTERED', 'Vasile Soferu', '0740123456', 'VEGETARIAN', 2, true, true),
(1, 2, 'WITHDRAWN', null, null, 'NONE', 2, true, false);

INSERT INTO check_in (event_id, qr_code, code) VALUES
(1, 'QR_SUMMER_TECH_12345', 12345),
(2, 'QR_TEAMBUILDING_99999', 99999);

INSERT INTO attendance_record (check_in_id, user_id) VALUES
(1, 2);

INSERT INTO notifications (user_id, event_id, start_time, message) VALUES
(2, 1, '2026-08-19 10:00:00', 'Nu uita ca maine incepe Summer Tech Camp! Te asteptam cu drag.'),
(1, 2, '2026-09-09 10:00:00', 'Teambuilding-ul se apropie. Verifica ultimele detalii.');
