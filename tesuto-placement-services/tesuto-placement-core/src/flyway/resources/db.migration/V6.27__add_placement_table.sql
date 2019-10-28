CREATE TABLE placement(
    id character varying(100) NOT NULL primary key,
    cccid character varying(100) NOT NULL,
    cb21_code char(1),
    course_group integer,
    subject_area_name character varying(100),
    subject_area_version int,
    college_id character varying(100) NOT NULL,
    notification_sent timestamp without time zone,
    notification_success boolean,
    is_assigned boolean,
    created_on timestamp without time zone
);

CREATE INDEX p_cccid_college_idx on placement(cccid, college_id);
CREATE INDEX p_cccid_college_subject_idx on placement (college_id, subject_area_name, cccid);

CREATE TABLE placement_component (
    id character varying(100) NOT NULL primary key,
    placement_component_type character varying(15) NOT NULL,
    cccid character varying(100) NOT NULL,
    cb21_code char(1),
    course_group integer,
    college_id character varying(100) NOT NULL,
    placement_id  character varying(100) NOT NULL,
    trigger_data character varying(100) NOT NULL,
    assessment_session_id  character varying(100) NOT NULL,
    student_ability double precision,
    mmap_cb21_code character varying(100) NOT NULL,
    mmap_course_categories text,
    notification_sent timestamp without time zone,
    notification_success boolean,
    rules_id  character varying(100) NOT NULL,
    rules_set_id character varying(100) NOT NULL,
    variables_set_id character varying(100) NOT NULL,
    created_on timestamp without time zone
);

CREATE INDEX pc_placement_idx on placement_component(placement_id);

ALTER TABLE ONLY placement_component ADD CONSTRAINT fk_placement
   FOREIGN KEY (placement_id) REFERENCES placement(id);

ALTER TABLE ONLY placement_component
    ADD CONSTRAINT pc_cb21_fkey FOREIGN KEY (cb21_code) REFERENCES cb21(cb21_code);




