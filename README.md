TESUTO maintained by Apereo
=================

An Open Source Test Assessment System

About
=====

 * Test delivery for student assessments
 * Scalable, high performance architecture
 * Localization
 * Implements mandatory accessibility requirements for all students
 * Accommodations for students with special needs based on a student's Personal Needs Profile (PNP)
 * Integrates with central authorization systems already in place at the learning institutions
 * Importing of test content
 * Scoring of tests
 * Adaptive testing
 * Activation of testing assessment windows
 * Proctor management

Requirements
============

 * JDK 8 or higher
 * Maven 3.2.x
 * Postgres (or MySQL with some minor changes, and stored procedure changes)
 * Mongo (one node required, 3 node replicating cluster is optional) **NOTE: Mongo must be run in authentication mode.
 * Redis (not optional, required to support microservices and session sharing)
 * S3 (optional, local file system can be used as well for a single node or multi-node shared mount location)
 * Queue SQS for production ElasticMQ for development
 * DynamoDB (a local version can be run as well)
 * Mitreid (a local docker version is available)

Configuration and Running (Development)
=======================================

## Steps Overview
In order for tesuto to be fully functioning the following steps need to be completed.

* Install Postgres. Create databases for services (see below for list based on installtion type).  Version 9.4.x or higher is recommended.
* Install Mongo.  Version 3.0.6 or higher is the minimum desired. We've tested 3.2.x and 3.4.x as well.
* Install Redis.  Version 3.2.3 or higher is recommended.
* Install ElasticMQ
* Install local DynamoDB
* Add env with paths to services and tesuto project root $TESUTO_HOME,$HOME_ELASTICMQ,$HOME_DYNAMODB
* Make sure tesuto script (projecthome/scripts/tesuto) has execute permission and is on PATH
* Start third party services:  ``tesuto start services`` some tests require services to be running
* Build    ``testuto build (complete, assess, min_asses, placement) ``
* Start     ``testuto start (complete, assess, min_asses, placement) ``
* To watch a log for a particular microservice ``tesuto watch (service_name) ``
* Status  to see current status of all microservices `` tesuto status ``
* Ready State to monitor if services are ready ``tesuto is_ready``

## Postgres database install and configuration ##
 * Install Postgres per your system's package manager
 * Set the database master password
 * Start the database using your system's run control or initialization scripts
 * ``psql -Upostgres``
 * ``create database tesuto ENCODING='UTF8';``
 * ``create user tesuto with password 'tesuto_admin';``
 * ``grant all privileges on database "tesuto_admin" to tesuto_admin;``
 * ``\c tesuto;``
 * ``\q``
 * ``psql -Upostgres``
 * ``psql -U tesuto tesuto;``
 * Repeat for placement microservice
 * ``create database tesuto_placement ENCODING='UTF8';``
 * ``create user tesuto_placement with password 'tesuto_placement';``
 * ``grant all privileges on database "tesuto_placement" to tesuto_placement;``
 * ``\c tesuto_placement;``
 * ``\q``

**``NOTE:``** For local build deployment with Docker this is done through a script postgres/init/01-filladb.sh. No need to install PostgreSQL/9 separately for Windows and Linux. It will be handled through ``Local Docker Builds`` deployment section.
  
## CREATE ADDITION SCHEMAS FOR ASSESSMENTS: 
 * ``tesuto_activation username:tesuto_activation | password:tesuto_activation``
 * ``tesuto_content    username:tesuto_content    | password:tesuto_content``
 * ``tesuto_delivery   username:tesuto_delivery   | password:tesuto_delivery``
 * ``tesuto_reports    username:tesuto_reports    | password:tesuto_reports``

**``NOTE:``** For local build deployment with Docker this is done through a script postgres/init/01-filladb.sh. No need to install PostgreSQL/9 separately for Windows and Linux. It will be handled through ``Local Docker Builds`` deployment section.

## Postgres database install for Mac
 * Install Postgres with the Graphic Installer found here: http://www.postgresql.org/download/macosx/
 * Complete the installation process, make note of where it was installed
 * For convenience you can add the following aliases in your ``~/.bash_profile`` file (adjust paths as needed)
 
