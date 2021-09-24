alter TABLE cb21 drop column created_by;
alter TABLE cb21 drop column updated_by;

insert into cb21 (cb21_code, level, created_on, updated_on)
select 'Y', 0, current_timestamp, current_timestamp;

insert into cb21 (cb21_code, level, created_on, updated_on)
select 'A', 1, current_timestamp, current_timestamp;

insert into cb21 (cb21_code, level, created_on, updated_on)
select 'B', 2, current_timestamp, current_timestamp;

insert into cb21 (cb21_code, level, created_on, updated_on)
select 'C', 3, current_timestamp, current_timestamp;

insert into cb21 (cb21_code, level, created_on, updated_on)
select 'D', 4, current_timestamp, current_timestamp;

insert into cb21 (cb21_code, level, created_on, updated_on)
select 'E', 5, current_timestamp, current_timestamp;

insert into cb21 (cb21_code, level, created_on, updated_on)
select 'F', 6, current_timestamp, current_timestamp;

insert into cb21 (cb21_code, level, created_on, updated_on)
select 'G', 7, current_timestamp, current_timestamp;

insert into cb21 (cb21_code, level, created_on, updated_on)
select 'H', 8, current_timestamp, current_timestamp;


