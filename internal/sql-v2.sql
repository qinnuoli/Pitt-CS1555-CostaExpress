DROP TABLE IF EXISTS Stations, Distance, Railroad_lines, LineInclude, Routes, RouteInclude,
    Trains, Route_Schedules, Reservations, Customers, Clock, Tickets, Users CASCADE;

CREATE TABLE Stations (
    station_id INTEGER,
    name VARCHAR(32),
    opening_time TIME, -- format: HH:MM in 24 hrs
    closing_time TIME,
    stop_delay INTEGER, -- in minutes
    street VARCHAR(32),
    town VARCHAR(32),
    postalcode VARCHAR(32),

    CONSTRAINT Stations_PK PRIMARY KEY(station_id)
);

CREATE TABLE Distance (
    station_a INTEGER, -- use its station_id
    station_b INTEGER, -- use its station_id
    miles INTEGER,

    CONSTRAINT Distance_PK PRIMARY KEY(station_a, station_b),
    CONSTRAINT Distance_FK_A FOREIGN KEY(station_a) REFERENCES Stations(station_id) -- added fk from distance to station
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT Distance_FK_B FOREIGN KEY (station_b) REFERENCES Stations(station_id) -- added fk from distance to station
        ON UPDATE CASCADE ON DELETE CASCADE

);

CREATE TABLE Railroad_lines (
    line_id INTEGER,
    speed_limit INTEGER,
    -- removed station_a and station_b because i created a new table
    -- LineInclude to try to capture the many-to-many relationship

    CONSTRAINT Railroad_lines_PK PRIMARY KEY(line_id)
);

-- i created a new table to represent the many-to-many relationship
-- for the two lists in railroad lines: <Station list>, <Distance from previous
-- line station list>; however i'm not sure if this seems logical. please
-- let me know if you have a different idea!
CREATE TABLE LineInclude (
    line_id INTEGER,
    station_a INTEGER,
    station_b INTEGER,

    CONSTRAINT LineInclude_PK PRIMARY KEY(line_id, station_a, station_b),
    CONSTRAINT LineInclude_FK_A FOREIGN KEY(station_a) REFERENCES Stations(station_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT LineInclude_FK_B FOREIGN KEY(station_b) REFERENCES Stations(station_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT LineInclude_FK_C FOREIGN KEY(station_a, station_b) REFERENCES Distance(station_a, station_b)
        ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE Routes (
    route_id INTEGER,
    -- depart_station INTEGER, -- not present in the sample data
    -- dest_station INTEGER, -- not present in the sample data

    CONSTRAINT Routes_PK PRIMARY KEY(route_id)
--     CONSTRAINT Routes_FK_DepartStation FOREIGN KEY(depart_station) REFERENCES Stations(station_id)
--         ON UPDATE CASCADE ON DELETE CASCADE,
--     CONSTRAINT Routes_FK_DestStation FOREIGN KEY(dest_station) REFERENCES Stations(station_id)
--         ON UPDATE CASCADE ON DELETE CASCADE
);

-- a new table to represent the two lists in routes: <Station list> and
-- <Stop list>
CREATE TABLE RouteInclude (
    route_id INTEGER,
    station_id INTEGER,
    stop BOOLEAN, -- true if it's a stop, false otherwise

    CONSTRAINT RouteInclude_FK_Route FOREIGN KEY(route_id) REFERENCES Routes(route_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT RouteInclude_FK_Station FOREIGN KEY(station_id) REFERENCES Stations(station_id)
        ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE Trains ( -- moved train creation above schedule bc schedule now requires a train number
    train_id INTEGER,
    name VARCHAR(32),
    description VARCHAR(32),
    seats INTEGER,
    top_speed INTEGER, -- in km/h
    cost_per_km INTEGER, -- changed price_mi to cost_per_km to keep units consistent

    CONSTRAINT Trains_PK PRIMARY KEY(train_id)
);

CREATE TABLE Route_Schedules (
    -- rail_id INTEGER, -- not present in the sample data
    route_id INTEGER,
    day VARCHAR(32), -- can we use TIME somehow to get the day instead of storing it as plain string...?
    time TIME,
    train_id INTEGER, -- added this

    CONSTRAINT Route_Schedules_PK PRIMARY KEY(route_id, day, time, train_id)
--     CONSTRAINT Route_Schedules_FK_Route FOREIGN KEY(route_id) REFERENCES Routes(route_id)
--         ON UPDATE CASCADE ON DELETE CASCADE,
--     CONSTRAINT Route_Schedules_FK_Train FOREIGN KEY(train_id) REFERENCES Trains(train_id) -- new fk for train number
--         ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE Reservations (
    reserv_no INTEGER,
    price MONEY,
    payments MONEY,
    balance MONEY,

    CONSTRAINT Reservations_PK PRIMARY KEY(reserv_no)
);

-- changed the name from 'Passenger' to 'Customer' for consistency
CREATE TABLE Customers (
    customer_id INTEGER,
    first_name VARCHAR(32),
    last_name VARCHAR(32),
    -- email VARCHAR(32), -- not present in the sample data
    -- phone_no VARCHAR(32), -- not present in the sample data
    street VARCHAR(32),
    town VARCHAR(32),
    postalcode VARCHAR(32),

    CONSTRAINT Customers_PK PRIMARY KEY(customer_id)
);

CREATE TABLE Clock (
    p_date TIMESTAMP,

    CONSTRAINT Clock_PK PRIMARY KEY(p_date)
);

CREATE TABLE Tickets (
    ticket_no INTEGER,
    reserv_no INTEGER,

    CONSTRAINT Tickets_PK PRIMARY KEY(ticket_no),
    CONSTRAINT Tickets_FK_Reserv FOREIGN KEY(reserv_no) REFERENCES Reservations(reserv_no)
        ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE Users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(50) NOT NULL
);

-- code added to verify table data population:
-- SELECT * FROM Stations;
-- SELECT * FROM Distance;
-- SELECT * FROM Railroad_lines;
-- SELECT * FROM LineInclude;
-- SELECT * FROM Routes;
-- SELECT * FROM RouteInclude;
-- SELECT * FROM Trains;
-- SELECT * FROM Route_Schedules;
-- SELECT * FROM Reservations;
-- SELECT * FROM Customers;
-- SELECT * FROM Clock;
-- SELECT * FROM Tickets;
-- SELECT * FROM Users;