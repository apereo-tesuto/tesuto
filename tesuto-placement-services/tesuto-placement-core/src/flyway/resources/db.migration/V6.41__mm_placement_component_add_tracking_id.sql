ALTER TABLE placement_component RENAME mmap_course_categories TO course_categories;
ALTER TABLE placement_component RENAME rules_id TO rule_id;
ALTER TABLE placement_component RENAME rules_set_id TO rule_set_id;
ALTER TABLE placement_component RENAME variables_set_id TO variable_set_id;
ALTER TABLE placement_component ADD COLUMN rule_set_row_id character varying(100);
ALTER TABLE placement_component ADD COLUMN row_number integer;
ALTER TABLE placement_component DROP COLUMN mmap_cb21_code;

ALTER TABLE placement_component ADD COLUMN tracking_id character varying(100);

--Dropping because need to support both assessment placement and multiple measure component
ALTER TABLE placement_component ALTER COLUMN variable_set_id DROP NOT NULL;
ALTER TABLE placement_component ALTER COLUMN rule_set_id DROP NOT NULL;
ALTER TABLE placement_component ALTER COLUMN rule_id DROP NOT NULL;