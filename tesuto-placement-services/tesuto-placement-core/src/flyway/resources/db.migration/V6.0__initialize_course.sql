CREATE SEQUENCE hibernate_sequence
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;

CREATE TABLE cb21 (
    cb21_code char(1) NOT NULL,
    level char(1) NOT NULL,
    created_by character varying(256) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    updated_by character varying(256) NOT NULL,
    updated_on timestamp without time zone NOT NULL,
    CONSTRAINT cb21_pkey PRIMARY KEY (cb21_code)
);

CREATE TABLE course (
    course_id serial,
    name character varying(120) NOT NULL,
    cid character varying(10) NOT NULL,
    description text,
    created_by character varying(256) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    updated_by character varying(256) NOT NULL,
    updated_on timestamp without time zone NOT NULL,
    CONSTRAINT course_pkey PRIMARY KEY (course_id)
);

CREATE TABLE college_discipline (
    college_discipline_id serial,
    college_id bigint NOT NULL,
    title character varying (120) NOT NULL,
    description text,
    competency_map_discipline character varying(256),
    sis_code character varying(100),
    created_by character varying(256) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    updated_by character varying(256) NOT NULL,
    updated_on timestamp without time zone NOT NULL,
    CONSTRAINT college_discipline_pkey PRIMARY KEY (college_discipline_id)
);

CREATE TABLE college_discipline_sequence (
    college_discipline_id serial,
    cb21_code char(1) NOT NULL,
    explanation text,
    show_student boolean,
    created_by character varying(256) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    updated_by character varying(256) NOT NULL,
    updated_on timestamp without time zone NOT NULL,
    CONSTRAINT college_discipline_sequence_pkey PRIMARY KEY (college_discipline_id, cb21_code)
);

ALTER TABLE ONLY college_discipline_sequence
ADD CONSTRAINT cds_cb21_fkey FOREIGN KEY (cb21_code) REFERENCES cb21(cb21_code);

ALTER TABLE ONLY college_discipline_sequence
ADD CONSTRAINT cds_cd_fkey FOREIGN KEY (college_discipline_id) REFERENCES college_discipline(college_discipline_id);

CREATE INDEX cds_cb21_idx ON college_discipline_sequence (cb21_code);

CREATE TABLE college_discipline_sequence_course (
    college_discipline_id serial,
    course_id serial,
    mis_code character varying(256),
    cb21_code char(1) NOT NULL,
    created_by character varying(256) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    updated_by character varying(256) NOT NULL,
    updated_on timestamp without time zone NOT NULL,
    CONSTRAINT college_discipline_sequence_course_pkey PRIMARY KEY (college_discipline_id, cb21_code, course_id)
);

ALTER TABLE ONLY college_discipline_sequence_course
ADD CONSTRAINT cdsc_cds_fkey FOREIGN KEY (college_discipline_id, cb21_code) REFERENCES college_discipline_sequence(college_discipline_id, cb21_code);

ALTER TABLE ONLY college_discipline_sequence_course
ADD CONSTRAINT cdsc_course_fkey FOREIGN KEY (course_id) REFERENCES course(course_id);

CREATE INDEX cdsc_course_idx ON college_discipline_sequence_course (course_id);

CREATE TABLE competency_group (
    competency_group_id serial,
    course_id bigint NOT NULL,
    probability_success integer,
    title character varying(120),
    created_by character varying(256) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    updated_by character varying(256) NOT NULL,
    updated_on timestamp without time zone NOT NULL,
    CONSTRAINT competency_group_pkey PRIMARY KEY (competency_group_id)
);

ALTER TABLE ONLY competency_group
ADD CONSTRAINT cg_course_fkey FOREIGN KEY (course_id) REFERENCES course(course_id);

CREATE TABLE competency_group_mapping (
    competency_group_id serial,
    competency_id character varying(100) NOT NULL,
    created_by character varying(256) NOT NULL,
    created_on timestamp without time zone NOT NULL,
    updated_by character varying(256) NOT NULL,
    updated_on timestamp without time zone NOT NULL,
    CONSTRAINT competency_group_mapping_pkey PRIMARY KEY (competency_group_id, competency_id)
);

