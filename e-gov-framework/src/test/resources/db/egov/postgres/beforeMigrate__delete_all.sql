DO
$$
    DECLARE
r RECORD;
BEGIN
        -- if the schema you operate on is not "current", you will want to
        -- replace current_schema() in query with 'schematodeletetablesfrom'
        -- *and* update the generate 'DROP...' accordingly.
FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = 'public')
            LOOP
                IF r.tablename != 'flyway_schema_history' THEN
                    EXECUTE 'DROP TABLE IF EXISTS ' || quote_ident(r.tablename) || ' CASCADE';
END IF;
END LOOP;
END
$$;