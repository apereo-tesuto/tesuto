CREATE TABLE activation (
  activation_id varchar(100) primary key,
  activated_for varchar(100) not null,
  assessment_id varchar(100) not null,
  created_by varchar(100),
  active bool not null default 't',
  create_date timestamp  not null,
  starts_at timestamp not null,
  expires_at timestamp not null,
  previous_version_id varchar(100) references activation on delete cascade
);

CREATE INDEX activation_activated_for
on activation(activated_for, assessment_id, active);

CREATE INDEX activation_previous_version_id
on activation(previous_version_id);


CREATE TABLE activation_assessment_setting (
  activation_id varchar(100) references activation on delete cascade,
  name varchar(100),
  value text
);
  
ALTER TABLE activation_assessment_setting
ADD CONSTRAINT activation_assessment_setting_pk PRIMARY KEY 
(activation_id, name);
