CREATE TABLE district (
  ccc_id character varying(100) NOT NULL,
  name character varying(100) not null,
  street_address character varying(100) not null,
  city character varying(64) not null,
  postal_code character varying(10) not null, 
  url character varying(100),
  created_date timestamp without time zone default (now() at time zone 'utc'),
  last_updated_date timestamp without time zone default (now() at time zone 'utc')
);

CREATE TABLE college (
  ccc_id character varying(100) NOT NULL,
  district_ccc_id character varying(100) NOT NULL,
  name character varying(100) not null,
  street_address_1 character varying(100) not null,
  street_address_2 character varying(100),
  city character varying(64) not null,
  postal_code character varying(10) not null, 
  url character varying(100),
  created_date timestamp without time zone default (now() at time zone 'utc'),
  last_updated_date timestamp without time zone default (now() at time zone 'utc')
);

CREATE TABLE test_location (
  id integer NOT NULL,
  college_ccc_id character varying(100) NOT NULL,
  name character varying(100) not null,
  street_address_1 character varying(100) not null,
  street_address_2 character varying(100) not null,
  city character varying(64) not null,
  postal_code character varying(10) not null,
  location_type character varying(20) not null,
  location_status character varying(20),
  capacity integer default 0,
  created_date timestamp without time zone default (now() at time zone 'utc'),
  last_updated_date timestamp without time zone default (now() at time zone 'utc')
);

ALTER TABLE ONLY district
ADD CONSTRAINT district_pkey PRIMARY KEY (ccc_id);

ALTER TABLE ONLY college
ADD CONSTRAINT college_pkey PRIMARY KEY (ccc_id);

ALTER TABLE ONLY test_location
ADD CONSTRAINT test_location_pkey PRIMARY KEY (id);

ALTER TABLE ONLY college
ADD CONSTRAINT fk_college_district FOREIGN KEY (district_ccc_id) REFERENCES district(ccc_id);

ALTER TABLE ONLY test_location
ADD CONSTRAINT fk_test_location_college FOREIGN KEY (college_ccc_id) REFERENCES college(ccc_id);

