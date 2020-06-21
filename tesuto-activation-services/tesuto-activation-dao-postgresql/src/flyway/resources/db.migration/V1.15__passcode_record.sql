create table passcode_record (
  passcode_record_id  bigserial primary key,
  user_id varchar(100) not null,
  type integer not null,
  value varchar(100) not null,
  create_date timestamp without time zone not null
);
