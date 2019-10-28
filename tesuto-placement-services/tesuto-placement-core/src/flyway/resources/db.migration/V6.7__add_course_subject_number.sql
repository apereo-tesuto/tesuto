alter TABLE course add column subject character varying(64);
alter TABLE course add column number character varying(34);

update course set subject=name where subject is null;
update course set number=cid where number is null;

alter TABLE course alter column subject set NOT NULL;
alter TABLE course alter column number set NOT NULL;