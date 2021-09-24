-- Droping completion_date not null since we support expired assessmentSesions
ALTER TABLE report_attempt_record ALTER completion_date DROP NOT NULL;