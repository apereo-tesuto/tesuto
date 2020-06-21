-- Add the home view permission to the faculty and counselor security groups
insert into security_group_security_permission
(security_group_id, security_permission_id)
select security_group_id, 'VIEW_ADMIN_DASHBOARD'
  from security_group sg
  where group_name = 'FACULTY';

insert into security_group_security_permission
(security_group_id, security_permission_id)
select security_group_id, 'VIEW_ADMIN_DASHBOARD'
  from security_group sg
  where group_name = 'COUNSELOR';

-- Add the college discipline permission to the faculty and counselor security groups
insert into security_group_security_permission
(security_group_id, security_permission_id)
select security_group_id, 'GET_DISCIPLINE_BY_COLLEGE_ID'
  from security_group sg
  where group_name = 'FACULTY';

insert into security_group_security_permission
(security_group_id, security_permission_id)
select security_group_id, 'GET_DISCIPLINE_BY_COLLEGE_ID'
  from security_group sg
  where group_name = 'COUNSELOR';

-- Add the competency map permission to the faculty user
insert into security_group_security_permission
(security_group_id, security_permission_id)
select security_group_id, 'VIEW_COMPETENCY_MAP_DISCIPLINES'
  from security_group sg
  where group_name = 'FACULTY';

-- Add more discipline related permissions to the faculty user
insert into security_group_security_permission
(security_group_id, security_permission_id)
select security_group_id, 'CREATE_DISCIPLINE'
  from security_group sg
  where group_name = 'FACULTY';

insert into security_group_security_permission
(security_group_id, security_permission_id)
select security_group_id, 'UPDATE_DISCIPLINE'
  from security_group sg
  where group_name = 'FACULTY';

insert into security_group_security_permission
(security_group_id, security_permission_id)
select security_group_id, 'DELETE_DISCIPLINE'
  from security_group sg
  where group_name = 'FACULTY';

insert into security_group_security_permission
(security_group_id, security_permission_id)
select security_group_id, 'GET_SEQUENCE'
  from security_group sg
  where group_name = 'FACULTY';

insert into security_group_security_permission
(security_group_id, security_permission_id)
select security_group_id, 'UPDATE_SEQUENCE'
  from security_group sg
  where group_name = 'FACULTY';

insert into security_group_security_permission
(security_group_id, security_permission_id)
select security_group_id, 'GET_COMPETENCY_GROUPS_BY_CLASS_ID'
  from security_group sg
  where group_name = 'FACULTY';

insert into security_group_security_permission
(security_group_id, security_permission_id)
select security_group_id, 'CREATE_COMPETENCY_GROUP'
  from security_group sg
  where group_name = 'FACULTY';

insert into security_group_security_permission
(security_group_id, security_permission_id)
select security_group_id, 'UPDATE_COMPETENCY_GROUP'
  from security_group sg
  where group_name = 'FACULTY';

insert into security_group_security_permission
(security_group_id, security_permission_id)
select security_group_id, 'DELETE_COMPETENCY_GROUP'
  from security_group sg
  where group_name = 'FACULTY';

insert into security_group_security_permission
(security_group_id, security_permission_id)
select security_group_id, 'CREATE_COURSE'
  from security_group sg
  where group_name = 'FACULTY';

insert into security_group_security_permission
(security_group_id, security_permission_id)
select security_group_id, 'UPDATE_COURSE'
  from security_group sg
  where group_name = 'FACULTY';

insert into security_group_security_permission
(security_group_id, security_permission_id)
select security_group_id, 'DELETE_COURSE'
  from security_group sg
  where group_name = 'FACULTY';

insert into security_group_security_permission
(security_group_id, security_permission_id)
select security_group_id, 'VIEW_COMPETENCY_MAP'
  from security_group sg
  where group_name = 'FACULTY';

