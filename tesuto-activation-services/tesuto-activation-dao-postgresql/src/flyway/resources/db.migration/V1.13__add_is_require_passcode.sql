alter table activation add column is_require_passcode boolean;

update activation set is_require_passcode=true where  is_require_passcode is null;

alter table activation alter column is_require_passcode set not null;
