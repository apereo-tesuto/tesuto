module.exports = function (grunt) {
    'use strict';

    /*============ DEPENDENCIES ============*/

    var _ = require('underscore');
    var javascriptResourceConfigs = require('../grunt.javascript.js')(grunt);


    /*============ PRIVATE VARIABLES AND METHODS ============*/

    // store any watches you like here that are not dynamic
    var staticWatchConfigs = {

        watch: {
            // un comment and update if you want to use html files and generate angularJS templateCache
            // templates: {
            //     files: [
            //         'ui_source/scripts/modules/**/*.html'
            //     ],
            //     tasks: [
            //         // new modules and views will need to be added here
            //         'ngtemplates:ccc_components',
            //         'ngtemplates:ccc_assessment',
            //         'ngtemplates:ccc_view_common',
            //         'ngtemplates:ccc_asmt_player',
            //         'ngtemplates:ccc_view_assessment_player',
            //         'ngtemplates:ccc_view_assessment_preview',
            //         'ngtemplates:ccc_view_assessment_prototype',
            //         'ngtemplates:ccc_view_home',
            //         'ngtemplates:ccc_view_student'
            //     ]
            // },

            // when the overrides are modified for bootstrap we need to recompile bootstrap
            less_bootstrap: {
                files: [
                    'ui_source/styles/source/mixins/bootstrap_mixins_overrides.less',
                    'ui_source/styles/source/variables/bootstrap_variables_overrides.less'
                ],
                tasks: [
                    'less:bootstrap'
                ]
            },

            less_app: {
                files: [
                    'ui_source/styles/source/**/*.less',
                ],
                tasks: [
                    'less:app'
                ]
            },

            i18n: {
                files: [
                    'ui_source/scripts/modules/**/i18n/*.json'
                ],
                tasks: [
                    'sync:modules'
                ]
            },

            module_watch: {
                files: [
                   'ui_source/scripts/modules/**/*.js'
                ],
                tasks: [],
                options: {nospawn: true}
            }
        }
    };

    // load the static watch configs
    grunt.config.merge(staticWatchConfigs);


    /*============ CODE TO DYNAMICALLY GENERATE WATCH CONFIGS FOR JAVASCRIPT MODULES ============*/

    var getJavaScriptGroupSourceFileArray = function (javaScriptGroup) {
        return _.map(javaScriptGroup.sources, function (sourcePath) {
            return javascriptResourceConfigs.sourcePrefix + sourcePath;
        });
    };

    var generateGenericModuleTask = function (javaScriptGroup, taskName) {

        var taskConfigs = {};
        taskConfigs[taskName] = {};

        var taskId = taskName + ':javascript_module_' + javaScriptGroup.id;

        taskConfigs[taskName]['javascript_module_' + javaScriptGroup.id] = getJavaScriptGroupSourceFileArray(javaScriptGroup);

        grunt.config.merge(taskConfigs);

        return taskId;
    };

    var generateUglifyModuleTask = function (javaScriptGroup) {

        var taskName = 'uglify';

        var taskConfigs = {};
        taskConfigs[taskName] = {};

        var taskId = taskName + ':javascript_module_' + javaScriptGroup.id;
        taskConfigs[taskName]['javascript_module_' + javaScriptGroup.id] = {files: {}};

        taskConfigs[taskName]['javascript_module_' + javaScriptGroup.id].files[javascriptResourceConfigs.targetPrefix + javaScriptGroup.targetMinScript] = getJavaScriptGroupSourceFileArray(javaScriptGroup);

        if (javaScriptGroup.skipUglify) {
            taskConfigs[taskName]['javascript_module_' + javaScriptGroup.id].options = {
                mangle: false,
                sourceMap: false
            };
        }

        grunt.config.merge(taskConfigs);

        return taskId;
    };

    var generateModuleWatchTasks = function (javaScriptGroup) {

        var watchTaskId = 'javascript_module_' + javaScriptGroup.id;

        var jsHintTaskId = generateGenericModuleTask(javaScriptGroup, 'jshint');
        var jscsTaskId = generateGenericModuleTask(javaScriptGroup, 'jscs');
        var uglifyTaskId = generateUglifyModuleTask(javaScriptGroup);

        grunt.registerTask(watchTaskId, [
            jsHintTaskId,
            jscsTaskId,
            uglifyTaskId,
            'sync:modules'
        ]);

        return watchTaskId;
    };

    var loadConcurrentConfigs = function () {

        var concurrentConfigs = {
            concurrent: {
                options: {
                    logConcurrentOutput: true
                },
                watch: {}
            }
        };

        var staticWatchKeys = _.keys(staticWatchConfigs.watch);

        var watchTasks = _.map(staticWatchKeys, function (key) {
            return 'watch:' + key;
        });

        concurrentConfigs.concurrent.watch.tasks = watchTasks;

        grunt.config.merge(concurrentConfigs);
    };

    var watchMap = {};
    var generateWatchMap = function () {

        _.each(javascriptResourceConfigs.groups, function (javaScriptGroup, groupIndex) {

            var javaScriptGroupSourceArray = getJavaScriptGroupSourceFileArray(javaScriptGroup);
            var watchTaskId = generateModuleWatchTasks(javaScriptGroup);

            _.each(javaScriptGroupSourceArray, function (sourceFile) {
                watchMap[sourceFile] = watchTaskId
            });
        });
    };


    /*============ INITIALIZATION ============*/

    // start listening to watch events to broker the right corresponding module task
    grunt.event.on('watch', function(action, filepath, target) {

        // we only care about files changed in module_watch in order to run module related tasks
        if (target === 'module_watch') {

            if (watchMap[filepath]) {
                grunt.task.run(watchMap[filepath]);
            } else {
                console.log("*** WARNING *** < " + filepath + " > did not match any registered module files");
            }
        }
    });

    grunt.registerTask('startWatcher', '', function () {

        // load the concurrent watches
        loadConcurrentConfigs();

        // start all the concurrent watches
        grunt.task.run('concurrent:watch');
    });


    // generate the watch map which also generates javascript module update tasks
    generateWatchMap();
};
