-- query changes from using subject name to discpline id 
drop index p_cccid_college_subject_idx;
create index p_cccid_college_subject_idx on placement  (college_id, discipline_id, cccid);

create index pc_cccid_college_idx on placement_component  (college_id, cccid);
create index pc_cccid_college_subject_idx on placement_component  (college_id, subject_area_id, cccid);