ALTER TABLE ONLY competency_group_mapping
ADD CONSTRAINT cgm_cg_fkey FOREIGN KEY (competency_group_id) REFERENCES competency_group(competency_group_id);

CREATE TABLE college_discipline_audit (
    college_discipline_id serial,
    college_id bigint NOT NULL,
    title character varying (120) NOT NULL,
    description text,
    competency_map_discipline character varying(256),
    sis_code character varying(100),
    modified_by character varying(256) NOT NULL,
    modified_on timestamp without time zone NOT NULL,
    modified_type character varying(20),
    CONSTRAINT college_discipline_audit_pkey PRIMARY KEY (college_discipline_id, modified_on)
);

ALTER TABLE ONLY college_discipline_audit
ADD CONSTRAINT cda_cd_fkey FOREIGN KEY (college_discipline_id) REFERENCES college_discipline(college_discipline_id);

CREATE TABLE college_discipline_sequence_audit (
    college_discipline_id serial,
    cb21_code char(1) NOT NULL,
    explanation text,
    show_student boolean,
    modified_by character varying(256) NOT NULL,
    modified_on timestamp without time zone NOT NULL,
    modified_type character varying(20),
    CONSTRAINT college_discipline_sequence_audit_pkey PRIMARY KEY (college_discipline_id, cb21_code, modified_on)
);

ALTER TABLE ONLY college_discipline_sequence_audit
ADD CONSTRAINT cdsa_cds_fkey FOREIGN KEY (college_discipline_id, cb21_code) REFERENCES college_discipline_sequence(college_discipline_id, cb21_code);

CREATE TABLE college_discipline_sequence_course_audit (
    college_discipline_id serial,
    course_id serial,
    mis_code character varying(256),
    cb21_code char(1) NOT NULL,
    modified_by character varying(256) NOT NULL,
    modified_on timestamp without time zone NOT NULL,
    modified_type character varying(20),
    CONSTRAINT college_discipline_sequence_course_audit_pkey PRIMARY KEY (college_discipline_id, cb21_code, course_id, modified_on)
);

ALTER TABLE ONLY college_discipline_sequence_course_audit
ADD CONSTRAINT cdsca_cdsc_fkey FOREIGN KEY (college_discipline_id, cb21_code, course_id) REFERENCES college_discipline_sequence_course(college_discipline_id, cb21_code, course_id);

CREATE INDEX cdsca_course_idx ON college_discipline_sequence_course_audit (course_id);

CREATE TABLE course_audit (
    course_id serial,
    name character varying(120) NOT NULL,
    cid character varying(10) NOT NULL,
    description text,
    modified_by character varying(256) NOT NULL,
    modified_on timestamp without time zone NOT NULL,
    modified_type character varying(20),
    CONSTRAINT course_audit_pkey PRIMARY KEY (course_id, modified_on)
);

ALTER TABLE ONLY course_audit
ADD CONSTRAINT course_audit_course_fkey FOREIGN KEY (course_id) REFERENCES course(course_id);

CREATE TABLE competency_group_audit (
    competency_group_id serial,
    course_id bigint NOT NULL,
    probability_success integer,
    title character varying(120),
    modified_by character varying(256) NOT NULL,
    modified_on timestamp without time zone NOT NULL,
    modified_type character varying(20),
    CONSTRAINT competency_group_audit_pkey PRIMARY KEY (competency_group_id, modified_on)
);

ALTER TABLE ONLY competency_group_audit
ADD CONSTRAINT cga_cg_fkey FOREIGN KEY (competency_group_id) REFERENCES competency_group(competency_group_id);

CREATE TABLE competency_group_mapping_audit (
    competency_group_id serial,
    competency_id character varying(100) NOT NULL,
    modified_by character varying(256) NOT NULL,
    modified_on timestamp without time zone NOT NULL,
    modified_type character varying(20),
    CONSTRAINT competency_group_mapping_audit_pkey PRIMARY KEY (competency_group_id, competency_id, modified_on)
);

ALTER TABLE ONLY competency_group_mapping_audit
ADD CONSTRAINT cgma_cgm_fkey FOREIGN KEY (competency_group_id, competency_id) REFERENCES competency_group_mapping(competency_group_id, competency_id);

CREATE INDEX cgma_competency_idx ON competency_group_mapping_audit (competency_id);
