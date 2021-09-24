create table passcode (
  user_id varchar(100) not null,
  type integer not null,
  value varchar(100) not null,
  create_date timestamp without time zone not null,
  primary key (user_id, type)
);

create index passcode_value_idx on passcode (value);


