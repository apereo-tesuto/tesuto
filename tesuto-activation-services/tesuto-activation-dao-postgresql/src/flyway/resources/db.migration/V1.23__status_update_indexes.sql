drop index activation_location_date_idx;
create index activation_location_date_idx on activation
(location_id, status_update_date);
drop index activation_user_status_idx;
