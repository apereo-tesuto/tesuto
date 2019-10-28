CREATE TABLE college_placment_model (
    id character varying(100) NOT NULL primary key,
    is_dirty boolean,
    college_id character varying(100) NOT NULL,
    competency_map_discipline character varying(100) NOT NULL,
    competency_map_order_id character varying(100) NOT NULL,
    placement_model character varying
);

CREATE TABLE placement_decision (
    id character varying(100) NOT NULL primary key,
    cccid character varying(100) NOT NULL,
    assessment_session_id character varying(100) NOT NULL,
    competency_map_discipline character varying(100) NOT NULL,
    competency_map_order_id character varying(100) NOT NULL,
    college_placement_model_id character varying(100) NOT NULL,
    college_id  character varying(100) NOT NULL,
    student_ability double precision,
    created_on_date timestamp without time zone,
    placement_decisions_by_discipline character varying
);

CREATE INDEX cmp_college_idx ON college_placment_model (college_id);
CREATE INDEX pd_college_idx ON placement_decision (college_id);
CREATE INDEX pd_cccid_idx ON placement_decision (cccid);
CREATE INDEX pd_competency_map_discipline_idx ON placement_decision (competency_map_discipline);

-- College Discipline Update trigger
create or replace FUNCTION public.update_college_placment_model_where_college_discipline_changed()
            RETURNS TRIGGER as $$
            BEGIN 
            IF (TG_OP = 'UPDATE' OR TG_OP = 'INSERT') THEN
            UPDATE public.college_placment_model
            SET is_dirty = true where NEW.college_id = college_id  and NEW.competency_map_discipline=competency_map_discipline;
            END IF;
            RETURN NULL;
            END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS change_college_discipline on public.college_discipline;

CREATE TRIGGER change_college_discipline
                AFTER UPDATE OR INSERT ON public.college_discipline
                FOR EACH ROW
            EXECUTE PROCEDURE public.update_college_placment_model_where_college_discipline_changed();

--  Discipline Sequence Update trigger
create or replace FUNCTION public.update_college_placment_model_where_college_discipline_sequence_changed()
            RETURNS TRIGGER as $$
            BEGIN 
            IF (TG_OP = 'UPDATE' OR TG_OP = 'INSERT') THEN
            UPDATE public.college_placment_model
            SET is_dirty = true where college_id in (select college_id from college_discipline as cd where NEW.college_discipline_id= cd.college_discipline_id) 
            and competency_map_discipline in (select competency_map_discipline from college_discipline as cd where NEW.college_discipline_id= cd.college_discipline_id);
            END IF;
            RETURN NULL;
            END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS change_college_discipline_sequence on public.college_discipline_sequence;

CREATE TRIGGER change_college_discipline_sequence
                AFTER UPDATE OR INSERT ON public.college_discipline_sequence
                FOR EACH ROW
            EXECUTE PROCEDURE public.update_college_placment_model_where_college_discipline_sequence_changed();

 --  Course Update trigger
 create or replace FUNCTION public.update_college_placment_model_where_course_changed()
            RETURNS TRIGGER as $$
            BEGIN 
            IF (TG_OP = 'UPDATE' OR TG_OP = 'INSERT') THEN
            UPDATE public.college_placment_model
            SET is_dirty = true where college_id in (select college_id from college_discipline as cd where cd.college_discipline_id  in (select college_discipline_id from college_discipline_sequence_course as cdsc where NEW.course_id = cdsc.course_id))
            and competency_map_discipline in (select competency_map_discipline from college_discipline as cd where cd.college_discipline_id  in (select college_discipline_id from college_discipline_sequence_course as cdsc where NEW.course_id = cdsc.course_id));
            END IF;
            RETURN NULL;
            END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS change_course on public.course;

CREATE TRIGGER change_course
                AFTER UPDATE OR INSERT ON public.course
                FOR EACH ROW
            EXECUTE PROCEDURE public.update_college_placment_model_where_course_changed();

--  CompetencyGroup Update trigger

DROP TRIGGER IF EXISTS change_competency_group on public.competency_group;

CREATE TRIGGER change_competency_group
                AFTER UPDATE OR INSERT ON public.competency_group
                FOR EACH ROW
            EXECUTE PROCEDURE public.update_college_placment_model_where_course_changed();

--  CompetencyGroupMap Update trigger
 create or replace FUNCTION public.update_college_placment_model_where_competency_group_mapping()
            RETURNS TRIGGER as $$
            BEGIN 
            IF (TG_OP = 'UPDATE' OR TG_OP = 'INSERT') THEN
            UPDATE public.college_placment_model
            SET is_dirty = true where college_id in (select college_id from college_discipline as cd where cd.college_discipline_id  in (select college_discipline_id from college_discipline_sequence_course as cdsc where cdsc.course_id in (select cg.course_id from competency_group as cg where cg.competency_group_id = NEW.competency_group_id)))
            and competency_map_discipline in (select competency_map_discipline from college_discipline as cd where cd.college_discipline_id  in (select college_discipline_id from college_discipline_sequence_course as cdsc where cdsc.course_id  in (select cg.course_id from competency_group as cg where cg.competency_group_id = NEW.competency_group_id)));
            END IF;
            RETURN NULL;
            END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS change_competency_group_mapping on public.competency_group_mapping;

CREATE TRIGGER change_competency_group_mapping
                AFTER UPDATE OR INSERT ON public.competency_group_mapping
                FOR EACH ROW
            EXECUTE PROCEDURE public.update_college_placment_model_where_competency_group_mapping();
