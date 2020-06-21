ALTER TABLE test_location ADD COLUMN enabled boolean;
ALTER TABLE test_location ALTER COLUMN street_address_1 DROP NOT NULL;
ALTER TABLE test_location ALTER COLUMN street_address_2 DROP NOT NULL;
ALTER TABLE test_location ALTER COLUMN city DROP NOT NULL;
ALTER TABLE test_location ALTER COLUMN postal_code DROP NOT NULL;
