ALTER TABLE report_response_record DROP CONSTRAINT fk_attemtp_record_response_record;

TRUNCATE TABLE report_attempt_record;
TRUNCATE TABLE report_response_record;

TRUNCATE TABLE report_assessment_structure;

ALTER TABLE report_attempt_record ADD COLUMN total_duration bigint;
ALTER TABLE report_attempt_record ADD COLUMN total_duration_formatted varchar(20);
ALTER TABLE report_attempt_record ADD COLUMN results_by_column varchar;

ALTER TABLE report_response_record DROP CONSTRAINT report_response_record_pkey;

ALTER TABLE report_response_record DROP COLUMN attempt_id;
ALTER TABLE report_response_record ADD COLUMN response_attempt_id varchar(255);
ALTER TABLE report_response_record ADD CONSTRAINT fk_attempt_record_response_record FOREIGN KEY (response_attempt_id) REFERENCES report_attempt_record(attempt_id);

ALTER TABLE report_assessment_structure DROP CONSTRAINT report_assessment_structure_pkey;

ALTER TABLE report_assessment_structure DROP COLUMN assessment_scoped_identifier;

ALTER TABLE report_assessment_structure ADD COLUMN item_ref_identifier varchar(255) NOT NULL;

ALTER TABLE report_assessment_structure ALTER COLUMN item_id DROP NOT NULL;

ALTER TABLE report_response_record ADD COLUMN item_ref_identifier varchar(255) NOT NULL;
ALTER TABLE report_response_record ADD CONSTRAINT report_response_record_pkey PRIMARY KEY (response_attempt_id, item_ref_identifier, attempt_index, item_id);
CREATE INDEX idx_attempt_id ON report_response_record (response_attempt_id);

ALTER TABLE report_assessment_structure ADD CONSTRAINT report_assessment_structure_pkey PRIMARY KEY (assessment_id, item_id);
