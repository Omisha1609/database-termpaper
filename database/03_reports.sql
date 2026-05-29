-- Параметры периода в примерах: [2026-05-01, 2026-06-01).

-- 1. Объем перевозок за период по маршрутам.
SELECT
    r.route_code,
    dep_city.name AS departure_city,
    arr_city.name AS arrival_city,
    COUNT(t.ticket_id) AS passenger_count,
    SUM(t.price) AS revenue
FROM tickets t
JOIN flights f ON f.flight_id = t.flight_id
JOIN routes r ON r.route_id = f.route_id
JOIN airports dep_airport ON dep_airport.airport_id = r.departure_airport_id
JOIN airports arr_airport ON arr_airport.airport_id = r.arrival_airport_id
JOIN cities dep_city ON dep_city.city_id = dep_airport.city_id
JOIN cities arr_city ON arr_city.city_id = arr_airport.city_id
WHERE f.scheduled_departure >= DATE '2026-05-01'
  AND f.scheduled_departure < DATE '2026-06-01'
GROUP BY r.route_code, dep_city.name, arr_city.name
ORDER BY revenue DESC;

-- 2. Объем перевозок за период по странам назначения.
SELECT
    arr_country.name AS arrival_country,
    COUNT(t.ticket_id) AS passenger_count,
    SUM(t.price) AS revenue
FROM tickets t
JOIN flights f ON f.flight_id = t.flight_id
JOIN routes r ON r.route_id = f.route_id
JOIN airports arr_airport ON arr_airport.airport_id = r.arrival_airport_id
JOIN cities arr_city ON arr_city.city_id = arr_airport.city_id
JOIN countries arr_country ON arr_country.country_id = arr_city.country_id
WHERE f.scheduled_departure >= DATE '2026-05-01'
  AND f.scheduled_departure < DATE '2026-06-01'
GROUP BY arr_country.name
ORDER BY revenue DESC;

-- 3. Объем перевозок за период по пунктам вылета.
SELECT
    dep_airport.iata_code AS departure_airport,
    dep_city.name AS departure_city,
    COUNT(t.ticket_id) AS passenger_count,
    SUM(t.price) AS revenue
FROM tickets t
JOIN flights f ON f.flight_id = t.flight_id
JOIN routes r ON r.route_id = f.route_id
JOIN airports dep_airport ON dep_airport.airport_id = r.departure_airport_id
JOIN cities dep_city ON dep_city.city_id = dep_airport.city_id
WHERE f.scheduled_departure >= DATE '2026-05-01'
  AND f.scheduled_departure < DATE '2026-06-01'
GROUP BY dep_airport.iata_code, dep_city.name
ORDER BY passenger_count DESC, revenue DESC;

-- 4. Объем перевозок за период по пунктам назначения.
SELECT
    arr_airport.iata_code AS arrival_airport,
    arr_city.name AS arrival_city,
    COUNT(t.ticket_id) AS passenger_count,
    SUM(t.price) AS revenue
FROM tickets t
JOIN flights f ON f.flight_id = t.flight_id
JOIN routes r ON r.route_id = f.route_id
JOIN airports arr_airport ON arr_airport.airport_id = r.arrival_airport_id
JOIN cities arr_city ON arr_city.city_id = arr_airport.city_id
WHERE f.scheduled_departure >= DATE '2026-05-01'
  AND f.scheduled_departure < DATE '2026-06-01'
GROUP BY arr_airport.iata_code, arr_city.name
ORDER BY passenger_count DESC, revenue DESC;

-- 5. Объем перевозок за период по типам самолетов.
SELECT
    at.manufacturer,
    at.model,
    COUNT(t.ticket_id) AS passenger_count,
    SUM(t.price) AS revenue
FROM tickets t
JOIN flights f ON f.flight_id = t.flight_id
JOIN aircraft a ON a.aircraft_id = f.aircraft_id
JOIN aircraft_types at ON at.aircraft_type_id = a.aircraft_type_id
WHERE f.scheduled_departure >= DATE '2026-05-01'
  AND f.scheduled_departure < DATE '2026-06-01'
GROUP BY at.manufacturer, at.model
ORDER BY revenue DESC;

-- 6. Зарплата работников за период с детализацией схемы оплаты.
SELECT
    e.personnel_number,
    e.role,
    e.last_name || ' ' || e.first_name AS employee_name,
    ps.name AS pay_scheme,
    sp.period_start,
    sp.period_end,
    sp.base_amount,
    sp.hours_amount,
    sp.bonus_amount,
    sp.total_amount
FROM salary_payments sp
JOIN employees e ON e.employee_id = sp.employee_id
JOIN pay_schemes ps ON ps.pay_scheme_id = e.pay_scheme_id
WHERE sp.period_start = DATE '2026-05-01'
  AND sp.period_end = DATE '2026-05-31'
ORDER BY sp.total_amount DESC;

-- 7. Контроль качества расписания: отклонение фактического вылета от планового.
SELECT
    f.flight_number,
    r.route_code,
    f.scheduled_departure,
    f.actual_departure,
    ROUND(EXTRACT(EPOCH FROM (f.actual_departure - f.scheduled_departure)) / 60, 1) AS departure_delay_minutes
FROM flights f
JOIN routes r ON r.route_id = f.route_id
WHERE f.actual_departure IS NOT NULL
  AND f.scheduled_departure >= DATE '2026-05-01'
  AND f.scheduled_departure < DATE '2026-06-01'
ORDER BY departure_delay_minutes DESC;
