module.exports = function (grunt) {
    'use strict';

    // Configuration.
    grunt.config.merge({
        clean: {
             application: [
                 grunt.option('envConfigs').application,
                 grunt.option('envConfigs').applicationJSPCommon,
                 grunt.option('envConfigs').applicationJSP
             ]
        }
    });
};
