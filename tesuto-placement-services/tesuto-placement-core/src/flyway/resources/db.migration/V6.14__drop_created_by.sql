alter TABLE college_discipline_sequence_course drop column created_by;
alter TABLE college_discipline_sequence_course drop column created_on;
alter TABLE college_discipline_sequence drop column created_by;
alter TABLE college_discipline_sequence drop column created_on;
alter TABLE college_discipline drop column created_by;
alter TABLE college_discipline drop column created_on;
alter TABLE competency_group_mapping drop column created_by;
alter TABLE competency_group_mapping drop column created_on;
alter TABLE competency_group drop column created_by;
alter TABLE competency_group drop column created_on;
alter TABLE course drop column created_by;
alter TABLE course drop column created_on;

alter TABLE history_college_discipline_sequence_course drop column created_by;
alter TABLE history_college_discipline_sequence_course drop column created_on;
alter TABLE history_college_discipline_sequence drop column created_by;
alter TABLE history_college_discipline_sequence drop column created_on;
alter TABLE history_college_discipline drop column created_by;
alter TABLE history_college_discipline drop column created_on;
alter TABLE history_competency_group_mapping drop column created_by;
alter TABLE history_competency_group_mapping drop column created_on;
alter TABLE history_competency_group drop column created_by;
alter TABLE history_competency_group drop column created_on;
alter TABLE history_course drop column created_by;
alter TABLE history_course drop column created_on;


alter TABLE history_college_discipline_sequence_course drop column createdBy_MOD;
alter TABLE history_college_discipline_sequence_course drop column createdOn_MOD;
alter TABLE history_college_discipline_sequence drop column createdBy_MOD;
alter TABLE history_college_discipline_sequence drop column createdOn_MOD;
alter TABLE history_college_discipline drop column createdBy_MOD;
alter TABLE history_college_discipline drop column createdOn_MOD;
alter TABLE history_competency_group_mapping drop column createdBy_MOD;
alter TABLE history_competency_group_mapping drop column createdOn_MOD;
alter TABLE history_competency_group drop column createdBy_MOD;
alter TABLE history_competency_group drop column createdOn_MOD;
alter TABLE history_course drop column createdBy_MOD;
alter TABLE history_course drop column createdOn_MOD;

