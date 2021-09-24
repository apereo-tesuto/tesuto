CREATE TABLE student_college_affiliation (
    eppn character varying(255) NOT NULL,
    student_ccc_id character varying(100) NOT NULL,
    mis_code character varying(100) NOT NULL,
    logged_date timestamp without time zone NOT NULL,
    CONSTRAINT student_college_affiliation_pkey PRIMARY KEY (eppn, student_ccc_id, mis_code)
);
