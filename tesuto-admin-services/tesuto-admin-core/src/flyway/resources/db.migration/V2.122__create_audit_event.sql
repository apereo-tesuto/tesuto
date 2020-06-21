CREATE TABLE audit_event (
  event_id bigint NOT NULL,
  username  character varying(64),
  cccid  character varying(255),
  user_agent  character varying(255),
  remote_address  character varying(255),
  roles  character varying(2048),
  colleges  character varying(2048),
  event_date  timestamp without time zone,
  event_type  character varying(255)
);

ALTER TABLE ONLY audit_event
ADD CONSTRAINT audit_event_pkey PRIMARY KEY (event_id);

CREATE INDEX audit_event_idx on audit_event(username, event_type, event_date); 

