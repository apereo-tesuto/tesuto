ALTER TABLE placement_component ADD COLUMN subject_area_version integer;

ALTER TABLE ONLY placement_component
  ADD CONSTRAINT pc_cd_fkey FOREIGN KEY (subject_area_id)
  REFERENCES college_discipline(college_discipline_id);

CREATE INDEX placement_component_subject_area_idx ON placement_component (subject_area_id);
