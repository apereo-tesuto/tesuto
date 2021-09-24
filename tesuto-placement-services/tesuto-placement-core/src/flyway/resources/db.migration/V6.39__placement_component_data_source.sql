ALTER TABLE placement_component ADD COLUMN data_source character varying(256);
ALTER TABLE placement_component ADD COLUMN data_source_date timestamp without time zone;
ALTER TABLE placement_component ADD COLUMN data_source_type character varying(100);