```bash
alias postgres_start='sudo -u postgres /Library/PostgreSQL/9.4/bin/pg_ctl -D /Library/PostgreSQL/9.4/data start'
alias postgres_stop='sudo -u postgres /Library/PostgreSQL/9.4/bin/pg_ctl -D /Library/PostgreSQL/9.4/data stop'
```
### Native Postgres on a Mac, Access from a local Docker container
It has been noted that default installs do not allow access from a Docker container running on the host.
Reference this to address this issue:
https://blog.bigbinary.com/2016/01/23/configure-postgresql-to-allow-remote-connection.html 


## Install and Launch Redis
    Redis is installed using default settings follow the quick start for your OS here:
    https://redis.io/topics/quickstart
    Launch redis from command line: redis-server

**``NOTE:``** For Windows and Linux there is no special installation needed for Redis. It will be handled through ``Local Docker Builds`` deployment section.

## Local AWS Settings
In your local user home directory there is a directory called ``.aws`` Inside this directory
there are 2 files:

``config``
```
   [default]
  region = us-west-
```

``credentials``
```
  [default]
   aws_access_key_id = 
   aws_secret_access_key = 
   
  [ci]
   aws_access_key_id = 
   aws_secret_access_key = 
                                                                                                                                                                                                                                                
   [test]                                                                                                                                                                                                                                       
   aws_access_key_id = 
   aws_secret_access_key = 
```
## Install and Launch ElasticMQ
Note: When installing ElasticMQ create a custom.config file with the following json:
```
include classpath("application.conf")
queues {
    assessment-complete {
        defaultVisibilityTimeout = 20 seconds
        delay = 5 seconds
        receiveMessageWait = 0 seconds
    }

    multiple-measure-requested {
        defaultVisibilityTimeout = 20 seconds
        delay = 5 seconds
        receiveMessageWait = 0 seconds
    }

    placement-complete-notification {
        defaultVisibilityTimeout = 20 seconds
        delay = 5 seconds
        receiveMessageWait = 0 seconds
    }
}

```
run with command:
```bash
java -Dconfig.file=custom.config -jar elasticmq-server-X.XX.X.jar
```
* custom.config should be in same folder as the elasticmq-server

**``Note:``** For Local Docker deployment these all settings are taken care in elasticmq/elasticmq.conf. No separate installation needed for Windows and Linux. It will be handled through ``Local Docker Builds`` deployment section.

## Setup up Mongo Admin Account
In terminal (Mac), on Linux things are the same once a Mongo database connection is made.
start mongod
start instance of mongo
in mongo client type following:
```
use admin

Create a separate root user:
db.createUser({
  user: "root",
  pwd: "mySuperSecurePassword",
  roles: [ { role: "root", db: "admin" }, ]
});

Add a database specific user for Tesuto itself:
db.createUser({
  user: "tesuto",
  pwd: "tesuto",
  roles: [ { role: "dbOwner", db: "tesuto" } ]
});

exit
```
stop mongod
restart with following command:
```
mongod --auth
```
to log into mongo client use:
```
mongo --host 127.0.0.1 --port 27017 -u "tesuto" -p "tesuto" --authenticationDatabase "admin"
```
**``Note:``** For Local Docker deployment these all settings are taken care in mongo/set_mongodb_password.sh. No separate installation needed for Windows and Linux. It will be handled through ``Local Docker Builds`` deployment section.

## Setup Local DynamoDB ##

Download local DynamoDB:
```
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb
```
**``Note:``** For Local Docker deployment no separate installation needed for Windows and Linux. It will be handled through ``Local Docker Builds`` deployment section.

## MitreId Docker Install
docker build --tag tesuto/mitreid-connect .
docker run  --net=host -p 8080:8080 tesuto/mitreid-connect:latest
url: http://localhost:8080/openid-connect-server-webapp/login

## Setup Rules Editor ##
The Rules Editor is not strictly required to run tesuto.  It is useful for updating and adding
rules to the rules engine.

