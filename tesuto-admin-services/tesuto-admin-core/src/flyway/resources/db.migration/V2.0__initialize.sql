
CREATE SEQUENCE hibernate_sequence
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;


CREATE TABLE persistent_logins (
  username character varying(64) NOT NULL,
  series character varying(64) NOT NULL,
  token character varying(64) NOT NULL,
  last_used timestamp without time zone NOT NULL
);



CREATE TABLE security_group (
  security_group_id integer NOT NULL,
  created_on_date timestamp without time zone,
  last_updated_date timestamp without time zone,
  description character varying(255),
  group_name character varying(255)
);


CREATE TABLE security_group_security_permission (
  security_permission_id character varying(255) NOT NULL,
  security_group_id integer NOT NULL
);

CREATE TABLE security_permission (
  security_permission_id character varying(255) NOT NULL,
  created_on_date timestamp without time zone,
  last_updated_date timestamp without time zone,
  description character varying(255)
);


CREATE TABLE user_account (
  user_account_id bigint NOT NULL,
  created_on_date timestamp without time zone,
  last_updated_date timestamp without time zone,
  account_locked boolean,
  display_name character varying(255) NOT NULL,
  email_address character varying(255) NOT NULL,
  enabled boolean,
  expired boolean,
  failed_logins integer,
  last_login_date timestamp without time zone,
  password character varying(255),
  username character varying(255) NOT NULL,
  first_name character varying(255)
);


CREATE TABLE user_account_security_group (
  user_account_id bigint NOT NULL,
  security_group_id integer NOT NULL
);

ALTER TABLE ONLY persistent_logins
ADD CONSTRAINT persistent_logins_pkey PRIMARY KEY (series);


ALTER TABLE ONLY security_group
ADD CONSTRAINT security_group_pkey PRIMARY KEY (security_group_id);


ALTER TABLE ONLY security_group_security_permission
ADD CONSTRAINT security_group_security_permission_pkey PRIMARY KEY (security_group_id, security_permission_id);


ALTER TABLE ONLY security_permission
ADD CONSTRAINT security_permission_pkey PRIMARY KEY (security_permission_id);


ALTER TABLE ONLY user_account
ADD CONSTRAINT uk_castjbvpeeus0r8lbpehiu0e4 UNIQUE (username);


ALTER TABLE ONLY user_account
ADD CONSTRAINT uk_ow0prx6uum7eag14ciw6fgiqn UNIQUE (email_address);


ALTER TABLE ONLY user_account
ADD CONSTRAINT user_account_pkey PRIMARY KEY (user_account_id);

ALTER TABLE ONLY user_account_security_group
ADD CONSTRAINT user_account_security_group_pkey PRIMARY KEY (security_group_id, user_account_id);

ALTER TABLE ONLY security_group_security_permission
ADD CONSTRAINT fk_3bypa31ksoqh25wg8nur1ig00 FOREIGN KEY (security_permission_id) REFERENCES security_permission(security_permission_id);


ALTER TABLE ONLY user_account_security_group
ADD CONSTRAINT fk_7bxcp0xlw815mc0f81mojd0j1 FOREIGN KEY (user_account_id) REFERENCES user_account(user_account_id);


ALTER TABLE ONLY security_group_security_permission
ADD CONSTRAINT fk_d5e8h62eonem0sm3bf8wjinbx FOREIGN KEY (security_group_id) REFERENCES security_group(security_group_id);


ALTER TABLE ONLY user_account_security_group
ADD CONSTRAINT fk_gijnt1j1mh67ysgms35xblvvj FOREIGN KEY (security_group_id) REFERENCES security_group(security_group_id);


insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values ('STUDENT', timestamp 'now', timestamp 'now', 'Common role for all students');

-- Create the student group role
insert into "security_group" (security_group_id, created_on_date, last_updated_date, description, group_name) values (1, timestamp 'now', timestamp 'now', 'Student security group', 'STUDENT');

-- Relate these permissions and groups
insert into "security_group_security_permission" (security_permission_id, security_group_id) values ('STUDENT', 1);

insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values ('ADMIN', timestamp 'now', timestamp 'now', 'Common role for all admins');

-- Create the student group role
insert into "security_group" (security_group_id, created_on_date, last_updated_date, description, group_name) values (2, timestamp 'now', timestamp 'now', 'Admin security group', 'ADMIN');

-- Relate these permissions and groups
insert into "security_group_security_permission" (security_permission_id, security_group_id) values ('ADMIN', 2);

insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values ('PROCTOR', timestamp 'now', timestamp 'now', 'Common role for all proctors');

-- Create the proctor group role
insert into "security_group" (security_group_id, created_on_date, last_updated_date, description, group_name) values (3, timestamp 'now', timestamp 'now', 'Student security group', 'PROCTOR');

-- Relate these permissions and groups
insert into "security_group_security_permission" (security_permission_id, security_group_id) values ('PROCTOR', 3);

insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values ('COUNSELOR', timestamp 'now', timestamp 'now', 'Common role for all counselor');

-- Create the counselor group role
insert into "security_group" (security_group_id, created_on_date, last_updated_date, description, group_name) values (4, timestamp 'now', timestamp 'now', 'Counselor security group', 'COUNSELOR');

-- Relate these permissions and groups
insert into "security_group_security_permission" (security_permission_id, security_group_id) values ('COUNSELOR', 4);

insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values ('FACULTY', timestamp 'now', timestamp 'now', 'Common role for all faculty members');

-- Create the faculty group role
insert into "security_group" (security_group_id, created_on_date, last_updated_date, description, group_name) values (5, timestamp 'now', timestamp 'now', 'Faculty security group', 'FACULTY');

-- Relate these permissions and groups
insert into "security_group_security_permission" (security_permission_id, security_group_id) values ('FACULTY', 5);

insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values ('RESEARCHER', timestamp 'now', timestamp 'now', 'Common role for all researchers');

-- Create the researcher group role
insert into "security_group" (security_group_id, created_on_date, last_updated_date, description, group_name) values (6, timestamp 'now', timestamp 'now', 'Student security group', 'RESEARCHER');

-- Relate these permissions and groups
insert into "security_group_security_permission" (security_permission_id, security_group_id) values ('RESEARCHER', 6);

insert into "security_permission" (security_permission_id, created_on_date, last_updated_date, description) values ('CONFIGURATOR', timestamp 'now', timestamp 'now', 'Common role for all configurator');

-- Create the configurator group role
insert into "security_group" (security_group_id, created_on_date, last_updated_date, description, group_name) values (7, timestamp 'now', timestamp 'now', 'Configurator security group', 'CONFIGURATOR');

-- Relate these permissions and groups
insert into "security_group_security_permission" (security_permission_id, security_group_id) values ('CONFIGURATOR', 7);