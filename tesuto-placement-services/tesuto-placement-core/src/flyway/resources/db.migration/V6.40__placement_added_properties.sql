ALTER TABLE placement ADD COLUMN assigned_date  timestamp without time zone;
ALTER TABLE placement ADD COLUMN create_rule_set_id character varying(100);
ALTER TABLE placement ADD COLUMN assigned_rule_set_id character varying(100);

