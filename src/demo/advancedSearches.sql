-- single route trip search
-- find all routes that stop at the specified depart station and then at the specified
-- destination station on a specific day of the week.
-- must account for available seats
-- must be able to order in multiple ways

-- in this example, we will find routes that stop at station 2 and stop at station 4
-- on tuesdays

-- get route id from route_include where station_id == input and isStop is true

CREATE VIEW rt_stA AS
    SELECT *
    FROM RouteInclude
    WHERE station_id = '2' AND stop = 'true';

CREATE VIEW rt_stB AS
    SELECT *
    FROM RouteInclude
    WHERE station_id = '4' AND stop = 'true';

SELECT * from rt_stB;

CREATE VIEW twoStopMatch AS
    SELECT rt_stA.route_id AS routeID, rt_stA.station_id AS stationA, rt_stB.station_id AS stationB  from rt_stA
    INNER JOIN rt_stB ON rt_stA.route_id = rt_stB.route_id;

SELECT * from twoStopMatch;

DROP VIEW stopDayMatch;

-- kept trainID in the following view because it will be necessary to utilize when customers are
-- added and train tickets begin to be issued.
CREATE VIEW stopDayMatch AS
    SELECT twoStopMatch.routeID AS routeID, stationa, stationb, day, train_id FROM twoStopMatch
    INNER JOIN Route_Schedules ON twoStopMatch.routeID = Route_Schedules.routeid
    AND day = 'Tuesday';

-- the following returns all of the routes that stop at station a and station b on the given day
SELECT * from stopDayMatch;

-- combination route trip search
-- all route combinations that stop at the specified depart station and destination station
-- on a specific day of the week
-- must account for available seats
-- must be able to order in multiple ways

-- in this example, we must find all routes that stop at a and all routes that stop at b
-- then we must find the stops in which these two routes overlap
-- using station 2 and 4 on 'Tuesday'

CREATE VIEW rt_stA_combo AS
    SELECT *
    FROM RouteInclude
    WHERE station_id = '2' AND stop = 'true'; --example input

CREATE VIEW rt_stB_combo AS
    SELECT *
    FROM RouteInclude
    WHERE station_id = '4' AND stop = 'true'; --example input

SELECT * FROM RouteInclude; -- reference
SELECT * FROM rt_stA_combo; -- includes all lines that stop at station "A"
SELECT * FROM rt_stB_combo; -- includes all lines that stop at station "B"

CREATE VIEW rtA_combo AS
    SELECT RouteInclude.route_id AS routeID, RouteInclude.station_id AS station, RouteInclude.stop AS stop
    FROM RouteInclude
    INNER JOIN rt_stA_combo ON rt_stA_combo.route_id = RouteInclude.route_id
    AND RouteInclude.stop = 'true'; -- if the route only passes through the station, we cannot use it as a layover

CREATE VIEW rtB_combo AS
    SELECT RouteInclude.route_id AS routeID, RouteInclude.station_id AS station, RouteInclude.stop AS stop
    FROM RouteInclude
    INNER JOIN rt_stB_combo ON rt_stB_combo.route_id = RouteInclude.route_id
    AND RouteInclude.stop = 'true'; -- if the route only passes through the station, we cannot use it as a layover

SELECT * FROM rtA_combo; -- includes every stop from routes that stop at station A
SELECT * FROM rtB_combo; -- includes every stop from routes that stop at station B

--find all routes that stop at station AB on Tuesday
DROP VIEW day_StationA_combo;
CREATE VIEW day_StationA_combo AS
    SELECT rtA_combo.routeID AS route, station, day, time, train_id
    FROM rtA_combo
    INNER JOIN Route_Schedules ON rtA_combo.routeID = Route_Schedules.routeid
    AND Route_Schedules.day = 'Tuesday';

DROP VIEW day_StationB_combo;
CREATE VIEW day_StationB_combo AS
    SELECT rtB_combo.routeID AS route, station, day, time, train_id
    FROM rtB_combo
    INNER JOIN Route_Schedules ON rtB_combo.routeID = Route_Schedules.routeid
    AND Route_Schedules.day = 'Tuesday';

