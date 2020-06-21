ALTER TABLE college_discipline_sequence
	ALTER COLUMN college_discipline_id DROP DEFAULT;

ALTER TABLE college_discipline_sequence_course
	ALTER COLUMN college_discipline_id DROP DEFAULT;

ALTER TABLE college_discipline_sequence_course
	ALTER COLUMN course_id DROP DEFAULT;

ALTER TABLE competency_group
	ALTER COLUMN course_id SET DATA TYPE integer;

ALTER TABLE competency_group_mapping
	ALTER COLUMN competency_group_id DROP DEFAULT;

ALTER TABLE college_discipline_audit
	ALTER COLUMN college_discipline_id DROP DEFAULT;

ALTER TABLE college_discipline_sequence_audit
	ALTER COLUMN college_discipline_id DROP DEFAULT;

ALTER TABLE college_discipline_sequence_course_audit
	ALTER COLUMN college_discipline_id DROP DEFAULT;

ALTER TABLE college_discipline_sequence_course_audit
	ALTER COLUMN course_id DROP DEFAULT;

ALTER TABLE course_audit
	ALTER COLUMN course_id DROP DEFAULT;

ALTER TABLE competency_group_audit
	ALTER COLUMN competency_group_id DROP DEFAULT;

ALTER TABLE competency_group_audit
	ALTER COLUMN course_id SET DATA TYPE integer;

ALTER TABLE competency_group_mapping_audit
	ALTER COLUMN competency_group_id DROP DEFAULT;