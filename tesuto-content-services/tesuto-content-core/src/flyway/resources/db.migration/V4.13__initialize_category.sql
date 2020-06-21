-- I realize that a category may be fairly generic, however the use of this table will grow with time.
-- Category attributes are strings at the itemRef level.
-- current use is to limit the addition of itemRefs marked with the specific category for each namespace.
CREATE TABLE item_use_category (
  category_id SERIAL PRIMARY KEY,
  category_name character varying(100) NOT NULL,
  namespace character varying(100) NOT NULL UNIQUE,
  use_for_branch_rule boolean NOT NULL, -- branch rule flag also controls preconditions as the same code applies to both
  use_for_placement_model boolean NOT NULL -- this is for future work involving placement model
);

-- current use is to limit the addition of itemRefs marked with the FIELDTEST to be removed from branchrule and precondition
-- processing but we will continue to score
insert into item_use_category (category_name, namespace, use_for_branch_rule, use_for_placement_model)
values ('FIELDTEST', 'LSI', false, false);

insert into item_use_category (category_name, namespace, use_for_branch_rule, use_for_placement_model)
values ('FIELDTEST', 'DEVELOPER', false, false);