--drop function getPriceFunction(s INTEGER, e INTEGER, train INTEGER) ;

create or replace function getPriceFunction(
    s INTEGER, --START
    e INTEGER, --END
    train INTEGER -- TRAIN ID
) returns double precision
language plpgsql as $$
begin

DROP VIEW IF EXISTS findline;
CREATE VIEW findLine AS
SELECT * from LineInclude;

   --do
    declare
        f record;
        g record;
        t INTEGER = '0'; --total distance
        cost DOUBLE PRECISION;
        result DOUBLE PRECISION;
    begin
        for f in select station_a, station_b, miles from findLine
            limit 10
    loop
    --raise notice '% - % ', f.station_a, f.station_b;
       if (s = f.station_a AND s != e) THEN t = t + f.miles;
           s = f.station_b;
       end if;
    end loop;
    raise notice 't = %', t; --t = total distance from a to b + b to c + c to d.. etc

    for g in SELECT train_id, cost_per_mile from Trains
    loop
        if (g.train_id = train) THEN cost = g.cost_per_mile;
        end if;
    end loop;

    raise notice 'c = %', cost; --cost = cost of train number input per mile
    result = t * cost;
    raise notice 'total = %', result;
    return result;
    end;
end;
$$;