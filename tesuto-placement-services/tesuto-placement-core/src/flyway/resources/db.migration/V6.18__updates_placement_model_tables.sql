truncate TABLE college_placment_model, placement_decision;

alter TABLE college_placment_model add college_discipline_id int NOT NULL;
alter TABLE college_placment_model drop placement_model;
alter TABLE college_placment_model add discipline_snapshot character varying;
;

alter TABLE placement_decision add cb21_code char(1) NOT NULL;
alter TABLE placement_decision add college_discipline_id int NOT NULL;
alter TABLE placement_decision add placement_decision_type  character varying(40) NOT NULL;
alter TABLE placement_decision drop placement_decisions_by_discipline;
alter TABLE placement_decision add discipline_decision character varying;

CREATE INDEX cmp_discipline_idx ON college_placment_model (college_discipline_id);
CREATE INDEX pd_discipline_idx ON placement_decision (college_discipline_id);
CREATE INDEX pd_placement_decision_type_idx ON placement_decision (placement_decision_type);
