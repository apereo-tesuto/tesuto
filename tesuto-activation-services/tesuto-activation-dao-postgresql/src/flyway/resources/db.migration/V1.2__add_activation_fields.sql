ALTER TABLE activation ADD COLUMN location varchar(4000);
ALTER TABLE activation ADD COLUMN accommodations varchar(4000);
ALTER TABLE activation ADD COLUMN min_window_size smallint default 0;
ALTER TABLE activation ADD COLUMN max_window_size smallint default 0;
ALTER TABLE activation ADD COLUMN max_attempts smallint default 1;
ALTER TABLE activation ADD COLUMN require_approval_to_resume bool default 't';
ALTER TABLE activation ADD COLUMN manual_essay_scoring bool default 'f';

ALTER TABLE activation ALTER min_window_size SET NOT NULL;
ALTER TABLE activation ALTER max_window_size SET NOT NULL;
ALTER TABLE activation ALTER max_attempts SET NOT NULL;
ALTER TABLE activation ALTER require_approval_to_resume SET NOT NULL;
ALTER TABLE activation ALTER manual_essay_scoring  SET NOT NULL;

