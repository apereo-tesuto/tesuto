CREATE TABLE accommodation (
  accommodation_id SERIAL PRIMARY KEY,
  code character varying(2) NOT NULL,
  name character varying(100) not NULL,
  description character varying(356) not NULL,
  created_on_date timestamp without time zone default (now() at time zone 'utc'),
  last_updated_date timestamp without time zone default (now() at time zone 'utc')
);

ALTER TABLE ONLY accommodation
ADD CONSTRAINT accommodation__code_unique UNIQUE (code);

ALTER TABLE ONLY accommodation
ADD CONSTRAINT accommodation__name_unique UNIQUE (name);
