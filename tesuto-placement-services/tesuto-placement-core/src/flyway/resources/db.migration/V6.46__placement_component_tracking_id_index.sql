-- query changes from using subject name to discpline id 
create index pc_tracking_id_idx on placement_component  (tracking_id);
