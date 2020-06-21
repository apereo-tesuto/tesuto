// Grunt.
module.exports = function (grunt) {
    'use strict';

    // Plugins and dependencies
    var extend = require('node.extend');
    var colors = require('colors');

    // Configuration.
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),
        jshint: {
            options: {
                "loopfunc": true
            }
        }
    });

    // You can create an "environment.json" file and override any of these properties that are used in some tasks
    var defaultEnvironmentConfigs = {
        target: '../resources/static/ui/',
        dynamicJSPTarget: '../resources/META-INF/resources/WEB-INF/common/dynamic/'
    };

    grunt.option('envConfigs', defaultEnvironmentConfigs);


    /*============ GRUNT OPTIONS ============*/

    // by default the environment should be prod
    var env = grunt.option('env') || 'prod';
    grunt.option('env', env);

    // see if an environment.json file has been created and merge in any overrides
    try {

        var envConfigs = grunt.file.readJSON('environment.json');
        var updatedEnvironmentConfigs = extend(grunt.option('envConfigs'), envConfigs);
        grunt.option('envConfigs', updatedEnvironmentConfigs);

    } catch (e) {
        console.log('No custom environment.json file detected');
    }


    /*============ LOAD IN ADDITIONAL PLUGINS ============*/

    require('time-grunt')(grunt);


    /*============ LOAD IN TASK RELATED PLUGINS ============*/

    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-angular-templates');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-concurrent');
    grunt.loadNpmTasks('grunt-sync');
    grunt.loadNpmTasks('grunt-contrib-yuidoc');
    grunt.loadNpmTasks('grunt-jscs');


    /*============ LOAD IN CUSTOM EXTERNAL TASKS IN THE UI_TASKS DIRECTORY ============*/

    grunt.task.loadTasks('ui_tasks');


    /*============ PRIVATE TASK ROLL UPS ============*/

    // Execute tasks that prepare JavaScript resources for a use in a production environment.
    grunt.registerTask('javascriptBuild', [
        'uglify:tesuto',
        'generateModuleJSPs',
        'jshint:all',
        'jscs:all'
    ]);


    /*============ PUBLIC TASKS ============*/

    // grunt watcher
    // Execute tasks that watch for changes in js, less, and templates
    grunt.registerTask('watcher', ['startWatcher']);

    // grunt build
    // MAIN TASK TO RUN DURING DEPLOYMENT : Execute tasks that prepare code base for use in a development environment.
    // NOTE: currently not running ngtemplates tasks because templates are in the dirctive js files, not using templateCache right now
    grunt.registerTask('build', [
        'copy:sourceToTarget',
        'less:bootstrap',
        'less:app',
        'javascriptBuild'
    ]);


    /*============ DEFAULT TASK IS TO JUST WATCH LESS AND JS CHANGES ============*/

    grunt.registerTask('default', ['watcher']);


    /*============ INIT CONFIG ============*/
};