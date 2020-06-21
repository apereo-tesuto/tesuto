drop TABLE college_discipline_sequence_course_audit;
drop TABLE college_discipline_sequence_audit;
drop TABLE competency_group_mapping_audit;
drop TABLE competency_group_audit;
drop TABLE course_audit;
drop TABLE college_discipline_audit;

alter TABLE college_discipline drop column updated_by;
alter TABLE college_discipline drop column updated_on;

alter TABLE college_discipline_sequence drop column updated_by;
alter TABLE college_discipline_sequence drop column updated_on;

alter TABLE college_discipline_sequence_course drop column updated_by;
alter TABLE college_discipline_sequence_course drop column updated_on;

alter TABLE competency_group drop column updated_by;
alter TABLE competency_group drop column updated_on;

alter TABLE competency_group_mapping drop column updated_by;
alter TABLE competency_group_mapping drop column updated_on;

alter TABLE course drop column updated_by;
alter TABLE course drop column updated_on;

delete from college_discipline where 1=1;

delete from college_discipline_sequence where 1=1;

delete from college_discipline_sequence_course where 1=1;

delete from competency_group where 1=1;

delete from competency_group_mapping where 1=1;

delete from course where 1=1;


    create TABLE REVCHANGES (
        REV int4 not null,
        ENTITYNAME character varying
    );

    create TABLE history_cb21 (
        cb21_code char(1) not null,
        REV int4 not null,
        REVEND int4,
        REVTYPE int2,
        REVEND_TSTMP timestamp,
        created_on timestamp,
        level int4,
        updated_on timestamp,
        
        cb21Code_MOD boolean,
        createdOn_MOD boolean,
        level_MOD boolean,
        updatedOn_MOD boolean,
        
        primary key (cb21_code, REV)
    );

    create TABLE history_college_discipline (
        college_discipline_id int4 not null,
        REV int4 not null,
        REVEND int4,
        REVTYPE int2,
        REVEND_TSTMP timestamp,
        college_id character varying,
        competency_map_discipline character varying(256),
        competency_map_version int4,
        use_prereq_placement_method boolean,
        created_by character varying(256),
        created_on timestamp,
        description text,
        sis_code character varying(100),
        title character varying,
        
        collegeId_MOD boolean,
        competencyMapDiscipline_MOD boolean,
        disciplineSequences_MOD boolean,
        competencyMapVersion_MOD boolean,
        usePrereqPlacementMethod_MOD boolean,
        createdBy_MOD boolean,
        createdOn_MOD boolean,
        description_MOD boolean,
        sisCode_MOD boolean,
        title_MOD boolean,
        
        primary key (college_discipline_id, REV)
    );

    create TABLE history_college_discipline_sequence (
        cb21_code char(1) not null,
        college_discipline_id int4 not null,
        REV int4 not null,
        REVEND int4,
        REVTYPE int2,
        REVEND_TSTMP timestamp,
        created_by character varying(256),
        created_on timestamp,
        explanation character varying,
        show_student boolean,
        mapping_level character varying (50),
        
        cb21_MOD boolean,
        discipline_MOD boolean,
        createdBy_MOD boolean,
        createdOn_MOD boolean,
        explanation_MOD boolean,
        showStudent_MOD boolean,
        mappingLevel_MOD boolean,
        disciplineSequenceCourses_MOD boolean,
        primary key (cb21_code, college_discipline_id, REV)
    );

    create TABLE history_college_discipline_sequence_course (
        cb21_code char(1) not null,
        course_id int4 not null,
        college_discipline_id int4 not null,
        REV int4 not null,
        REVEND int4,
        REVTYPE int2,
        REVEND_TSTMP timestamp,
        created_by character varying(256),
        created_on timestamp,
        
        course_MOD boolean,
        disciplineSequence_MOD boolean,
        createdBy_MOD boolean,
        createdOn_MOD boolean,
        
        primary key (cb21_code, course_id, college_discipline_id, REV)
    );

    create TABLE history_competency_group (
        competency_group_id int4 not null,
        REV int4 not null,
        REVEND int4,
        REVTYPE int2,
        REVEND_TSTMP timestamp,
        created_by character varying(256),
        created_on timestamp,
        probability_success int4,
        title character varying,
        course_id int4,
        
        competencyGroupMappings_MOD boolean,
        createdBy_MOD boolean,
        createdOn_MOD boolean,
        probabilitySuccess_MOD boolean,
        title_MOD boolean,
        course_MOD boolean,
        
        primary key (competency_group_id, REV)
    );

    create TABLE history_competency_group_mapping (
        competency_group_id int4 not null,
        competency_id character varying(100) not null,
        REV int4 not null,
        REVEND int4,
        REVTYPE int2,
        REVEND_TSTMP timestamp,
        created_by character varying(256),
        created_on timestamp,
        
        competencyGroup_MOD boolean,
        createdBy_MOD boolean,
        createdOn_MOD boolean,
        
        primary key (competency_group_id, competency_id, REV)
    );

    create TABLE history_course (
        course_id int4 not null,
        REV int4 not null,
        REVEND int4,
        REVTYPE int2,
        REVEND_TSTMP timestamp,
        cid character varying(10),
        competency_group_logic character varying,
        created_by character varying(256),
        created_on timestamp,
        description text,
        name character varying(120),
        number character varying(34),
        subject character varying(64),
        
        cid_MOD boolean,
        competencyGroupLogic_MOD boolean,
        createdBy_MOD boolean,
        createdOn_MOD boolean,
        description_MOD boolean,
        name_MOD boolean,
        number_MOD boolean,
        subject_MOD boolean,
        
        primary key (course_id, REV)
    );


    create TABLE history_placement_revision (
        id int4 not null,
        timestamp int8 not null,
        user_account_id character varying,
        primary key (id)
    );

    alter TABLE REVCHANGES 
        add constraint FK_t69kea3hasj6uc6ddn5ck5r9y 
        foreign key (REV) 
        references history_placement_revision;

    alter TABLE history_cb21 
        add constraint FK_6ppc5rvx9tmrj27x0j0ytdp1 
        foreign key (REV) 
        references history_placement_revision;

    alter TABLE history_college_discipline 
        add constraint FK_q0rs46eg11nqrgf6nlxmtuavm 
        foreign key (REV) 
        references history_placement_revision;

    alter TABLE history_college_discipline_sequence 
        add constraint FK_lgw8ojrw1qj9tb1kj4p112tkl 
        foreign key (REV) 
        references history_placement_revision;

    alter TABLE history_college_discipline_sequence_course 
        add constraint FK_5rm1wkexlhswc23li1hymsmch 
        foreign key (REV) 
        references history_placement_revision;

    alter TABLE history_competency_group 
        add constraint FK_929xtiquxfgpvtbxxw4y1ljml 
        foreign key (REV) 
        references history_placement_revision;

    alter TABLE history_competency_group_mapping 
        add constraint FK_gvebhspske5vxs7fiomb3xf83 
        foreign key (REV) 
        references history_placement_revision;

    alter TABLE history_course 
        add constraint FK_edma42u24501yknjvy5vfm043 
        foreign key (REV) 
        references history_placement_revision;

INSERT INTO history_placement_revision (ID, timestamp, user_account_id) values (1, 1471023263447 , 'system');
insert into revchanges (rev, entityname) values (1 ,'org.cccnext.tesuto.placement.model.CB21');

INSERT INTO history_cb21(rev, revtype, revend, revend_tstmp, cb21code_mod, createdon_mod, level_mod, updatedon_mod,
cb21_code, level, created_on, updated_on) 
select 1 as rev, 0 as revtype, null as revend, null as revend_tstmp, true as cb21code_mod, true as createdon_mod, true as level_mod, true as updatedon_mod,
cb21_code, level, created_on, updated_on from cb21;

