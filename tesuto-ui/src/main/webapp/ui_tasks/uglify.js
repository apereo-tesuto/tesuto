module.exports = function (grunt) {
    'use strict';

    var _ = require('underscore');
    var javascriptResourceConfigs = require('../grunt.javascript.js')(grunt);

    var uglifyConfigs = {
        uglify: {

            options: {
                compress: true,
                mangle: true,
                preserveComments: false,
                preserveLicenseComments: true,
                sourceMap: false
            },

            tesuto: {
                files: {}
            }
        }
    };

    // run though the grunt javascript resource configs and generate uglify settings to generate minified files
    _.each(javascriptResourceConfigs.groups, function (javaScriptGroup) {

        var javaScriptGroupSourceArray = _.map(javaScriptGroup.sources, function (sourcePath) {
            return javascriptResourceConfigs.sourcePrefix + sourcePath;
        });

        uglifyConfigs.uglify.tesuto.files[javascriptResourceConfigs.targetPrefix + javaScriptGroup.targetMinScript] = javaScriptGroupSourceArray;
    });

    // finally add in our dynamically generated uglify configs
    grunt.config.merge(uglifyConfigs);
};
