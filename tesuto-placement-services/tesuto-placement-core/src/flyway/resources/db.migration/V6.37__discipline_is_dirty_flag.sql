-- Add the is_dirty boolean for currently edited subject areas (disciplines).
alter table college_discipline add column is_dirty boolean default true;

-- Add the is_dirty boolean for currently edited subject areas (disciplines).
alter table college_discipline add column created_on_date timestamp without time zone;
alter table college_discipline add column last_updated_date timestamp without time zone;

-- Add the is_dirty boolean for currently edited subject areas (disciplines).
alter table history_college_discipline add column is_dirty boolean;
alter table history_college_discipline add column isDirty_MOD boolean;