-- Added a new table relationship to track the seats available when a train is at a specific station on a specific day/time.

drop table if exists stop_SeatCount CASCADE ;
CREATE TABLE stop_SeatCount (
    route_id INTEGER,
    station_number INTEGER,
    seat_count INTEGER,
    day VARCHAR(32),
    time TIME,
    train_id INTEGER
);

--SELECT * from stop_SeatCount; -- new table

CREATE OR REPLACE VIEW seats_per_Train_2 AS
    SELECT RouteInclude.route_id AS route_num,
           day, time, train_id, station_id FROM route_schedules
    INNER JOIN RouteInclude ON routeinclude.route_id = route_schedules.routeid AND
                               RouteInclude.stop = 'true';

--select * from seats_per_Train_2; -- to populate station numbers

INSERT INTO stop_SeatCount(route_id, day, time, train_id, station_number)
SELECT route_num, day, time, train_id, station_id FROM seats_per_Train_2;

--SELECT * from stop_SeatCount; -- new table

CREATE OR REPLACE VIEW seats_per_Train AS
    SELECT DISTINCT trains.train_id, seats FROM Trains
    INNER JOIN stop_SeatCount sSC on trains.train_id = sSC.train_id;

--SELECT * FROM seats_per_Train; -- view that reflects the seats/train

UPDATE stop_SeatCount --update seat count for each relationship
SET seat_count = seats_per_Train.seats,
    day = day,
    time = time,
    route_id = route_id
FROM seats_per_Train
WHERE seats_per_Train.train_id = stop_SeatCount.train_id;

--SELECT * from stop_SeatCount; -- view completely populated new table

---------------------------------------------------------

-- the following function allow the new reservation option to
-- update the seatcount of a specific train at a specific stop
-- on a specific time/route/day combination, depending on the
-- stops that were entered at the time of reservation

create or replace procedure updateSeatCountFunction(
            s INTEGER,
            rtid INTEGER,
            inputday VARCHAR,
            inputtime TIME
)
 language plpgsql AS $$

begin

    declare
        f record;

        begin
        raise notice 's = %', s;
        raise notice 'rtid = %', rtid;
        raise notice 'inputday = %', inputday;
        raise notice 'inputtime = %', inputtime;
        for f in select route_id, station_number, seat_count, day, time, train_id from stop_seatcount

        loop
            if (s = f.station_number AND
                rtid = f.route_id AND
                inputday = f.day AND
                inputtime = f.time) THEN
                    raise notice 'yes';
                    UPDATE stop_seatcount SET seat_count = stop_seatcount.seat_count-1
                        WHERE stop_seatcount.station_number = f.station_number
                        AND stop_seatcount.day = f.day
                        AND stop_seatcount.time = f.time;
            end if;
        end loop;
    end;

end;
$$;

--CALL updateSeatCountFunction ( --WORKING!!!
--    2,
--    157,
--   'Tuesday'::varchar,
--   '09:36:00'::time
--  );


-- select * from stop_seatcount WHERE route_id = '157' AND day = 'Tuesday' AND time = '09:36:00';


create or replace procedure updateRouteSeatCountFunction(
    s INTEGER, -- START
    e INTEGER, -- END
    rtid INTEGER,
    inputday VARCHAR,
    inputtime TIME
)
language plpgsql as $$
begin

    declare
        f record;

    begin for f in select station_a, station_b, miles from lineinclude
        loop
            if (s = f.station_a AND s != e) THEN CALL updateSeatCountFunction(s, rtid, inputday, inputtime);
                s = f.station_b;
            end if;
        end loop;
    end;

end;
$$;


---------------------------------------------------------

-- java code calls creation of views
-- CREATE OR REPLACE VIEW rt_stA AS
--     SELECT *
--     FROM RouteInclude
--     WHERE station_id = 'startA.getText()' AND stop = 'true';

-- as CREATE OR REPLACE VIEW rt_stB AS
--     SELECT *
--     FROM RouteInclude
--     WHERE station_id = 'endB.getText()' AND stop = 'true';

