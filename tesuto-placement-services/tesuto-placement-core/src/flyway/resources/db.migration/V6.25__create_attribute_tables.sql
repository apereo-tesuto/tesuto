CREATE TABLE competency_attributes (
    competency_code character varying(20) NOT NULL,
    competency_attribute_id serial,
    opt_in_multi_measure boolean,
    placement_component_mmap boolean DEFAULT true,
    placement_component_assess boolean DEFAULT true,
    use_self_reported_data_for_mm boolean,	
	show_placement_to_esl boolean,
	show_placement_to_native_speaker boolean,
	highest_level_reading_course character varying(120) DEFAULT NULL,
	prereq_general_education character varying(120) DEFAULT NULL,
	prereq_statistics  character varying(120) DEFAULT NULL,
	
	primary key (competency_attribute_id)
);


ALTER TABLE college_discipline ADD COLUMN competency_attribute_id INT;

ALTER TABLE  college_discipline ADD CONSTRAINT fk_attribute 
   FOREIGN KEY (competency_attribute_id) REFERENCES competency_attributes(competency_attribute_id);


