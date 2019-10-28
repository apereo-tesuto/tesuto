-- Table will hold item data and will be populated during assessment import.
CREATE TABLE item_points (
  item_id character varying(100) NOT NULL PRIMARY KEY,  -- maps to mongo _id
  max_points double precision NOT NULL,
  min_points double PRECISION NOT NULL,
  item_identifier character varying(100) NOT NULL, -- maps to the item identifier
  namespace character varying(100) NOT NULL,
  version INT NOT NULL
);