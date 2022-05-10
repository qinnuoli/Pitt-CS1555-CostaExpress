

CREATE OR REPLACE PROCEDURE exportData()
language plpgsql AS $$
begin

-- the following must be changed depending on the file path of the system. The export folder must allow write access
-- to either PostgreSQL and/or the user running the server.
-- The example presented is for Windows, but Mac users should change the directory to /tmp
-- and restart the server to ensure changes were applied.
COPY Customers TO '/Users/qinnuoli/temp/Customers.txt' DELIMITER '|' ;
COPY Clock TO '/Users/qinnuoli/temp/Clock.txt' DELIMITER '|' ;
COPY lineinclude TO '/Users/qinnuoli/temp/LineInclude.txt' DELIMITER '|' ;
COPY railroad_lines TO '/Users/qinnuoli/temp/railroad_lines.txt' DELIMITER '|' ;
COPY reservations TO '/Users/qinnuoli/temp/reservations.txt' DELIMITER '|' ;
COPY route_schedules TO '/Users/qinnuoli/temp/route_schedules.txt' DELIMITER '|' ;
COPY routeinclude TO '/Users/qinnuoli/temp/routeinclude.txt' DELIMITER '|' ;
COPY routes TO '/Users/qinnuoli/temp/routes.txt' DELIMITER '|' ;
COPY stations TO '/Users/qinnuoli/temp/stations.txt' DELIMITER '|' ;
COPY stop_seatcount TO '/Users/qinnuoli/temp/stop_seatcount.txt' DELIMITER '|' ;
COPY tickets TO '/Users/qinnuoli/temp/tickets.txt' DELIMITER '|' ;
COPY trains TO '/Users/qinnuoli/temp/trains.txt' DELIMITER '|' ;

end;
$$;

-- CALL exportData();

-- to import data:
-- COPY tickets FROM 'C:\Users\Sam\Desktop\CS1555_CostaExpress\export\tickets.txt' ( DELIMITER'|');

CREATE OR REPLACE PROCEDURE deleteData()

language plpgsql AS $$
begin

DELETE FROM Customers;
DELETE FROM Clock;
DELETE FROM lineinclude;
DELETE FROM railroad_lines;
DELETE FROM reservations;
DELETE FROM route_schedules;
DELETE FROM routeinclude;
DELETE FROM routes;
DELETE FROM stations;
DELETE FROM stop_seatcount;
DELETE FROM tickets;
DELETE FROM trains;


END;
$$;

-- CALL deleteData();

-- SELECT * FROM Customers;
-- SELECT * FROM Clock;
-- SELECT * FROM lineinclude;
-- SELECT * FROM railroad_lines;
-- SELECT * FROM reservations;
-- SELECT * FROM route_schedules;
-- SELECT * FROM routeinclude;
-- SELECT * FROM routes;
-- SELECT * FROM stations;
-- SELECT * FROM stop_seatcount;
-- SELECT * FROM tickets;
-- SELECT * FROM trains;



-- Create a trigger called line_disruption that adjusts all the
-- tickets to the immediate next line when a line is closed due
-- to an accident or maintenance, unless the customer has specified
-- no substitutions/adjustments in which case the ticket is cancelled.

-- add no adjustments attribute to reservations
ALTER TABLE reservations
ADD no_adjust boolean,  -- reads true if the customer has specified no adjustments
ADD start_station INTEGER,
ADD end_station INTEGER;

--SELECT * from reservations;
--SELECT * from Trains;
--SELECT * from lineinclude;
--SELECT * from stop_seatcount;
