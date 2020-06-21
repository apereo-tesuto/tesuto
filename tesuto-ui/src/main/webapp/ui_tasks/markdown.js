module.exports = function (grunt) {
    'use strict';

    // Configuration.
    grunt.config.merge({
        markdown: {
            readme: {
                files: [
                    {
                        expand: true,
                        src: 'README.md',
                        dest: '',
                        ext: '.html'
                    }
                ]
            }
        }
    });
};
