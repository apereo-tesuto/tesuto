ALTER TABLE activation ADD COLUMN assessment_type smallint;
ALTER TABLE activation ALTER assessment_type SET NOT NULL;

