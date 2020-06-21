-- Create the many to many join table for the placement and placement_component tables.
create table placement_placement_component (
  placement_id character varying(100) not null,
  placement_component_id character varying(100) not null,
  primary key (placement_id, placement_component_id),
  foreign key (placement_id) references placement(id),
  foreign key (placement_component_id) references placement_component(id)
);

-- Migrate placement component table data
insert into placement_placement_component (placement_id, placement_component_id)
  select placement_id, id
  from placement_component
  where placement_id is not null;

-- Remove the old placement_id column from the placement_component table
alter table placement_component drop column placement_id;