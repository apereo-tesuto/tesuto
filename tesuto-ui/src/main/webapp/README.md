CCC Assess UI Build and Dev Scripts
=========

CCC Assess utilizes NodeJS as a tool to seperate out UI code managment from maven. This allows UX developers the opportunity to modify and expand upon tools necessary for producing final UI files for deployments.

The tools include a main build script to be run by Maven which performs all tasks necessary for a deployment. Additionaly, there are scripts to make UX development faster.


Folder and File Structure
---------

* All UI source files are in the 'UI_ROOT_SOURCE/ui_source' directory. Development to source UI files will take place here.
* The build scripts will generate 'UI_ROOT_SOURCE/ui' directory which will contain the final UI files needed for a deployment
* The 'UI_ROOT_SOURCE/ui_tasks' directory holds script files used during build processess
* The 'UI_ROOT_SOURCE/client_server' directory currently houses a NodeJS express server script that can stand up a sample page used for testing of the build process
* The 'UI_ROOT_SOURCE/.jscsrc' file houses configurations for a JavaScript style enforcement plugin used in the build process. This file will only need to be tweaked to suit the desires of the development team.
* The 'UI_ROOT_SOURCE/environment.json' file. This file will only need to be used by developers to point their target directory to a running Tomcat server.
* The 'UI_ROOT_SOURCE/package.json' file lists configurations for NPM dependencies. This file should rarely need to be changed.
* After installing dependencies you will see a 'UI_ROOT_SOURCE/node_modules' directory that will hold all dependencies. You should never modify files here.


Steps to setup NodeJS
---------

* Install NodeJS (v0.12.5) [Download & Install NodeJS](https://nodejs.org/download/)
** NodeJS later versions including v0.12.5 will also ship with NPM which is the package managment tool for NodeJS
* Change your working directory in the console to the webapp directory that contains the "package.json" file
* Download all dependencies from NPM
```bash
sudo npm install
```


Run the build script for deployment
---------

This script MUST be run during the Maven build process in order to ensure all files necessary for a deployment are created and placed properly.  This build script will generally perform the following tasks:

* Copy all base files from 'ui_source' to 'ui' in the same directory
* Compile LESS files into final CSS files
* Concatenate libraries files
* Concatenate each Javascript Module into one file

This command should be run from Maven with the working directory set the directory that contains the "UI_ROOT_SOURCE/Gruntfile.js" file
```bash
grunt build --env=prod
```


Development environment setup
---------

There are two ways to edit UI files.
* Make the change in ui_source and run 'grunt build --prod', then redeploy your app locally
* Utilize additional scripts to help speed up development within the UI

Take the following steps to setup your development environment:

1. Intall the CCC Assess webapp and get it running in Tomcat
1. Make note of the absolute path on your machine to where the 'UI_ROOT_TARGET' directory is within Tomcat
1. Edit the 'UI_ROOT_SOURCE/environment.json' file in the source code and modify "target" to the absolute path mentioned in the step above
1. Now via the command line navigate to the 'UI_ROOT_SOURCE' in your source code whre the 'Gruntfile.js' is located
1. Run the following command
```bash
grunt
```

The 'grunt' command will start the default task which is to setup watches on certain areas to build files and move them to your Tomcat during development on the fly. The following watches are in place:

* Watch global LESS variable and mixin changes -> recompile Twitter Bootstrap LESS, recompile all app LESS files, move CSS files to target
* Watch app level LESS files -> recompile all app LESS files, move CSS files to target
* Watch all app level html template files -> recompile all templates to generate templateCache files, move to target
* Watch all app level module javascript files -> run linting and style checks, copy to target

This allows you to modify commonly changed source files and have them automatically transformed to a final form placed into a live running webapp.

NOTE: Adding a new JavaScript module, adding a new library file will require changes in the scripts. You can follow the scripts and patterns set already to add the necessary lines of code.