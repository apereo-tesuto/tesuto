ALTER TABLE report_attempt_record ADD COLUMN college_id character varying(100);
ALTER TABLE report_attempt_record ADD COLUMN college_label character varying(100);

ALTER TABLE report_attempt_record ADD COLUMN student_ability double precision;
ALTER TABLE report_attempt_record ADD COLUMN points_scored double precision;
ALTER TABLE report_attempt_record ADD COLUMN percent_score double precision;
ALTER TABLE report_attempt_record ADD COLUMN odds_success double precision;
ALTER TABLE report_attempt_record ADD COLUMN average_item_difficulty double precision;
ALTER TABLE report_attempt_record ADD COLUMN item_difficulty_count double precision;
ALTER TABLE report_attempt_record ADD COLUMN reported_scale double precision;