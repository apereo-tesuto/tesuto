create sequence placement_event_log_seq;

create table placement_event_log (
	   placement_event_log_id int primary key default nextval('placement_event_log_seq'),
	   tracking_id varchar not null,
	   cccid varchar not null,
	   subject_area_id int,
	   subject_area_version_id int,
	   miscode varchar,
	   event varchar,
	   create_date timestamp not null,
	   message text
);

create index placement_event_log_tracking_ix on placement_event_log (tracking_id);

create index placement_event_log_cccid_ix on placement_event_log (cccid);

create index placement_event_log_miscode_ix on placement_event_log (miscode);


