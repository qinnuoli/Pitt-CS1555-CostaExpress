CREATE OR REPLACE FUNCTION cancel_unticketed_reserv()
RETURNS TRIGGER AS
$$
	DECLARE
		curr_row RECORD;
		reserv_time TIMESTAMP;
		clock_time TIMESTAMP;
	BEGIN
	    SELECT p_date INTO clock_time FROM clock;

		FOR curr_row IN SELECT reserv_no, time, valid FROM reservations
		LOOP
			SELECT to_timestamp(curr_row.time, 'YYYY-MM-DD HH24:MI:SS') INTO reserv_time;
			IF (extract(hours from reserv_time) - extract(hours from (clock_time)) <= 2)
			THEN
			    RAISE NOTICE '%', extract(hours from (reserv_time - clock_time));
				UPDATE reservations
				SET valid = false
				WHERE reserv_no = curr_row.reserv_no;
			END IF;
		END LOOP;
    RETURN NULL;
	END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS Reservation_Cancel on Clock;
CREATE TRIGGER Reservation_Cancel
	AFTER UPDATE OF p_date ON Clock
	EXECUTE PROCEDURE cancel_unticketed_reserv();

INSERT INTO reservations(reserv_no, customer_id, price, balance, route_num, day, time, train, valid)
VALUES (1, 1, 50, 20, 535099, 'Monday', '2022-02-22 12:10:00', 1, true);
SELECT * FROM reservations;

INSERT INTO clock(clock_key, p_date) VALUES (1, '2022-02-22 11:10:00');
SELECT * FROM clock;

UPDATE reservations
    SET time = '2022-02-22 14:10:00'
    WHERE reserv_no = 1;
UPDATE clock SET p_date = '2022-02-22 13:10:00' WHERE clock_key = 1;