-- then it calls CALL singleRouteTripFunction();

-- then it calls
-- CREATE VIEW stopDayMatch AS
--     SELECT twoStopMatch.routeID AS routeID, stationa, stationb, day, time, train_id FROM twoStopMatch
--     INNER JOIN Route_Schedules ON twoStopMatch.routeID = Route_Schedules.routeid
--     AND day = 'day.getText()';

--

create or replace procedure singleRouteTripFunction ()
language plpgsql AS $$
begin

    CREATE OR REPLACE VIEW twoStopMatch AS
        SELECT rt_stA.route_id AS routeID, rt_stA.station_id AS stationA, rt_stB.station_id AS stationB  from rt_stA
        INNER JOIN rt_stB ON rt_stA.route_id = rt_stB.route_id;

end;
$$;


-- select * from stopDayMatch;

create or replace procedure routeWithSeatsFunction()
language plpgsql AS $$
begin
    CREATE OR REPLACE VIEW routesWithSeats AS
        SELECT DISTINCT stopDayMatch.routeID AS routeID, stopDayMatch.time, stopDayMatch.day, stopDayMatch.train_id, stop_SeatCount.seat_count, stopdaymatch.stationa,
                    stopdaymatch.stationb from stopDayMatch
        INNER JOIN stop_seatcount on stopDayMatch.routeID = stop_SeatCount.route_id
        AND stopDayMatch.day = stop_SeatCount.day
        AND stopDayMatch.time = stop_SeatCount.time
        AND stopDayMatch.train_id = stop_SeatCount.train_id
        AND stop_SeatCount.seat_count > 0;

    -- sort price functionality
    -- find train with lowest price
    CREATE OR REPLACE VIEW priceFunctionality AS
        SELECT routeid, time, day, routeswithseats.train_id, seat_count, stationa, stationb, trains.cost_per_mile AS price from routeswithseats
        INNER JOIN trains on trains.train_id = routeswithseats.train_id
        ORDER BY price;

    -- find train with shortest time(using global train speed)
    CREATE OR REPLACE VIEW timeFunctionality AS
        SELECT routeid, time, day, routeswithseats.train_id, seat_count, stationa, stationb, top_speed from routeswithseats
        INNER JOIN trains on trains.train_id = routeswithseats.train_id
        ORDER BY top_speed DESC;

    CREATE OR REPLACE VIEW fewestStopFunctionality AS
    SELECT route_id, COUNT(route_id) AS stopcount
        FROM routeinclude
        WHERE stop = 'true'
        GROUP BY route_id;

    CREATE OR REPLACE VIEW fewestStopsView AS
    SELECT routeswithseats.routeid, time, day, train_id, seat_count, stationa, stationb, stopcount from feweststopfunctionality
    INNER JOIN routeswithseats on routeswithseats.routeid = feweststopfunctionality.route_id
    ORDER BY stopcount;

    CREATE OR REPLACE VIEW fewestStationsFunctionality AS
    SELECT route_id, COUNT(route_id) AS stationcount
    FROM routeinclude
    GROUP BY route_id;

    CREATE OR REPLACE VIEW fewestStationsView AS
    SELECT routeswithseats.routeid, time, day, train_id, seat_count, stationa, stationb, stationcount from feweststationsfunctionality
    INNER JOIN routeswithseats on routeswithseats.routeid = feweststationsfunctionality.route_id
    ORDER BY stationcount;

end;
$$;


--select * FROM routesWithSeats; -- use for general route from station a - station b search, to verify there are enough seats

--select * from priceFunctionality; -- use to get return screen for single search by price

--select * from timeFunctionality; -- use to get return screen for single search by speed

--select * from fewestStopFunctionality; -- used for functionality, do not call

--select * from fewestStopsView; -- use to get return screen for single search by fewest stops

--select * from fewestStationsFunctionality; -- used for functionality, do not call

--select * from fewestStationsView; -- used to get return screen for fewest stations





