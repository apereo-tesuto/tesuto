
ALTER TABLE placement_component ADD COLUMN subject_area_id integer;


-- there change to the requirement, so we need to drop these NOT NULL constraints
-- 1. placement component do not need to be associated with placement
-- 2. there could be a no course assignement
-- 3. placement component does not need to come from assessment

alter table placement_component alter placement_id DROP NOT NULL;
alter table placement_component alter mmap_cb21_code DROP NOT NULL;
alter table placement_component alter assessment_session_id DROP NOT NULL;
