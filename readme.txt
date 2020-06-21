   List of commands:
home                 go to home directory
build_all            builds a set of all microservices (sets complete,placement,assessment,assessment_min default is placement), runs flyway scripts to generate database and npm for js  but does not test. example: ccc.sh build_all complete
build_quick          builds a set of all microservices (sets complete,placement,assessment,assessment_min default is placement) example: to build just minimal assessment  cccs.sh build_all assessment_min
build_test_all       builds all microservices, runs flyway scripts to generate database, npm for js  and tests. 
build_micro          builds a single micro but does not test. example: ccc.sh build_micro content
build_test_micro     builds source code for micros but does not test. example: ccc.sh build_test_micro content
build_ui             builds source code for ui only and compiles js using grun
build_database       builds the database for all microservices
start_all            starts all microservices required to fully support assessments and placements,  micros not to be run ex. uiactivation will not run ui or activation micros.,  names to be debugged 
start_placement      starts all microservices required to fully support placements,  micros not to be run ex. uiactivation will not run ui or activation micros.,  names to be debugged
start_assessment     starts all microservices required to fully support assessment,  micros not to be run ex. uiactivation will not run ui or activation micros.,  names to be debugged
start_min_assessment starts minimal microservices required to support assessment,  micros not to be run ex. uiactivation will not run ui or activation micros.,  names to be debugged
start_micro          starts a microservice who are started with spring boot,  is micro to kill. ex. ccc.sh start_micro ui
debug_micro          starts a microservice who are started with spring boot,  is micro to debug. ex. ccc.sh debug_micro ui
kill_all             kills all microservices who are started with spring boot
kill_micro           kills a microservice who are started with spring boot,  is micro to kill. ex. ccc.sh kill_micro ui
watch                watch log  suffix microservice name  number of lines
watch_all            see N lines of all microservices logs (20 is default)
status               searches log for Start Application
clear_logs           clears all logs
   
 Short name and debug port For All microservices
activation   port:8001
admin        port:8002
content      port:8004
delivery     port:8005
placement    port:8006
preview      port:8007
qti-importer port:8008
reports      port:8009
rules        port:8010
ui           port:8011
router       port:8003


 BASIC INSTRUCTIONS FOR GETTING UP AND RUNNING
TO BUILD AND RUN Placement and Assessments microservices including reports:
    INSTALL and start postgres and mongo
    CREATE postgres databases:
              database                 username          password
       1) tesuto_activation     tesuto_admin     tesuto_activation
       2) tesuto_admin          tesuto_admin     tesuto_admin
       3) tesuto_content        tesuto_content   tesuto_content
       4) tesuto_delivery       tesuto_delivery  tesuto_delivery
       5) tesuto_placement      tesuto_placement tesuto_placement
       6) tesuto_reports        tesuto_reports   tesuto_reports
    INSTALL and start redis, elasticmq-server, local dynamodb
    SET ENV variable $TESUTO_HOME to your tesuto repos folder

    ccc.sh build_all All
    FROM script folder, make sure ccc.sh has permission execute
    ccc.sh start_all
    ccc.sh watch ? 20 to view logs where ? is microservice short name example: ccc.sh watch ui 10 to tail ui logs
   
    go to localhost:8443/login to start application: login is superuser:password
   
   
TO BUILD AND RUN Placement services only 
    INSTALL and start postgres and mongo
    CREATE postgres databases:
       1) tesuto_admin          tesuto_admin     tesuto_admin
       2) tesuto_placement      tesuto_placement tesuto_placement
    INSTALL and start redis, elasticmq-server, local dynamodb
    SET ENV variable $TESUTO_HOME to your tesuto repos folder

    ccc.sh build_all Placement
    ccc.sh start_placement
    ccc.sh watch ? 20 to view logs where ? is microservice short name example: ccc.sh watch ui 10 to tail ui logs
   
    go to localhost:8443/login to start application: login is superuser:password
   
   
TO BUILD AND RUN Assessments microservices without reports,preview,import:
    INSTALL and start postgres and mongo
    CREATE postgres databases:
              database                 username          password
       1) tesuto_activation     tesuto_admin     tesuto_activation
       2) tesuto_admin          tesuto_admin     tesuto_admin
       3) tesuto_content        tesuto_content   tesuto_content
       4) tesuto_delivery       tesuto_delivery  tesuto_delivery
    INSTALL and start redis, elasticmq-server
    SET ENV variable $TESUTO_HOME to your tesuto repos folder

    ccc.sh build_all AssessmentMin
    ccc.sh start_assessment_min
    ccc.sh watch ? 20 to view logs where ? is microservice short name example: ccc.sh watch ui 10 to tail ui logs
    go to localhost:8443/login to start application: login is superuser:password
   
   
TO BUILD AND RUN Assessments microservices with reports:
    INSTALL and start postgres and mongo
    CREATE postgres databases:
              database                 username          password
       1) tesuto_activation     tesuto_admin     tesuto_activation
       2) tesuto_admin          tesuto_admin     tesuto_admin
       3) tesuto_content        tesuto_content   tesuto_content
       4) tesuto_delivery       tesuto_delivery  tesuto_delivery
       5) tesuto_reports        tesuto_reports   tesuto_reports
    INSTALL and start redis, elasticmq-server
    SET ENV variable $TESUTO_HOME to your tesuto repos folder
    ccc.sh build_all Assessment
    FROM script folder, make sure ccc.sh has permission execute
    ccc.sh start_assessment
    ccc.sh watch ? 20 to view logs where ? is microservice short name example: ccc.sh watch ui 10 to tail ui logs
   
    go to localhost:8443/login to start application: login is superuser:password
