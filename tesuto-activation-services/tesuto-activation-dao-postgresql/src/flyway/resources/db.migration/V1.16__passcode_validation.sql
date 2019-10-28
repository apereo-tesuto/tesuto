create table passcode_validation_attempt (
       passcode_validation_attempt_id bigserial primary key,
       activation_id varchar(100) not null references activation on delete cascade,
       is_successful boolean not null,
       passcode varchar(100) not null,
       user_id varchar(100), -- create of passcode, not null if successful
       validation_date timestamp without time zone not null
);

--more indexes may be required for reporting purposes
create index passcode_validation_attempt_activation_ix
on passcode_validation_attempt(activation_id);
