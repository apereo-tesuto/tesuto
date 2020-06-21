alter table activation drop column location_id;
alter table activation add column location_id integer;

create index activation_user_location_idx on activation (user_id, location_id, status);
create index activation_location_date_idx on activation (location_id, start_date, end_date, status);
create index activation_location_status_idx on activation (location_id, status);


