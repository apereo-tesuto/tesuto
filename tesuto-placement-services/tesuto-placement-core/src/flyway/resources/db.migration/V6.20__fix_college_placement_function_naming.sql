-- This script does not add functionality beyond V6.17 and V6.18
-- It is the result of a typo that caused a build failure and this
-- was part of the cascade of changes to fix it properly.
create or replace FUNCTION public.update_college_placement_model_where_competency_group_mapping()
            RETURNS TRIGGER as $$
            BEGIN
            IF (TG_OP = 'UPDATE' OR TG_OP = 'INSERT') THEN
            UPDATE public.college_placement_model
            SET is_dirty = true where college_id in (select college_id from college_discipline as cd where cd.college_discipline_id  in (select college_discipline_id from college_discipline_sequence_course as cdsc where cdsc.course_id in (select cg.course_id from competency_group as cg where cg.competency_group_id = NEW.competency_group_id)))
            and competency_map_discipline in (select competency_map_discipline from college_discipline as cd where cd.college_discipline_id  in (select college_discipline_id from college_discipline_sequence_course as cdsc where cdsc.course_id  in (select cg.course_id from competency_group as cg where cg.competency_group_id = NEW.competency_group_id)));
            END IF;
            RETURN NULL;
            END;
$$ LANGUAGE plpgsql;

--  Discipline Sequence Update trigger
drop trigger change_college_discipline_sequence on college_discipline_sequence;
drop function update_college_placment_model_where_college_discipline_sequence_changed();
--  Discipline Sequence Update trigger
create or replace FUNCTION public.update_college_placement_model_where_college_discipline_sequence_changed()
            RETURNS TRIGGER as $$
            BEGIN
            IF (TG_OP = 'UPDATE' OR TG_OP = 'INSERT') THEN
            UPDATE public.college_placement_model
            SET is_dirty = true where college_id in (select college_id from college_discipline as cd where NEW.college_discipline_id= cd.college_discipline_id)
            and competency_map_discipline in (select competency_map_discipline from college_discipline as cd where NEW.college_discipline_id= cd.college_discipline_id);
            END IF;
            RETURN NULL;
            END;
$$ LANGUAGE plpgsql;

create trigger change_college_discipline_sequence
                AFTER UPDATE OR INSERT ON public.college_discipline_sequence
                FOR EACH ROW
            EXECUTE PROCEDURE public.update_college_placement_model_where_college_discipline_sequence_changed();

drop trigger change_college_discipline_sequence on public.college_discipline_sequence;
drop trigger change_college_discipline on public.college_discipline;
drop function update_college_placment_model_where_college_discipline_changed();
--  CompetencyGroupMap Update trigger
-- Fix a typo in a function name.
create or replace FUNCTION public.update_college_placement_model_where_college_discipline_changed()
            RETURNS TRIGGER as $$
            BEGIN
            IF (TG_OP = 'UPDATE' OR TG_OP = 'INSERT') THEN
            UPDATE public.college_placement_model
            SET is_dirty = true where NEW.college_id = college_id  and NEW.competency_map_discipline=competency_map_discipline;
            END IF;
            RETURN NULL;
            END;
$$ LANGUAGE plpgsql;
create trigger change_college_discipline_sequence
                after update or insert on public.college_discipline_sequence
                for each row
            execute procedure public.update_college_placement_model_where_college_discipline_sequence_changed();
create trigger change_college_discipline
                after update or insert on public.college_discipline
                for each row
            execute procedure public.update_college_placement_model_where_college_discipline_changed();

DROP TRIGGER IF EXISTS change_course on public.course;
--  CompetencyGroup Update trigger
DROP TRIGGER IF EXISTS change_competency_group on public.competency_group;
drop function update_college_placment_model_where_course_changed();
--  CompetencyGroup Update trigger
--  Course Update trigger
 create or replace FUNCTION public.update_college_placement_model_where_course_changed()
            RETURNS TRIGGER as $$
            BEGIN
            IF (TG_OP = 'UPDATE' OR TG_OP = 'INSERT') THEN
            UPDATE public.college_placement_model
            SET is_dirty = true where college_id in (select college_id from college_discipline as cd where cd.college_discipline_id  in (select college_discipline_id from college_discipline_sequence_course as cdsc where NEW.course_id = cdsc.course_id))
            and competency_map_discipline in (select competency_map_discipline from college_discipline as cd where cd.college_discipline_id  in (select college_discipline_id from college_discipline_sequence_course as cdsc where NEW.course_id = cdsc.course_id));
            END IF;
            RETURN NULL;
            END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER change_competency_group
                AFTER UPDATE OR INSERT ON public.competency_group
                FOR EACH ROW
            EXECUTE PROCEDURE public.update_college_placement_model_where_course_changed();
CREATE TRIGGER change_course
                AFTER UPDATE OR INSERT ON public.course
                FOR EACH ROW
            EXECUTE PROCEDURE public.update_college_placement_model_where_course_changed();

drop trigger change_course on public.course;
create trigger change_course
                after update or insert on public.course
                for each row
            execute procedure public.update_college_placement_model_where_course_changed();

drop trigger change_competency_group on public.competency_group;
create trigger change_competency_group
                after update or insert on public.competency_group
                for each row
            execute procedure public.update_college_placement_model_where_course_changed();

drop trigger change_competency_group_mapping on competency_group_mapping;
drop function update_college_placment_model_where_competency_group_mapping();
--  CompetencyGroupMap Update trigger
create or replace function public.update_college_placement_model_where_competency_group_mapping()
            RETURNS TRIGGER as $$
            BEGIN
            IF (TG_OP = 'UPDATE' OR TG_OP = 'INSERT') THEN
            UPDATE public.college_placement_model
            SET is_dirty = true where college_id in (select college_id from college_discipline as cd where cd.college_discipline_id  in (select college_discipline_id from college_discipline_sequence_course as cdsc where cdsc.course_id in (select cg.course_id from competency_group as cg where cg.competency_group_id = NEW.competency_group_id)))
            and competency_map_discipline in (select competency_map_discipline from college_discipline as cd where cd.college_discipline_id  in (select college_discipline_id from college_discipline_sequence_course as cdsc where cdsc.course_id  in (select cg.course_id from competency_group as cg where cg.competency_group_id = NEW.competency_group_id)));
            END IF;
            RETURN NULL;
            END;
$$ LANGUAGE plpgsql;
create trigger change_competency_group_mapping
                AFTER UPDATE OR INSERT ON public.competency_group_mapping
                FOR EACH ROW
            EXECUTE PROCEDURE public.update_college_placement_model_where_competency_group_mapping();

drop trigger change_competency_group_mapping on public.competency_group_mapping;
create trigger change_competency_group_mapping
                after update or insert on public.competency_group_mapping
                for each row
            execute procedure public.update_college_placement_model_where_competency_group_mapping();