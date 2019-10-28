alter table activation add column status_update_date timestamp;

update activation act set status_update_date = (select max(change_date) from activation_status_change ch where ch.activation_id = act.activation_id);

update activation set status_update_date = create_date where status_update_date is null;


