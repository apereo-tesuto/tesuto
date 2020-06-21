--  Add some index optimizations to the user, group and permission management.

create index group_name_idx on security_group (group_name);