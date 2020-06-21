alter table placement drop column notification_sent;
alter table placement drop column notification_success;
alter table placement_component drop column notification_sent;
alter table placement_component drop column notification_success;

create sequence placement_notification_id_seq;

create table placement_notification (
       placement_notification_id int primary key
                 default nextval('placement_notification_id_seq'),
       placement_id varchar(100)
                    references placement on delete cascade,
       notification_sent timestamp,
       notification_success boolean
);
create table placement_component_notification (
       placement_component_notification_id int primary key
                 default nextval('placement_notification_id_seq'),
       placement_component_id varchar(100)
                              references placement_component on delete cascade,
       notification_sent timestamp,
       notification_success boolean
);