cd {project-root}/tesuto-drools-services/tesuto-drools-editor
```
```
mvn clean install
```
```
mvn spring-boot:run
```

## Initial Build ##
 * Insure that script ccc.sh is set to executable. (scripts/ccc.sh)
 * ``ccc.sh build complete`` to build all microservices and router to run assessments and placements.
 * ``ccc.sh build place`` (default build_all) to build microservices and router to run placements.
 * ``ccc.sh build_all assessment`` build microservices and router to run assessments only.
 
## To Launch Microservices ## 
 * ``ccc.sh start complete``
 * ``ccc.sh help `` to see available options.

## To View and Initial Seed Data ## 
 * ``https:localhost:8443/login``
 * ``username: superuser | password:password``
 * ``in browser url: https:localhost:8443/dashboard``
 * ``Scroll to bottom, click Seed Data button``


## UI Development (abbreviated, see more in the section below)
 * Install node.js
 * ``cd tesuto-web/src/main/webapp``
 * ``sudo npm install -g grunt-cli``
 * ``grunt build``
 

## Email Development ##
 * ``sudo npm install -g gulp-cli``
 * ``sudo npm install -g gulp``
 * ``npm install`` (within the tesuto-web/src/main/email)
 * ``foundation build`` or ``npm start``

### Test these steps on a new system ###
 * ``npm run start`` (good for development)
 * ``npm run build`` (for inlining, minifying and production code)

Reference: https://github.com/zurb/foundation-emails-template



TESUTO UI Build and Dev Scripts
===================================

TESUTO utilizes NodeJS as a tool to separate out UI code management from Maven. This allows 
UX developers the opportunity to modify and expand upon tools necessary for producing final UI files for deployments.

The tools include a main build script to be run by Maven which performs all tasks necessary for a deployment. 
Additionally, there are scripts to make UX development faster.

##Steps to setup NodeJS

* Install NodeJS (v0.12.5) [Download & Install NodeJS](https://nodejs.org/download/)
  * NodeJS later versions including v0.12.5 will also ship with NPM which is the package management tool for NodeJS
* Change your working directory in the console to the webapp directory that contains the "package.json" file
* Download all dependencies from NPM
```bash
sudo npm install
```

##Run the build script for deployment

This script MUST be run during the Maven build process in order to ensure all files necessary for a deployment are created and placed properly.  This build script will generally perform the following tasks:

* Copy all base files from 'ui_source' to 'ui' in the same directory
* Compile LESS files into final CSS files
* Concatenate libraries files
* Concatenate each Javascript Module into one file

This command should be run from Maven with the working directory set the directory that contains the "UI_ROOT_SOURCE/Gruntfile.js" file
```bash
grunt build
```

##Development environment setup

There are two ways to edit UI files.
* Make the change in ui_source and run ``grunt build``, then redeploy your app locally
* Utilize additional scripts to help speed up development within the UI

Take the following steps to setup your development environment:

1. Install the CCC Assess webapp and get it running in Tomcat
1. Make note of the absolute path on your machine to where the ``UI_ROOT_TARGET`` directory is within Tomcat
1. Edit the ``UI_ROOT_SOURCE/environment.json`` file in the source code and modify "target" to 
the absolute path mentioned in the step above
1. Now via the command line navigate to the 'UI_ROOT_SOURCE' in your source code where the 'Gruntfile.js' is located
1. Run the following command
```bash
grunt
```

The 'grunt' command will start the default task which is to setup watches on certain areas to build files and move them to your Tomcat during development on the fly. The following watches are in place:

* Watch global LESS variable and mixin changes -> recompile Twitter Bootstrap LESS, recompile all app LESS files, move CSS files to target
* Watch app level LESS files -> recompile all app LESS files, move CSS files to target
* Watch all app level html template files -> recompile all templates to generate templateCache files, move to target
* Watch all app level module Javascript files -> run linting and style checks, copy to target

This allows you to modify commonly changed source files and have them automatically transformed to a final form placed into a live running webapp.

NOTE: Adding a new Javascript module, adding a new library file will require changes in the scripts. You can follow the scripts and patterns set already to add the necessary lines of code.

##Accessing API Through Swagger

* Main Service Endpoints ``https://localhost:8443/swagger-ui.html``
* Placement Service Endpoints: ``https://localhost:8443/placement-service/swagger-ui.html``

#USE TESUTO SCRIPT FILE TO BUILD AND RUN

script file is found at scripts/tesuto
create a system variable $TESUTO_HOME that points to the tesuto home directory on your system.
XXXX can be: 
complete, assess, assess_app, min_assess_app, min_assess, place, place_app, (set of microservices)
or
admin,activation,content,delivery,drools-editor,placement, qti-importer, preview, reports, rules or ui

tesuto build XXXX      builds XXXX
tesuto build ui        builds only npm
tesuto build database  builds the database for all microservices
tesuto start services  (optional starts third party services if setup properly, see tesuto help for more info)
tesuto start XXXX      runs the ui microservice
tesuto debug XXXX      sets the ui microservice up for debugging on channel 8001
tesuto start router    launches the Zuul router
tesuto stop  XXXX      stops microservice or services




# Run Assess Dockerized
## Need Docker Registry credentials
Ask someone from operations for credentials to access docker.dev.ccctechcenter.org.
Login to the docker registry:

```docker login -u <ldap-uid> docker.dev.ccctechcenter.org```

## Local Docker Builds
To run locally Docker, you have to run only 

``sh docker-deploy.sh``


This script will:

 * Build all required projects
 * Create Docker images with latest builds
 * Deploy all Docker images including Mongo, DynamoDB, Redis, Postgres, elasticmq.

 
**``NOTE:``** On **Windows** we have to increase the Docker Memory from Settings -> Advanced Tab, up to 4608 MB/4.5 GB.

This will also start the router, which is only used locally.
In the AWS environment, the router is not needed or used.

Inline comments are supported in YMAL,  but not block comments.  Any service in the
docker-compose.yml file can be disabled from running by added a "#" at the beginning
of each line.

It may be necessary to pull some base images.  Try skipping it first.  
This seemed to only be an issue on Macs:
docker pull docker.dev.ccctechcenter.org/base-newrelic:1.2.0

# Additional Information
https://cccnext.jira.com/wiki/spaces/CCCAS/pages/172107738/Assess+Mothball+and+Final+Dev+Ops+Notes`
```
##Useful URLS for local development

* Import Assessments/Competency Maps: ``https://localhost:8443/import``
* Add sample user data: ``https://localhost:8443/import`` (bottom of page}
* Activation Service Endpoints: ``https://localhost:8443/activation-service/swagger-ui.html``
* Admin Service Endpoints: ``https://localhost:8443/admin-service/swagger-ui.html``
* Content Service Endpoints: ``https://localhost:8443/content-service/swagger-ui.html``
* Delivery Service Endpoints: ``https://localhost:8443/delivery-service/swagger-ui.html``
* Placement Service Endpoints: ``https://localhost:8443/placement-service/swagger-ui.html``
* Preview Service Endpoints: ``https://localhost:8443/preview-service/swagger-ui.html``
* Reports Service Endpoints: ``https://localhost:8443/reports-service/swagger-ui.html``
* Rules Service Endpoints: ``https://localhost:8443/rules-service/swagger-ui.html``
* UI Service Endpoints: ``https://localhost:8443/ui-service/swagger-ui.html``
* HealthChecks are the same but with ``https://localhost:8443/xxx-service/HealthCheck``
* Confirm Hookups to outside services: ``https://localhost:8443/xxx-service/rest-client/validate``
* Confirm Hookups to outside services: ``https://localhost:8443/xxx-service/configuration/clients/status``
* Troubleshoot configurations: ``https://localhost:8443/xxx-service/configuration``
* View version of service when on aws: ``https://localhost:8443/xxx-service/version``

* DynamoDb Local Database console: ``http://localhost:8000/shell/``

* Drools/Rules Editor: ``https://localhost:8448/rules-editor/ui``

