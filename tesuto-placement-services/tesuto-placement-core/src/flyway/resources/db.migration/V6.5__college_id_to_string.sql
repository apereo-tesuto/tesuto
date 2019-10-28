alter TABLE college_discipline add column string_college_id varchar;
update college_discipline set string_college_id=college_id::varchar;
alter TABLE college_discipline drop column college_id;
alter TABLE college_discipline add column college_id varchar;
update college_discipline set college_id=string_college_id;
alter TABLE college_discipline alter column  college_id set not null;
alter TABLE college_discipline drop column string_college_id;
