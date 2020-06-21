module.exports = function (grunt) {
    'use strict';

    grunt.config.merge({
        copy: {
            sourceToTarget: {
                files: [
                    {
                        src: ['**'],
                        dest: grunt.option('envConfigs').target + 'scripts',
                        expand: true,
                        cwd: 'ui_source/scripts'
                    }
                ]
            }
        }
    });
};