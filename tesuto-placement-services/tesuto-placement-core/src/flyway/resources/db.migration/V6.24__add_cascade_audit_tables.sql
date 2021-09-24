    alter TABLE REVCHANGES 
        drop constraint FK_t69kea3hasj6uc6ddn5ck5r9y ,
        add constraint FK_t69kea3hasj6uc6ddn5ck5r9y 
        foreign key (REV) 
        references history_placement_revision
        on delete cascade;

    alter TABLE history_cb21 
        drop constraint FK_6ppc5rvx9tmrj27x0j0ytdp1 ,
        add constraint FK_6ppc5rvx9tmrj27x0j0ytdp1 
        foreign key (REV) 
        references history_placement_revision
       on delete cascade;

    alter TABLE history_college_discipline 
        drop constraint FK_q0rs46eg11nqrgf6nlxmtuavm,     
        add constraint FK_q0rs46eg11nqrgf6nlxmtuavm 
        foreign key (REV) 
        references history_placement_revision
        on delete cascade;

    alter TABLE history_college_discipline_sequence 
        drop constraint FK_lgw8ojrw1qj9tb1kj4p112tkl,
        add constraint FK_lgw8ojrw1qj9tb1kj4p112tkl 
        foreign key (REV) 
        references history_placement_revision
        on delete cascade;

    alter TABLE history_college_discipline_sequence_course 
        drop constraint FK_5rm1wkexlhswc23li1hymsmch, 
        add constraint FK_5rm1wkexlhswc23li1hymsmch 
        foreign key (REV) 
        references history_placement_revision
        on delete cascade;

    alter TABLE history_competency_group 
        drop constraint FK_929xtiquxfgpvtbxxw4y1ljml, 
        add constraint FK_929xtiquxfgpvtbxxw4y1ljml 
        foreign key (REV) 
        references history_placement_revision
        on delete cascade;

    alter TABLE history_competency_group_mapping 
        drop constraint FK_gvebhspske5vxs7fiomb3xf83, 
        add constraint FK_gvebhspske5vxs7fiomb3xf83 
        foreign key (REV) 
        references history_placement_revision
        on delete cascade;

    alter TABLE history_course 
        drop constraint FK_edma42u24501yknjvy5vfm043, 
        add constraint FK_edma42u24501yknjvy5vfm043 
        foreign key (REV) 
        references history_placement_revision
        on delete cascade;