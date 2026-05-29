INSERT INTO countries (name, iso_code) VALUES
    ('Россия', 'RU'),
    ('Турция', 'TR'),
    ('ОАЭ', 'AE'),
    ('Сербия', 'RS');

INSERT INTO cities (country_id, name) VALUES
    (1, 'Москва'),
    (1, 'Санкт-Петербург'),
    (2, 'Стамбул'),
    (3, 'Дубай'),
    (4, 'Белград');

INSERT INTO airports (city_id, iata_code, name) VALUES
    (1, 'SVO', 'Шереметьево'),
    (2, 'LED', 'Пулково'),
    (3, 'IST', 'Istanbul Airport'),
    (4, 'DXB', 'Dubai International'),
    (5, 'BEG', 'Nikola Tesla Airport');

INSERT INTO airlines (country_id, name) VALUES
    (1, 'Аэрофлот'),
    (2, 'Turkish Airlines'),
    (3, 'Emirates');

INSERT INTO aircraft_types (manufacturer, model, seat_capacity) VALUES
    ('Airbus', 'A320', 180),
    ('Boeing', '737-800', 189),
    ('Boeing', '777-300ER', 360);

INSERT INTO aircraft (airline_id, aircraft_type_id, registration_number, manufacture_year) VALUES
    (1, 1, 'RA-73701', 2018),
    (1, 2, 'RA-73622', 2017),
    (2, 2, 'TC-JFA', 2019),
    (3, 3, 'A6-ENU', 2021);

INSERT INTO passengers (passport_number, last_name, first_name, middle_name, birth_date) VALUES
    ('4510123456', 'Иванов', 'Алексей', 'Петрович', '1998-04-12'),
    ('4510654321', 'Петрова', 'Мария', 'Игоревна', '2001-09-21'),
    ('4011122233', 'Сидоров', 'Денис', 'Андреевич', '1988-11-03'),
    ('7000111222', 'Кузнецова', 'Анна', 'Олеговна', '1995-01-19'),
    ('9000456789', 'Орлова', 'Екатерина', 'Сергеевна', '1992-06-25'),
    ('AB1234567', 'Yilmaz', 'Kerem', NULL, '1987-03-08');

INSERT INTO pay_schemes (name, base_rate, hourly_rate, flight_bonus, sale_percent) VALUES
    ('Кассир: оклад и процент продаж', 45000.00, 0.00, 0.00, 2.00),
    ('Пилот: оклад, часы и рейсовая премия', 120000.00, 1200.00, 7000.00, 0.00),
    ('Менеджер: фиксированный оклад', 70000.00, 0.00, 0.00, 0.00);

INSERT INTO employees (pay_scheme_id, role, personnel_number, last_name, first_name, middle_name, hire_date) VALUES
    (1, 'cashier', 'C-001', 'Смирнова', 'Ольга', 'Викторовна', '2023-02-01'),
    (1, 'cashier', 'C-002', 'Морозов', 'Илья', 'Павлович', '2024-06-10'),
    (2, 'pilot', 'P-101', 'Волков', 'Андрей', 'Николаевич', '2019-05-15'),
    (2, 'pilot', 'P-102', 'Егоров', 'Сергей', 'Михайлович', '2020-08-20'),
    (2, 'pilot', 'P-103', 'Ким', 'Роман', 'Дмитриевич', '2022-01-12'),
    (3, 'manager', 'M-001', 'Федорова', 'Наталья', 'Александровна', '2021-09-01');

INSERT INTO work_schedules (employee_id, work_date, shift_start, shift_end) VALUES
    (1, '2026-05-01', '2026-05-01 08:00', '2026-05-01 20:00'),
    (1, '2026-05-02', '2026-05-02 08:00', '2026-05-02 20:00'),
    (2, '2026-05-01', '2026-05-01 10:00', '2026-05-01 22:00'),
    (3, '2026-05-01', '2026-05-01 06:00', '2026-05-01 16:00'),
    (4, '2026-05-01', '2026-05-01 06:00', '2026-05-01 16:00'),
    (5, '2026-05-02', '2026-05-02 07:00', '2026-05-02 17:00');

INSERT INTO routes (departure_airport_id, arrival_airport_id, route_code, distance_km) VALUES
    (1, 2, 'SVO-LED', 635),
    (1, 3, 'SVO-IST', 1750),
    (1, 4, 'SVO-DXB', 3680),
    (2, 5, 'LED-BEG', 1790),
    (3, 1, 'IST-SVO', 1750);

