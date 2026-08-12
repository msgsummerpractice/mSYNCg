INSERT INTO users (first_name, last_name, email, password, location, status, role) VALUES
('Victor', 'Popescu', 'victor@example.com', 'hashed_password_123', 'CLUJ-NAPOCA', true, 'ADMIN'),
('Maria', 'Ionescu', 'maria@example.com', 'hashed_password_456', 'TIMISOARA', true, 'PARTICIPANT');

INSERT INTO drivers (name, telephone_nr) VALUES
('Vasile Soferu', '0740123456'),
('Mihai Viteazu', '0722987654');

INSERT INTO events (name, status, type, location, start_time, end_time, food_provided, registration_start, registration_end, description, created_by) VALUES
('Summer Tech Camp', 'PUBLISHED', 'INTERNAL', 'CLUJ-NAPOCA', '2026-08-20 10:00:00', '2026-08-22 18:00:00', true, '2026-08-01 00:00:00', '2026-08-15 23:59:59', 'Un workshop de vara despre tehnologie Java si React.', 1),
('Teambuilding 2026', 'DRAFT', 'LOCAL', 'TIMISOARA', '2026-09-10 14:00:00', '2026-09-12 12:00:00', true, '2026-08-20 00:00:00', '2026-09-01 23:59:59', 'Teambuilding anual pentru toata compania.', 1);

INSERT INTO registrations (user_id, event_id, status, driver_id, food_preference, accommodation_days, gdpr, photo_consent) VALUES
(2, 1, 'COMPLETED', 1, 'VEGETARIAN', 2, true, true),
(1, 2, 'PUBLISHED', null, 'NONE', 2, true, false);

INSERT INTO check_in (event_id, qr_code, code) VALUES
(1, 'QR_SUMMER_TECH_12345', 12345),
(2, 'QR_TEAMBUILDING_99999', 99999);

INSERT INTO attendance_record (check_in_id, user_id) VALUES
(1, 2);

INSERT INTO notifications (user_id, event_id, start_time, message) VALUES
(2, 1, '2026-08-19 10:00:00', 'Nu uita ca maine incepe Summer Tech Camp! Te asteptam cu drag.'),
(1, 2, '2026-09-09 10:00:00', 'Teambuilding-ul se apropie. Verifica ultimele detalii.');
