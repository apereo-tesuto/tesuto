module.exports = function (grunt) {
    'use strict';

    var lessConfigs = {
        less: {
            bootstrap: {
                files: {} // we will insert files here dynamically below
            },
            app: {
                files: {} // we will insert files here dynamically below
            }
        }
    };

    // re-compile bootstrap because we could have variable or mixin overrides
    lessConfigs.less.bootstrap.files[grunt.option('envConfigs').target + '../ui_lib/bootstrap-3.3.5/dist/css/bootstrap.css'] = grunt.option('envConfigs').target + '../ui_lib/bootstrap-3.3.5/less/bootstrap.less';

    // re-compile the shared modules less
    lessConfigs.less.app.files[grunt.option('envConfigs').target + 'styles/css/ccc_assess.css'] = 'ui_source/styles/source/ccc_assess/styles.less';
    lessConfigs.less.app.files[grunt.option('envConfigs').target + 'styles/css/ccc_components.css'] = 'ui_source/styles/source/ccc_components/styles.less';
    lessConfigs.less.app.files[grunt.option('envConfigs').target + 'styles/css/ccc_assessment.css'] = 'ui_source/styles/source/ccc_assessment/styles.less';
    lessConfigs.less.app.files[grunt.option('envConfigs').target + 'styles/css/ccc_asmt_player.css'] = 'ui_source/styles/source/ccc_asmt_player/styles.less';
    lessConfigs.less.app.files[grunt.option('envConfigs').target + 'styles/css/ccc_calculator.css'] = 'ui_source/styles/source/ccc_calculator/styles.less';
    lessConfigs.less.app.files[grunt.option('envConfigs').target + 'styles/css/ccc_activity_monitor.css'] = 'ui_source/styles/source/ccc_activity_monitor/styles.less';
    lessConfigs.less.app.files[grunt.option('envConfigs').target + 'styles/css/ccc_activations.css'] = 'ui_source/styles/source/ccc_activations/styles.less';
    lessConfigs.less.app.files[grunt.option('envConfigs').target + 'styles/css/ccc_placement.css'] = 'ui_source/styles/source/ccc_placement/styles.less';

    // re-compile the ccc_view_assessment_player less
    lessConfigs.less.app.files[grunt.option('envConfigs').target + 'styles/css/views/view_assessment_player.css'] = 'ui_source/styles/source/ccc_view_assessment_player/styles.less';

    // re-compile the ccc_view_assessment_preview less
    lessConfigs.less.app.files[grunt.option('envConfigs').target + 'styles/css/views/view_assessment_preview.css'] = 'ui_source/styles/source/ccc_view_assessment_preview/styles.less';

    // re-compile the ccc_view_assessment_print less
    lessConfigs.less.app.files[grunt.option('envConfigs').target + 'styles/css/views/view_assessment_print.css'] = 'ui_source/styles/source/ccc_view_assessment_print/styles.less';

    // re-compile the view home less
    lessConfigs.less.app.files[grunt.option('envConfigs').target + 'styles/css/views/view_home.css'] = 'ui_source/styles/source/ccc_view_standard_home/styles.less';

    // re-compile the view student less
    lessConfigs.less.app.files[grunt.option('envConfigs').target + 'styles/css/views/view_student.css'] = 'ui_source/styles/source/ccc_view_standard_student/styles.less';

    // re-compile the view remote_proctor less
    lessConfigs.less.app.files[grunt.option('envConfigs').target + 'styles/css/views/view_remote_proctor.css'] = 'ui_source/styles/source/ccc_view_remote_proctor/styles.less';

    // re-compile the view dashboard less
    lessConfigs.less.app.files[grunt.option('envConfigs').target + 'styles/css/views/view_dashboard.css'] = 'ui_source/styles/source/ccc_view_standard_dashboard/styles.less';

    grunt.config.merge(lessConfigs);
};