SELECT * FROM day_StationA_combo; --contains only the routes that stop at station a on tuesday
SELECT * FROM day_StationB_combo; --contains only the routes that stop at station b on tuesday

DROP VIEW comboMatch;
CREATE VIEW comboMatch AS
    SELECT day_StationA_combo.route AS routeA, day_StationB_combo.route AS routeB, day_StationA_combo.station AS layover_station
    FROM day_StationA_combo
    INNER JOIN day_StationB_combo ON day_StationA_combo.station = day_StationB_combo.station
    AND day_StationA_combo.route != day_StationB_combo.route; --removes single route trips

-- routea stops at station a and the layover_station
-- routeb stops at the layover_station and station b
SELECT * FROM comboMatch;

-- need to add seat functionality after customers are added and tickets are issued

-- need to add sorting functionalities


-- advanced search a
-- find all TRAINS that pass through a specific station at a specific day/time combination
DROP VIEW search_station_day;
CREATE VIEW search_station_day AS
    SELECT DISTINCT train_id, route_id
    FROM route_schedules NATURAL JOIN routeinclude
    WHERE route_schedules.day = 'Tuesday' AND route_schedules.time = '00:01:00'
        AND route_schedules.routeid = routeinclude.route_id
        AND routeinclude.station_id = '4';
SELECT * FROM search_station_day;

-- advanced search b
-- find the routes that travel more than one railline
DROP VIEW route_railline CASCADE;
-- all stations associated with each line
CREATE VIEW route_railline AS
    SELECT line_id, station_a AS station
    FROM lineinclude
    UNION
    SELECT line_id, station_b AS station
    FROM lineinclude;

DROP VIEW search_routes_over_multiple_lines;
CREATE VIEW search_routes_over_multiple_lines AS
    SELECT route_id
    FROM (
        SELECT route_id, COUNT(DISTINCT line_id) AS count
        FROM (
            SELECT line_id, route_id
            FROM route_railline LEFT OUTER JOIN routeinclude ON route_railline.station = routeinclude.station_id
            ORDER BY line_id ASC, route_id ASC) B
        GROUP BY route_id
        HAVING COUNT(DISTINCT line_id) > 1
        ORDER BY route_id ASC, count ASC) A;
SELECT * FROM search_routes_over_multiple_lines;


-- advanced search c
-- rank the trains that are scheduled for more than one route
-- ranking seems weird, but is correct: there are 290 trains that are scheduled for 6
-- routes, so the remaining trains with 5 scheduled routes are ranked 291st, the rest are scheduled for 1
DROP VIEW search_train_route_rank;
CREATE VIEW search_train_route_rank AS
    SELECT train_id, RANK() OVER (ORDER BY count DESC) AS rank
    FROM (
        SELECT train_id, COUNT(routeid) as count
        FROM route_schedules
        GROUP BY train_id) A
    ORDER BY rank ASC, train_id ASC;
SELECT * FROM search_train_route_rank;

-- advanced search d
-- find the routes that pass through the same stations but dont have the same stops
-- takes 3s on my device, need to check logic
DROP VIEW search_same_stations_dif_stops;
CREATE VIEW search_same_stations_dif_stops AS
    SELECT DISTINCT a.route_id
    FROM routeinclude a JOIN routeinclude b ON a.route_id = b.route_id
    WHERE a.station_id = b.station_id AND EXISTS (
        SELECT DISTINCT c.route_id
        FROM routeinclude c JOIN routeinclude b ON a.route_id = b.route_id
        WHERE c.stop != b.stop AND a.route_id = c.route_id)
    ORDER BY a.route_id ASC;
SELECT * FROM search_same_stations_dif_stops;

-- advanced search e
-- find any stations through which all trains pass through
-- another weird one: each station has 350 distinct train passes through, and
-- there are 350 trains in total. thus this will return all stations in the result
CREATE OR REPLACE VIEW all_trains_for_each_station AS
    SELECT station_id, train_id
    FROM Route_Schedules, RouteInclude;

