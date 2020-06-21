CREATE TABLE college_attribute (
    college_id VARCHAR (100) NOT NULL PRIMARY KEY,
    english_placement_option VARCHAR (100) NOT NULL,
    esl_placement_option VARCHAR (100) NOT NULL,
    CONSTRAINT college_attribute_fkey FOREIGN KEY (college_id) REFERENCES college (ccc_id)
);