--support accurate total duration by accounting for duplicate duration values in item bundles
ALTER TABLE report_assessment_structure ADD COLUMN items_in_bundle integer DEFAULT 1;