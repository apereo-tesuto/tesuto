alter table activation add column activation_type varchar(100);
update activation set activation_type='Individual';
alter table activation alter activation_type set not null;
alter table activation alter start_date  drop not null;
alter table activation alter end_date  drop not null;
alter table activation alter delivery_type  drop not null;
