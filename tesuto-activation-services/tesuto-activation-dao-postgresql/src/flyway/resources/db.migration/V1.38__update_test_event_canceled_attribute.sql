ALTER TABLE test_event ADD COLUMN canceled boolean;
UPDATE test_event set canceled=FALSE WHERE canceled IS NULL;
ALTER TABLE test_event ALTER COLUMN canceled SET NOT NULL;
ALTER TABLE test_event ALTER COLUMN canceled SET DEFAULT false;