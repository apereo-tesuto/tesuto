CREATE TABLE history_competency_attributes (
    competency_attribute_id integer NOT NULL,
    REV int4 not null,
    REVEND int4,
    REVTYPE int2,
    REVEND_TSTMP timestamp,
    
    competency_code character varying(20) NOT NULL,
    opt_in_multi_measure boolean,
    placement_component_mmap boolean,
    placement_component_assess boolean,
    use_self_reported_data_for_mm boolean,	
    
    -- english and esl attributes
	show_placement_to_esl boolean,
	show_placement_to_native_speaker boolean,
	highest_level_reading_course character varying(120) DEFAULT NULL,
	-- math attributes
	prereq_general_education character varying(120) DEFAULT NULL,
	prereq_statistics  character varying(120) DEFAULT NULL,
    
    competencyCode_MOD boolean,
    optInMultiMeasure_MOD boolean,
    placementComponentMmap_MOD boolean,
    placementComponentAssess_MOD boolean,
    useSelfReportedDataForMm_MOD boolean,
	showPlacementToEsl_MOD boolean,
	showPlacementToNativeSpeaker_MOD boolean,
	highestLevelReadingCourse_MOD boolean,
	prerequisiteGeneralEducation_MOD boolean,
	prerequisiteStatistics_MOD boolean,
    
    PRIMARY KEY (competency_attribute_id, REV)
);

ALTER TABLE history_college_discipline ADD COLUMN competency_attribute_id INT;
ALTER TABLE history_college_discipline ADD COLUMN competencyAttributes_MOD boolean;

