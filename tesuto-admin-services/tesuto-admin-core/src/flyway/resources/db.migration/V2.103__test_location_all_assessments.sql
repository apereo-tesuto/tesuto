ALTER TABLE test_location ADD COLUMN all_assessments boolean;
UPDATE test_location set enabled=TRUE WHERE enabled IS NULL;
ALTER TABLE test_location ALTER COLUMN enabled SET NOT NULL; 
ALTER TABLE test_location ALTER COLUMN enabled SET DEFAULT true;
UPDATE test_location set all_assessments=TRUE WHERE all_assessments IS NULL;
ALTER TABLE test_location ALTER COLUMN all_assessments SET NOT NULL; 
ALTER TABLE test_location ALTER COLUMN all_assessments SET DEFAULT true;
