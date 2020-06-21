-- To facilitate clean up after unit/integration tests

ALTER TABLE college_discipline_sequence DROP CONSTRAINT cds_cb21_fkey;

ALTER TABLE ONLY college_discipline_sequence
ADD CONSTRAINT cds_cb21_fkey FOREIGN KEY (cb21_code) REFERENCES cb21(cb21_code)
ON DELETE CASCADE;

ALTER TABLE college_discipline_sequence DROP CONSTRAINT cds_cd_fkey;

ALTER TABLE ONLY college_discipline_sequence 
ADD CONSTRAINT cds_cd_fkey FOREIGN KEY (college_discipline_id) REFERENCES college_discipline(college_discipline_id)
ON DELETE CASCADE;

ALTER TABLE college_discipline_sequence_course DROP CONSTRAINT  cdsc_cds_fkey;

ALTER TABLE ONLY college_discipline_sequence_course
ADD CONSTRAINT cdsc_cds_fkey FOREIGN KEY (college_discipline_id, cb21_code) REFERENCES college_discipline_sequence(college_discipline_id, cb21_code)
ON DELETE CASCADE;

ALTER TABLE college_discipline_sequence_course DROP CONSTRAINT cdsc_course_fkey;

ALTER TABLE ONLY college_discipline_sequence_course
ADD CONSTRAINT cdsc_course_fkey FOREIGN KEY (course_id) REFERENCES course(course_id)
ON DELETE CASCADE;


ALTER TABLE competency_group DROP CONSTRAINT  cg_course_fkey;

ALTER TABLE ONLY competency_group
ADD CONSTRAINT cg_course_fkey FOREIGN KEY (course_id) REFERENCES course(course_id)
ON DELETE CASCADE;


ALTER TABLE competency_group_mapping DROP CONSTRAINT cgm_cg_fkey;

ALTER TABLE ONLY competency_group_mapping
ADD CONSTRAINT cgm_cg_fkey FOREIGN KEY (competency_group_id) REFERENCES competency_group(competency_group_id)
ON DELETE CASCADE;


