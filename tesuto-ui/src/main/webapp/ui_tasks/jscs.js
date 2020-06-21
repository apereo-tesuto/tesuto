module.exports = function (grunt) {
    'use strict';

    grunt.config.merge({
        jscs: {
            options: { // additional settings are in the .jscsrc file
                'excludeFiles': [
                    'ui_lib/**/*.js',
                    '**/*.json'
                ]
            },

            all: {
                files: {
                    src: [
                        'ui_source/scripts/modules/**/*.js',
                        '!ui_source/**/templateCache.js'
                    ]
                }
            },

            api_modules: {
                files: {
                    src: [
                        'ui_source/scripts/modules/modules_api/ccc_api_*/**/*.js'
                    ]
                }
            }
        }
    });
};
