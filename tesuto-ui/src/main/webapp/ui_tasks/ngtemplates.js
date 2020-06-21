module.exports = function (grunt) {
    'use strict';

    var getTemplateOptions = function (moduleName) {
        return {
            module: moduleName,
            standalone: true,
            prefix: '',
            url: function (templateURL) {
                var filePieces = templateURL.split(/\\|\//g);
                return filePieces[filePieces.length -1];
            },
            htmlmin: {
                collapseBooleanAttributes: true,
                collapseWhitespace: true,
                removeAttributeQuotes: true,
                removeComments: true,
                removeEmptyAttributes: true,
                removeRedundantAttributes: true,
                removeScriptTypeAttributes: true,
                removeStyleLinkTypeAttributes: true
            }
        };
    };





    grunt.config.merge({
        ngtemplates: {

            /*============= CCC COMPONENTS =============*/

            ccc_components: {
                options: getTemplateOptions('CCC.Components'),
                cwd: 'ui_source/scripts/modules/ccc_components/',
                src: '**/*.html',
                dest: 'ui/scripts/modules/ccc_components/templateCache.js'
            },

            ccc_assessment: {
                options: getTemplateOptions('CCC.Assessment'),
                cwd: 'ui_source/scripts/modules/ccc_assessment/',
                src: '**/*.html',
                dest: 'ui/scripts/modules/ccc_assessment/templateCache.js'
            },

            ccc_asmt_player: {
                options: getTemplateOptions('CCC.AsmtPlayer'),
                cwd: 'ui_source/scripts/modules/ccc_asmt_player/',
                src: '**/*.html',
                dest: 'ui/scripts/modules/ccc_asmt_player/templateCache.js'
            },

            ccc_calculator: {
                options: getTemplateOptions('CCC.Calculator'),
                cwd: 'ui_source/scripts/modules/ccc_calculator/',
                src: '**/*.html',
                dest: 'ui/scripts/modules/ccc_calculator/templateCache.js'
            },

            /*============= VIEWS (each new view will need to be added here) =============*/

            ccc_view_assessment_player: {
                options: getTemplateOptions('CCC.View.AssessmentPlayer'),
                cwd: 'ui_source/scripts/modules/ccc_view_assessment_player/',
                src: '**/*.html',
                dest: 'ui/scripts/modules/ccc_view_assessment_player/templateCache.js'
            },

            ccc_view_assessment_preview: {
                options: getTemplateOptions('CCC.View.AssessmentPreview'),
                cwd: 'ui_source/scripts/modules/ccc_view_assessment_preview/',
                src: '**/*.html',
                dest: 'ui/scripts/modules/ccc_view_assessment_preview/templateCache.js'
            },

            ccc_view_assessment_prototype: {
                options: getTemplateOptions('CCC.View.AssessmentPrototype'),
                cwd: 'ui_source/scripts/modules/ccc_view_assessment_prototype/',
                src: '**/*.html',
                dest: 'ui/scripts/modules/ccc_view_assessment_prototype/templateCache.js'
            },

            ccc_view_common: {
                options: getTemplateOptions('CCC.View.Common'),
                cwd: 'ui_source/scripts/modules/ccc_view_standard_common/',
                src: '**/*.html',
                dest: 'ui/scripts/modules/ccc_view_standard_common/templateCache.js'
            },

            ccc_view_home: {
                options: getTemplateOptions('CCC.View.Home'),
                cwd: 'ui_source/scripts/modules/ccc_view_standard_home/',
                src: '**/*.html',
                dest: 'ui/scripts/modules/ccc_view_standard_home/templateCache.js'
            },

            ccc_view_student: {
                options: getTemplateOptions('CCC.View.Student'),
                cwd: 'ui_source/scripts/modules/ccc_view_standard_student/',
                src: '**/*.html',
                dest: 'ui/scripts/modules/ccc_view_standard_student/templateCache.js'
            }

        }
    });
};
