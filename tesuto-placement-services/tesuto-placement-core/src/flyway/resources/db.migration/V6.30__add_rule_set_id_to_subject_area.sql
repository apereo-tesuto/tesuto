-- adding course group DisciplineSequence
ALTER TABLE competency_attributes
ADD COLUMN mm_decision_logic character varying(100);

ALTER TABLE history_competency_attributes
ADD COLUMN mm_decision_logic character varying(100);

ALTER TABLE history_competency_attributes
ADD COLUMN mmDecisionLogic_MOD boolean;

ALTER TABLE competency_attributes
ADD COLUMN mm_placement_logic character varying(100);

ALTER TABLE history_competency_attributes
ADD COLUMN mm_placement_logic character varying(100);

ALTER TABLE history_competency_attributes
ADD COLUMN mmPlacementLogic_MOD boolean;

ALTER TABLE competency_attributes
ADD COLUMN mm_asigne_placement_login character varying(100);

ALTER TABLE history_competency_attributes
ADD COLUMN mm_asigne_placement_login character varying(100);

ALTER TABLE history_competency_attributes
ADD COLUMN mmAssignedPlacementLogic_MOD boolean;






