alter TABLE cb21 add column int_level int;
update cb21 set int_level=level::int;
alter TABLE cb21 drop column level;
alter TABLE cb21 add column level int;
update cb21 set level=int_level;
alter TABLE cb21 alter column  level set not null;
alter TABLE cb21 drop column int_level;