SELECT * FROM all_trains_for_each_station;

CREATE OR REPLACE VIEW all_trains AS
    SELECT train_id FROM Trains;

SELECT * FROM all_trains;

CREATE OR REPLACE VIEW stations_with_train_count AS
    SELECT station_id, count(DISTINCT train_id)
    FROM all_trains_for_each_station
    GROUP BY station_id;

SELECT * FROM stations_with_train_count;

-- DROP VIEW stations_passed_by_all_trains;
-- CREATE VIEW stations_passed_by_all_trains AS
--     SELECT * FROM trains_and_stations AS x
--     WHERE NOT EXISTS (
--         (SELECT y.train_id FROM all_trains AS y)
--         EXCEPT
--         (SELECT z.train_id FROM trains_and_stations AS z
--             WHERE z.station_id = x.station_id)
--         );

-- this line takes forever to run, might need to check the logic of the view above
-- SELECT DISTINCT station_id FROM stations_passed_by_all_trains;

-- advanced search f
-- find all trains that do not stop at a specific station

-- (this is kind of weird one... i've tried to more than 10 random stations, and with
-- each station there will always be 350 DISTINCT trains stopped at it, therefore the
-- set difference always returns an empty set)

-- trains that DO stop at station 25
CREATE OR REPLACE VIEW trains_do_stop AS
    SELECT DISTINCT train_id
    FROM Route_Schedules, RouteInclude
    WHERE station_id = 25
      AND stop = TRUE;
-- all_trains - trains_do_stop
(SELECT train_id FROM Trains)
EXCEPT
(SELECT DISTINCT train_id FROM trains_do_stop);

-- advanced search g
-- find routes where they stop at least xx% (where xx is a number from 10-90) of the
-- stations from which they pass (eg route passing through 10 stops will stop at 5 will
-- be returned as a result of a 50% search

-- need help with this one...

CREATE OR REPLACE VIEW count_stop AS
    SELECT route_id, count(stop) AS stopped FROM routeinclude
    WHERE stop = true
    GROUP BY route_id;

CREATE OR REPLACE VIEW count_pass AS
    SELECT route_id, count(stop) AS passed FROM routeinclude
    GROUP BY route_id;

CREATE OR REPLACE VIEW routes_with_stops_percentage AS
    SELECT * FROM count_pass NATURAL JOIN count_stop;

DROP TABLE pass_and_stop_for_routes;
CREATE TABLE pass_and_stop_for_routes (
    route_id INTEGER,
    pass INTEGER,
    stop INTEGER,
    percent DOUBLE PRECISION DEFAULT -1
);

INSERT INTO pass_and_stop_for_routes (route_id, pass, stop)
SELECT * FROM routes_with_stops_percentage;

CREATE OR REPLACE PROCEDURE calc_percentage() AS
$$
    DECLARE
        curr_row RECORD;
    BEGIN

        FOR curr_row IN SELECT * FROM routes_with_stops_percentage
        LOOP
            UPDATE pass_and_stop_for_routes
            SET percent = (curr_row.stopped / curr_row.passed)
            WHERE route_id = curr_row.route_id;
        END LOOP;

    END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION advancedSearchG(num INTEGER)
RETURNS SETOF RECORD AS
$$
    BEGIN

        CALL calc_percentage();
        RETURN QUERY
            SELECT * FROM pass_and_stop_for_routes
            WHERE percent >= (num / 100);

    END;
$$ LANGUAGE plpgsql;

-- break down the procedure:
-- procedure: calculate percent & update the table
-- function: accept a num, find the matching rows, return

-- advanced search h
-- display the schedule of a route
-- for a specific route, list the days of departure, departure hours and trains that run it
SELECT * FROM Route_Schedules
    WHERE routeid = 590;

-- advanced search i
-- find the availability of a route at every stop on a specific day and time
SELECT route_id, station_id, day, time
    FROM Route_Schedules, RouteInclude
    WHERE Route_Schedules.routeid = 376
      AND RouteInclude.route_id = 376
      AND RouteInclude.stop = TRUE;