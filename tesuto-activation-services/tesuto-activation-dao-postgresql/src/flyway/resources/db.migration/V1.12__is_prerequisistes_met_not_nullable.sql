update activation set is_prerequisites_met=true where  is_prerequisites_met is null;

alter table activation alter column  is_prerequisites_met set not null;

