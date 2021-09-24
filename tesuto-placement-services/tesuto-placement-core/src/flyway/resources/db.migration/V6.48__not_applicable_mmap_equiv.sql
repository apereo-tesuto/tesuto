insert into mmap_equivalent (competency_map_discipline, mmap_equivalent_code, mmap_equivalent, mmap_equivalent_order) 
   select  'ENGLISH', 'eng_na', 'Not Applicable', 4 where not exists (select * from mmap_equivalent where mmap_equivalent_code = 'eng_na');
    
insert into mmap_equivalent (competency_map_discipline, mmap_equivalent_code, mmap_equivalent, mmap_equivalent_order) 
 select 'ESL', 'esl_na', 'Not Applicable', 4 where not exists (select * from mmap_equivalent where mmap_equivalent_code = 'esl_na');
 
insert into mmap_equivalent (competency_map_discipline, mmap_equivalent_code, mmap_equivalent, mmap_equivalent_order) 
 select 'MATH', 'math_na', 'Not Applicable', 8 where not exists (select * from mmap_equivalent where mmap_equivalent_code = 'math_na');