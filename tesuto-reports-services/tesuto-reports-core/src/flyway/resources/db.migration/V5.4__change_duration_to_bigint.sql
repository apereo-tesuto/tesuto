ALTER TABLE report_response_record ALTER duration SET DATA TYPE bigint;

DROP SEQUENCE report_response_record_duration_seq CASCADE;

ALTER TABLE report_response_record ALTER duration DROP NOT NULL;