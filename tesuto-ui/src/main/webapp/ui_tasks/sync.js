module.exports = function (grunt) {
    'use strict';

    grunt.config.merge({
        sync: {
            modules: {
                files: [{
                    cwd: 'ui_source/scripts/modules/',
                    src: [
                        '**/*.js',
                        '**/*.json'
                    ],
                    dest: grunt.option('envConfigs').target + 'scripts/modules/',
                }],
                verbose: true // Display log messages when copying files
            }
        }
    });

};