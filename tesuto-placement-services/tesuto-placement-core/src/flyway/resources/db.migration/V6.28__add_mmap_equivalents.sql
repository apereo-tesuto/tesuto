CREATE TABLE mmap_equivalent (
    mmap_equivalent_code character varying(20) NOT NULL,
    competency_map_discipline character varying(256) NOT NULL,
    mmap_equivalent character varying(256) NOT NULL,
    mmap_equivalent_order integer NOT NULL,

    PRIMARY KEY (mmap_equivalent_code)
);

INSERT INTO mmap_equivalent (competency_map_discipline, mmap_equivalent_code, mmap_equivalent, mmap_equivalent_order) VALUES
    ('ENGLISH', 'eng_eng', 'English', 1),
    ('ENGLISH', 'eng_esl', 'ESL', 2),
    ('ENGLISH', 'eng_read', 'Reading', 3),
    ('ESL', 'esl_eng', 'English', 1),
    ('ESL', 'esl_esl', 'ESL', 2),
    ('ESL', 'esl_read', 'Reading', 3),
    ('MATH', 'math_alg', 'Int. Algebra', 1),
    ('MATH', 'math_stat', 'Statistics', 2),
    ('MATH', 'math_ge', 'General Education Math', 3),
    ('MATH', 'math_col_alg', 'College Algebra', 4),
    ('MATH', 'math_trig', 'Trigonometry', 5),
    ('MATH', 'math_pre_calc', 'Pre-Calculus', 6),
    ('MATH', 'math_calc_i', 'Calculus', 7)
;
