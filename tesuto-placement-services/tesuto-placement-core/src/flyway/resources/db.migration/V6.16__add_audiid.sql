create sequence audit_seq;
alter TABLE competency_group_mapping add column audit_id integer not null default nextval('audit_seq');
alter sequence audit_seq owned by competency_group_mapping.audit_id;

alter TABLE history_competency_group_mapping add column audit_id int4;

update history_competency_group_mapping set audit_id=(select audit_id from competency_group_mapping 
where competency_group_mapping.competency_id=history_competency_group_mapping.competency_id 
and competency_group_mapping.competency_group_id=history_competency_group_mapping.competency_group_id);

alter TABLE history_competency_group_mapping add column auditId_MOD boolean;
update history_competency_group_mapping set auditId_MOD=false where 1=1;
