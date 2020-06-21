 -- good thing we're not in production, yet!
delete from activation_assessment_session;
delete from activation;
delete from activation_attribute;
delete from activation_status_change;
alter table activation add column namespace varchar(100);
alter table activation add column assessment_identifier varchar(100);
create index activation_scoped_identifier_idx on activation(namespace, assessment_identifier);
alter table activation drop column  assessment_id;