INSERT INTO fares (route_id, service_class, valid_from, valid_to, price) VALUES
    (1, 'economy', '2026-01-01', NULL, 6200.00),
    (1, 'business', '2026-01-01', NULL, 18800.00),
    (2, 'economy', '2026-01-01', NULL, 21400.00),
    (2, 'business', '2026-01-01', NULL, 58900.00),
    (3, 'economy', '2026-01-01', NULL, 33600.00),
    (3, 'business', '2026-01-01', NULL, 102000.00),
    (4, 'economy', '2026-01-01', NULL, 25100.00),
    (5, 'economy', '2026-01-01', NULL, 22000.00);

INSERT INTO flights (route_id, aircraft_id, flight_number, scheduled_departure, scheduled_arrival, actual_departure, actual_arrival, status) VALUES
    (1, 1, 'SU100', '2026-05-01 09:20', '2026-05-01 10:45', '2026-05-01 09:24', '2026-05-01 10:49', 'arrived'),
    (2, 2, 'SU2130', '2026-05-01 12:30', '2026-05-01 17:10', '2026-05-01 12:42', '2026-05-01 17:21', 'arrived'),
    (3, 4, 'EK132', '2026-05-02 08:15', '2026-05-02 14:45', '2026-05-02 08:20', '2026-05-02 14:54', 'arrived'),
    (4, 3, 'TK404', '2026-05-02 15:00', '2026-05-02 17:55', '2026-05-02 15:06', '2026-05-02 18:05', 'arrived'),
    (5, 3, 'TK411', '2026-05-03 11:10', '2026-05-03 15:40', NULL, NULL, 'scheduled');

INSERT INTO flight_pilots (flight_id, pilot_id, crew_role) VALUES
    (1, 3, 'captain'),
    (1, 4, 'copilot'),
    (2, 3, 'captain'),
    (2, 5, 'copilot'),
    (3, 4, 'captain'),
    (3, 5, 'copilot'),
    (4, 5, 'captain'),
    (4, 3, 'copilot');

INSERT INTO tickets (flight_id, passenger_id, cashier_id, fare_id, ticket_number, sale_timestamp, seat_number, price) VALUES
    (1, 1, 1, 1, 'T-2026-0001', '2026-04-25 10:15', '12A', 6200.00),
    (1, 2, 1, 2, 'T-2026-0002', '2026-04-25 10:22', '2C', 18800.00),
    (2, 3, 2, 3, 'T-2026-0003', '2026-04-26 11:05', '18F', 21400.00),
    (2, 4, 1, 4, 'T-2026-0004', '2026-04-27 14:30', '3A', 58900.00),
    (3, 5, 2, 5, 'T-2026-0005', '2026-04-28 09:45', '24B', 33600.00),
    (3, 6, 2, 6, 'T-2026-0006', '2026-04-28 10:00', '4D', 102000.00),
    (4, 1, 1, 7, 'T-2026-0007', '2026-04-29 16:25', '14C', 25100.00),
    (5, 2, 2, 8, 'T-2026-0008', '2026-05-01 12:10', '10A', 22000.00);

INSERT INTO ticket_payments (ticket_id, payment_timestamp, amount, payment_method) VALUES
    (1, '2026-04-25 10:16', 6200.00, 'card'),
    (2, '2026-04-25 10:23', 18800.00, 'cash'),
    (3, '2026-04-26 11:07', 21400.00, 'online'),
    (4, '2026-04-27 14:31', 58900.00, 'card'),
    (5, '2026-04-28 09:47', 33600.00, 'card'),
    (6, '2026-04-28 10:02', 102000.00, 'online'),
    (7, '2026-04-29 16:26', 25100.00, 'cash'),
    (8, '2026-05-01 12:11', 22000.00, 'card');

INSERT INTO salary_payments (employee_id, period_start, period_end, base_amount, hours_amount, bonus_amount) VALUES
    (1, '2026-05-01', '2026-05-31', 45000.00, 0.00, 1000.00),
    (2, '2026-05-01', '2026-05-31', 45000.00, 0.00, 3060.00),
    (3, '2026-05-01', '2026-05-31', 120000.00, 24000.00, 21000.00),
    (4, '2026-05-01', '2026-05-31', 120000.00, 24000.00, 14000.00),
    (5, '2026-05-01', '2026-05-31', 120000.00, 24000.00, 21000.00);
