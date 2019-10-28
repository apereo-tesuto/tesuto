-- adding course group DisciplineSequence
ALTER TABLE college_discipline_sequence
  ADD COLUMN course_group int DEFAULT 1 NOT NULL;

-- drop foreign key before alter primary key
ALTER TABLE college_discipline_sequence_course DROP CONSTRAINT cdsc_cds_fkey;

ALTER TABLE college_discipline_sequence
  DROP CONSTRAINT college_discipline_sequence_pkey;
ALTER TABLE college_discipline_sequence
  ADD CONSTRAINT college_discipline_sequence_pkey
  PRIMARY KEY (college_discipline_id, cb21_code, course_group);

-- fix history tables
ALTER TABLE history_college_discipline_sequence
  ADD COLUMN course_group int DEFAULT 1 NOT NULL;

ALTER TABLE history_college_discipline_sequence
  DROP CONSTRAINT history_college_discipline_sequence_pkey;
ALTER TABLE history_college_discipline_sequence
  ADD CONSTRAINT history_college_discipline_sequence_pkey
  PRIMARY KEY (cb21_code, course_group, college_discipline_id, REV);


-- adding course group DisciplineSequenceCourse (to mapping table between course and sequence)
ALTER TABLE college_discipline_sequence_course
  ADD COLUMN course_group int DEFAULT 1 NOT NULL;

-- alter primary key and foreign key
ALTER TABLE college_discipline_sequence_course
  DROP CONSTRAINT college_discipline_sequence_course_pkey;
ALTER TABLE college_discipline_sequence_course
  ADD CONSTRAINT college_discipline_sequence_course_pkey
  PRIMARY KEY (college_discipline_id, cb21_code, course_group, course_id);
ALTER TABLE ONLY college_discipline_sequence_course
  ADD CONSTRAINT cdsc_cds_fkey FOREIGN KEY (college_discipline_id, cb21_code, course_group)
  REFERENCES college_discipline_sequence(college_discipline_id, cb21_code, course_group)
  ON DELETE CASCADE;


-- modify histroy table accordingly.
ALTER TABLE history_college_discipline_sequence_course
  ADD COLUMN course_group int DEFAULT 1 NOT NULL;

ALTER TABLE history_college_discipline_sequence_course
  DROP CONSTRAINT history_college_discipline_sequence_course_pkey;
ALTER TABLE history_college_discipline_sequence_course
  ADD CONSTRAINT history_college_discipline_sequence_course_pkey
  PRIMARY KEY (cb21_code, course_group, course_id, college_discipline_id, REV);


-- adding new column table COURSE and associated history table
ALTER TABLE course
  ADD COLUMN mmap_equivalent_code character varying(20);
ALTER TABLE course
  ADD COLUMN sis_test_code character varying(100);

ALTER TABLE history_course
  ADD COLUMN mmap_equivalent_code character varying(20);
ALTER TABLE history_course
  ADD COLUMN mmapEquivalentCode_MOD boolean;
ALTER TABLE history_course
  ADD COLUMN sis_test_code character varying(100);
ALTER TABLE history_course
  ADD COLUMN sisTestCode_MOD boolean;


-- changing column name in CB21 table to be more descriptive
ALTER TABLE ONLY cb21 RENAME COLUMN level TO levels_below_transfer;

ALTER TABLE ONLY history_cb21 RENAME COLUMN level TO levels_below_transfer;
ALTER TABLE ONLY history_cb21 RENAME COLUMN level_MOD TO levelsbelowtransfer_MOD;
