DROP TABLE IF EXISTS salary_payments CASCADE;
DROP TABLE IF EXISTS ticket_payments CASCADE;
DROP TABLE IF EXISTS tickets CASCADE;
DROP TABLE IF EXISTS flight_pilots CASCADE;
DROP TABLE IF EXISTS flights CASCADE;
DROP TABLE IF EXISTS fares CASCADE;
DROP TABLE IF EXISTS routes CASCADE;
DROP TABLE IF EXISTS work_schedules CASCADE;
DROP TABLE IF EXISTS employees CASCADE;
DROP TABLE IF EXISTS pay_schemes CASCADE;
DROP TABLE IF EXISTS passengers CASCADE;
DROP TABLE IF EXISTS aircraft CASCADE;
DROP TABLE IF EXISTS aircraft_types CASCADE;
DROP TABLE IF EXISTS airlines CASCADE;
DROP TABLE IF EXISTS airports CASCADE;
DROP TABLE IF EXISTS cities CASCADE;
DROP TABLE IF EXISTS countries CASCADE;

CREATE TABLE countries (
    country_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(80) NOT NULL UNIQUE,
    iso_code char(2) NOT NULL UNIQUE
);

CREATE TABLE cities (
    city_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    country_id integer NOT NULL REFERENCES countries(country_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    name varchar(80) NOT NULL,
    UNIQUE (country_id, name)
);

CREATE TABLE airports (
    airport_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    city_id integer NOT NULL REFERENCES cities(city_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    iata_code char(3) NOT NULL UNIQUE,
    name varchar(120) NOT NULL
);

CREATE TABLE airlines (
    airline_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    country_id integer NOT NULL REFERENCES countries(country_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    name varchar(120) NOT NULL UNIQUE
);

CREATE TABLE aircraft_types (
    aircraft_type_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    manufacturer varchar(80) NOT NULL,
    model varchar(80) NOT NULL,
    seat_capacity integer NOT NULL CHECK (seat_capacity > 0),
    UNIQUE (manufacturer, model)
);

CREATE TABLE aircraft (
    aircraft_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    airline_id integer NOT NULL REFERENCES airlines(airline_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    aircraft_type_id integer NOT NULL REFERENCES aircraft_types(aircraft_type_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    registration_number varchar(16) NOT NULL UNIQUE,
    manufacture_year integer NOT NULL CHECK (manufacture_year BETWEEN 1980 AND 2100)
);

CREATE TABLE passengers (
    passenger_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    passport_number varchar(20) NOT NULL UNIQUE,
    last_name varchar(60) NOT NULL,
    first_name varchar(60) NOT NULL,
    middle_name varchar(60),
    birth_date date NOT NULL CHECK (birth_date < CURRENT_DATE)
);

CREATE TABLE pay_schemes (
    pay_scheme_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(80) NOT NULL UNIQUE,
    base_rate numeric(12,2) NOT NULL CHECK (base_rate >= 0),
    hourly_rate numeric(12,2) NOT NULL CHECK (hourly_rate >= 0),
    flight_bonus numeric(12,2) NOT NULL DEFAULT 0 CHECK (flight_bonus >= 0),
    sale_percent numeric(5,2) NOT NULL DEFAULT 0 CHECK (sale_percent BETWEEN 0 AND 100)
);

CREATE TABLE employees (
    employee_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pay_scheme_id integer NOT NULL REFERENCES pay_schemes(pay_scheme_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    role varchar(20) NOT NULL CHECK (role IN ('cashier', 'pilot', 'manager')),
    personnel_number varchar(20) NOT NULL UNIQUE,
    last_name varchar(60) NOT NULL,
    first_name varchar(60) NOT NULL,
    middle_name varchar(60),
    hire_date date NOT NULL
);

CREATE TABLE work_schedules (
    schedule_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id integer NOT NULL REFERENCES employees(employee_id) ON UPDATE CASCADE ON DELETE CASCADE,
    work_date date NOT NULL,
    shift_start timestamp NOT NULL,
    shift_end timestamp NOT NULL,
    CHECK (shift_end > shift_start),
    UNIQUE (employee_id, work_date, shift_start)
);

CREATE TABLE routes (
    route_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    departure_airport_id integer NOT NULL REFERENCES airports(airport_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    arrival_airport_id integer NOT NULL REFERENCES airports(airport_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    route_code varchar(20) NOT NULL UNIQUE,
    distance_km integer NOT NULL CHECK (distance_km > 0),
    CHECK (departure_airport_id <> arrival_airport_id)
);

CREATE TABLE fares (
    fare_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    route_id integer NOT NULL REFERENCES routes(route_id) ON UPDATE CASCADE ON DELETE CASCADE,
    service_class varchar(20) NOT NULL CHECK (service_class IN ('economy', 'business')),
    valid_from date NOT NULL,
    valid_to date,
    price numeric(12,2) NOT NULL CHECK (price > 0),
    CHECK (valid_to IS NULL OR valid_to >= valid_from),
    UNIQUE (route_id, service_class, valid_from)
);

CREATE TABLE flights (
    flight_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    route_id integer NOT NULL REFERENCES routes(route_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    aircraft_id integer NOT NULL REFERENCES aircraft(aircraft_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    flight_number varchar(12) NOT NULL,
    scheduled_departure timestamp NOT NULL,
    scheduled_arrival timestamp NOT NULL,
    actual_departure timestamp,
    actual_arrival timestamp,
    status varchar(20) NOT NULL DEFAULT 'scheduled' CHECK (status IN ('scheduled', 'departed', 'arrived', 'cancelled')),
    CHECK (scheduled_arrival > scheduled_departure),
    CHECK (actual_arrival IS NULL OR actual_departure IS NULL OR actual_arrival > actual_departure),
    UNIQUE (flight_number, scheduled_departure)
);

CREATE TABLE flight_pilots (
    flight_id integer NOT NULL REFERENCES flights(flight_id) ON UPDATE CASCADE ON DELETE CASCADE,
    pilot_id integer NOT NULL REFERENCES employees(employee_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    crew_role varchar(20) NOT NULL CHECK (crew_role IN ('captain', 'copilot')),
    PRIMARY KEY (flight_id, pilot_id)
);

CREATE TABLE tickets (
    ticket_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    flight_id integer NOT NULL REFERENCES flights(flight_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    passenger_id integer NOT NULL REFERENCES passengers(passenger_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    cashier_id integer NOT NULL REFERENCES employees(employee_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    fare_id integer NOT NULL REFERENCES fares(fare_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    ticket_number varchar(24) NOT NULL UNIQUE,
    sale_timestamp timestamp NOT NULL,
    seat_number varchar(6) NOT NULL,
    price numeric(12,2) NOT NULL CHECK (price > 0),
    UNIQUE (flight_id, seat_number)
);

CREATE TABLE ticket_payments (
    payment_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ticket_id integer NOT NULL REFERENCES tickets(ticket_id) ON UPDATE CASCADE ON DELETE CASCADE,
    payment_timestamp timestamp NOT NULL,
    amount numeric(12,2) NOT NULL CHECK (amount > 0),
    payment_method varchar(20) NOT NULL CHECK (payment_method IN ('cash', 'card', 'online'))
);

CREATE TABLE salary_payments (
    salary_payment_id integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    employee_id integer NOT NULL REFERENCES employees(employee_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    period_start date NOT NULL,
    period_end date NOT NULL,
    base_amount numeric(12,2) NOT NULL CHECK (base_amount >= 0),
    hours_amount numeric(12,2) NOT NULL CHECK (hours_amount >= 0),
    bonus_amount numeric(12,2) NOT NULL CHECK (bonus_amount >= 0),
    total_amount numeric(12,2) GENERATED ALWAYS AS (base_amount + hours_amount + bonus_amount) STORED,
    CHECK (period_end >= period_start),
    UNIQUE (employee_id, period_start, period_end)
);

CREATE INDEX idx_flights_period ON flights(scheduled_departure);
CREATE INDEX idx_tickets_sale_period ON tickets(sale_timestamp);
CREATE INDEX idx_work_schedules_employee_date ON work_schedules(employee_id, work_date);
