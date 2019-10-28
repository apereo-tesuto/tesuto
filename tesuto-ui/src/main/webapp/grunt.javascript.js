module.exports = function (grunt) {

    /**
     * Groups of javascript configurations for minification and generation of JSP files
     * For each JSP that will contain a group of scripts (module), create a group and push in the ordered scripts needed
     * This configuration can then be used to uglfigy/minify, then generate the specified jsp
     * with logic that can switch between the source and minified files
     * This data structure is also pulled into the grunt watch command to generate module specific watches
     */
    var javascriptConfigs = {
        sourcePrefix: 'ui_source/', // prepended when looking for source files
        targetPrefix: grunt.option('envConfigs').target, // prepended when copying / building files and when generating jsp
        groups: []
    };

    var dynamicJSPTarget = grunt.option('envConfigs').dynamicJSPTarget;


    /*============= COMMON LIBARARY FILES ============*/

    javascriptConfigs.groups.push({
        id: 'lib',
        targetJsp: dynamicJSPTarget + 'scripts.common.jsp',
        targetMinScript: 'scripts/build/lib.min.js',
        isLibraryGroup: true,
        sources: [

            // jquery and jquery plugins
            '../../resources/static/ui_lib/jquery-2.1.4/jquery-2.1.4.min.js',
            '../../resources/static/ui_lib/CCC/jquery-plugins/jquery-plugins.js',

            // fast click for touch devices
            '../../resources/static/ui_lib/fast-click/fast-click.min.js',

            // underscore
            '../../resources/static/ui_lib/underscore/underscore-1.8.3.min.js',

            // browser class
            '../../resources/static/ui_lib/browser-class/browser-class.min.js',

            // seeded random (used for generating psuedo random numbers in fake data)
            '../../resources/static/ui_lib/seeded-random/seeded-random.min.js',

            // angular
            '../../resources/static/ui_lib/angular-1.5.5/angular.min.js',
            '../../resources/static/ui_lib/angular-1.5.5/angular-cookies.min.js',
            '../../resources/static/ui_lib/angular-1.5.5/angular-loader.min.js',
            '../../resources/static/ui_lib/angular-1.5.5/angular-resource.min.js',
            '../../resources/static/ui_lib/angular-1.5.5/angular-sanitize.min.js',
            '../../resources/static/ui_lib/angular-1.5.5/angular-touch.min.js',
            '../../resources/static/ui_lib/angular-1.5.5/angular-messages.min.js',
            '../../resources/static/ui_lib/angular-1.5.5/angular-animate.min.js',
            '../../resources/static/ui_lib/angular-1.5.5/angular-aria.min.js',

            // angular cloneable
            '../../resources/static/ui_lib/angular-cloneable/module.js',
            '../../resources/static/ui_lib/angular-cloneable/classes/Cloneable.cls.js',

            // additional boostrap components
            '../../resources/static/ui_lib/ui-bootstrap-custom-build-2.5.0/ui-bootstrap-custom-tpls-2.5.0.js',

            // drag and drop library
            '../../resources/static/ui_lib/jquery-ui-1.11.4.custom/jquery-ui.min.js',
            '../../resources/static/ui_lib/angular-drag/angular-drag.min.js',

            // angular ui-router
            '../../resources/static/ui_lib/angular-ui-router/angular-ui-router-0.2.15.min.js',

            // angular translate, message-format, and interpolation
            '../../resources/static/ui_lib/angular-translate/angular-translate.min.js',
            '../../resources/static/ui_lib/angular-translate/angular-translate-loader-static-files.min.js',

            // angular local storage
            '../../resources/static/ui_lib/angular-local-storage/angular-local-storage.min.js',

            // angular-idle
            '../../resources/static/ui_lib/ng-idle/angular-idle.min.js',

            // interpolation, localization, pluralization
            '../../resources/static/ui_lib/message-format/message-format.js',
            '../../resources/static/ui_lib/message-format/locales.js',
            '../../resources/static/ui_lib/angular-translate/angular-translate-interpolation-messageformat.min.js',

            // ui bootstrap plugins
            '../../resources/static/ui_lib/bootstrap-3.3.5/dist/js/bootstrap.min.js',
            '../../resources/static/ui_lib/bootstrap-notify/bootstrap-notify.min.js'
        ]
    });


    /*============= MODULE : CCC.Assess ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.assess',
        targetJsp: dynamicJSPTarget + 'scripts.assess.jsp',
        targetMinScript: 'scripts/build/module.ccc_assess.min.js',
        libraries: [
            // we include moment here because this module also wraps it in a service
            '../../resources/static/ui_lib/moment/moment.min.js',
            '../../resources/static/ui_lib/moment/moment-round.min.js',
        ],
        sources: [
            // the actual CCC.Assess module
            'scripts/modules/modules_common/ccc_assess/module.js',
            'scripts/modules/modules_common/ccc_assess/services/TranslateFileService.svc.js',
            'scripts/modules/modules_common/ccc_assess/services/ErrorHandlerService.svc.js',
            'scripts/modules/modules_common/ccc_assess/services/CCCUtils.svc.js',
            'scripts/modules/modules_common/ccc_assess/services/Moment.svc.js',
            'scripts/modules/modules_common/ccc_assess/services/RoutePermissionsService.svc.js',
            'scripts/modules/modules_common/ccc_assess/classes/VersionableAPIClass.cls.js'
        ]
    });


    /*============= MODULE : CCC.Components (component re-usable across all views) ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.components',
        targetJsp: dynamicJSPTarget + 'scripts.components.jsp',
        targetMinScript: 'scripts/build/module.ccc_components.min.js',
        sources: [

            // module and configs
            'scripts/modules/modules_common/ccc_components/module.js',

            // services
            'scripts/modules/modules_common/ccc_components/services/BackgroundContentService.svc.js',
            'scripts/modules/modules_common/ccc_components/services/ForegroundTrayService.svc.js',
            'scripts/modules/modules_common/ccc_components/services/FocusService.svc.js',
            'scripts/modules/modules_common/ccc_components/services/LanguageService.svc.js',
            'scripts/modules/modules_common/ccc_components/services/ModalService.svc.js',
            'scripts/modules/modules_common/ccc_components/services/OverlayService.svc.js',
            'scripts/modules/modules_common/ccc_components/services/NotificationService.svc.js',
            'scripts/modules/modules_common/ccc_components/services/AriaLiveService.svc.js',
            'scripts/modules/modules_common/ccc_components/services/IdleService.svc.js',
            'scripts/modules/modules_common/ccc_components/services/WindowFocusService.svc.js',
            'scripts/modules/modules_common/ccc_components/services/RouteFreezeService.svc.js',
            'scripts/modules/modules_common/ccc_components/services/NavigationFreezeService.svc.js',
            'scripts/modules/modules_common/ccc_components/services/CSSService.svc.js',
            'scripts/modules/modules_common/ccc_components/services/RoleService.svc.js',
            'scripts/modules/modules_common/ccc_components/services/DateService.svc.js',
            'scripts/modules/modules_common/ccc_components/services/UtilsService.svc.js',

            // classes
            'scripts/modules/modules_common/ccc_components/classes/ActivityServiceClass.cls.js',
            'scripts/modules/modules_common/ccc_components/classes/ActivityChannelClass.cls.js',
            'scripts/modules/modules_common/ccc_components/classes/BooleanGroupClass.cls.js',
            'scripts/modules/modules_common/ccc_components/classes/FacetedSearchManagerClass.cls.js',

            // entities (TODO, turn these into classes)
            'scripts/modules/modules_common/ccc_components/entities/ObservableEntity.ent.js',
            'scripts/modules/modules_common/ccc_components/entities/ViewManagerEntity.ent.js',
            'scripts/modules/modules_common/ccc_components/entities/GroupedItemManagerEntity.ent.js',

            // common modal directives
            'scripts/modules/modules_common/ccc_components/directives/ccc-modal-server-error/ccc-modal-server-error.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-modal-server-error-json-only/ccc-modal-server-error-json-only.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-modal-unauthorized/ccc-modal-unauthorized.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-modal-session-timeout/ccc-modal-session-timeout.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-modal-alert/ccc-modal-alert.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-modal-confirm/ccc-modal-confirm.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-modal-custom/ccc-modal-custom.dir.js',

            // directives
            'scripts/modules/modules_common/ccc_components/directives/ccc-click-no-focus/ccc-click-no-focus.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-view-manager/ccc-view-manager.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-error-details/ccc-error-details.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-background/ccc-background.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-foreground-tray/ccc-foreground-tray.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-responds-to-dialogs/ccc-responds-to-dialogs.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-lang/ccc-lang.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-required-array/ccc-required-array.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-required-email/ccc-required-email.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-min-date/ccc-min-date.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-max-date/ccc-max-date.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-force-character-length/ccc-force-character-length.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-scroll-indicator/ccc-scroll-indicator.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-show-errors/ccc-show-errors.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-facet-list/ccc-facet-list.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-grouped-items/ccc-grouped-items.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-clearable-input/ccc-clearable-input.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-validation-badge/ccc-validation-badge.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-content-loading-placeholder/ccc-content-loading-placeholder.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-options-select-language/ccc-options-select-language.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-update-page-title/ccc-update-page-title.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-instructions-player/ccc-instructions-player.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-choice-input/ccc-choice-input.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-date-picker/ccc-date-picker.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-input-invalid/ccc-input-invalid.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-input-file/ccc-input-file.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-input-date/ccc-input-date.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-overlay-calendar/ccc-overlay-calendar.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-nested-item-selector/ccc-nested-item-selector.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-boolean-group/ccc-boolean-group.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-focusable/ccc-focusable.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-info/ccc-info.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-item-dropdown/ccc-item-dropdown.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-validation-expression/ccc-validation-expression.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-image/ccc-image.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-detailed-select/ccc-detailed-select.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-dropdown-focus/ccc-dropdown-focus.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-label-required/ccc-label-required.dir.js',
            'scripts/modules/modules_common/ccc_components/directives/ccc-view-manager-static-view/ccc-view-manager-static-view.dir.js'
        ]
    });


    /*============= MODULE : CCC.Assessment (files needed to render assessment items) ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.assessment',
        targetJsp: dynamicJSPTarget + 'scripts.assessment.jsp',
        targetMinScript: 'scripts/build/module.ccc_assessment.min.js',
        sources: [

            // module and configs
            'scripts/modules/modules_common/ccc_assessment/module.js',
            'scripts/modules/modules_common/ccc_assessment/configs/interactionsMap.config.js',

            // services
            'scripts/modules/modules_common/ccc_assessment/services/InteractionFactoryService.svc.js',
            'scripts/modules/modules_common/ccc_assessment/services/InteractionUtilsService.svc.js',
            'scripts/modules/modules_common/ccc_assessment/services/AsmtTaskUtilsService.svc.js',
            'scripts/modules/modules_common/ccc_assessment/services/AsmtTaskItemUtilsService.svc.js',

            // factories
            'scripts/modules/modules_common/ccc_assessment/factories/interactions/InteractionFactory.fct.js',
            'scripts/modules/modules_common/ccc_assessment/factories/interactions/InteractionChoiceFactory.fct.js',
            'scripts/modules/modules_common/ccc_assessment/factories/interactions/InteractionInlineChoiceFactory.fct.js',
            'scripts/modules/modules_common/ccc_assessment/factories/interactions/InteractionTextEntryFactory.fct.js',
            'scripts/modules/modules_common/ccc_assessment/factories/interactions/InteractionExtendedTextEntryFactory.fct.js',
            'scripts/modules/modules_common/ccc_assessment/factories/interactions/InteractionMatchFactory.fct.js',

            // directives
            'scripts/modules/modules_common/ccc_assessment/directives/ccc-mathml-click/ccc-mathml-click.dir.js',
            'scripts/modules/modules_common/ccc_assessment/directives/ccc-dropdown-keyboard-nav/ccc-dropdown-keyboard-nav.dir.js',
            'scripts/modules/modules_common/ccc_assessment/directives/ccc-assessment-item/ccc-assessment-item.dir.js',
            'scripts/modules/modules_common/ccc_assessment/directives/ccc-interaction/ccc-interaction.dir.js',
            'scripts/modules/modules_common/ccc_assessment/directives/ccc-question-label/ccc-question-label.dir.js',
            'scripts/modules/modules_common/ccc_assessment/directives/ccc-toggle-input/ccc-toggle-input.dir.js',
            'scripts/modules/modules_common/ccc_assessment/directives/interaction_types/ccc-interaction-choice/ccc-interaction-choice.dir.js',
            'scripts/modules/modules_common/ccc_assessment/directives/interaction_types/ccc-interaction-choice/ccc-interaction-choice-multiple.dir.js',
            'scripts/modules/modules_common/ccc_assessment/directives/interaction_types/ccc-interaction-choice/ccc-interaction-choice-single.dir.js',
            'scripts/modules/modules_common/ccc_assessment/directives/interaction_types/ccc-interaction-inline-choice/ccc-interaction-inline-choice.dir.js',
            'scripts/modules/modules_common/ccc_assessment/directives/interaction_types/ccc-interaction-text-entry/ccc-interaction-text-entry.dir.js',
            'scripts/modules/modules_common/ccc_assessment/directives/interaction_types/ccc-interaction-extended-text-entry/ccc-interaction-extended-text-entry.dir.js',
            'scripts/modules/modules_common/ccc_assessment/directives/interaction_types/ccc-interaction-match/ccc-interaction-match.dir.js'
        ]
    });


    /*============= MODULE : CCC.Calculator ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.calculator',
        targetJsp: dynamicJSPTarget + 'scripts.calculator.jsp',
        targetMinScript: 'scripts/build/module.ccc_calculator.min.js',
        sources: [

            // module and configs
            'scripts/modules/modules_common/ccc_calculator/module.js',

            // classes
            'scripts/modules/modules_common/ccc_calculator/classes/CalculatorController.cls.js',

            // directives
            'scripts/modules/modules_common/ccc_calculator/directives/ccc-calculator.dir.js',
        ]
    });

    /*============= MODULE : CCC.Math ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.math',
        targetJsp: dynamicJSPTarget + 'scripts.math.jsp',
        targetMinScript: 'scripts/build/module.ccc_math.min.js',
        libraries: [
            '../../resources/static/ui_lib/MathJax_2.7.0/MathJax.js?config=default'
        ],
        sources: [

            // module and configs
            'scripts/modules/modules_common/ccc_math/module.js',

            // classes
            'scripts/modules/modules_common/ccc_math/services/MathService.svc.js'
        ]
    });


    /*============= MODULE : CCC.AsmtPlayer ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.asmtPlayer',
        targetJsp: dynamicJSPTarget + 'scripts.asmt_player.jsp',
        targetMinScript: 'scripts/build/module.ccc_asmt_player.min.js',
        sources: [

            // module and configs
            'scripts/modules/modules_common/ccc_asmt_player/module.js',
            'scripts/modules/modules_common/ccc_asmt_player/conf/layouts.conf.js',
            'scripts/modules/modules_common/ccc_asmt_player/conf/AsmtPlayerEventFilterTransformMap.conf.js',

            // services
            'scripts/modules/modules_common/ccc_asmt_player/services/AsmtService.svc.js',
            'scripts/modules/modules_common/ccc_asmt_player/services/AssessmentTaskLayoutService.svc.js',
            'scripts/modules/modules_common/ccc_asmt_player/services/CalculatorToolService.svc.js',
            'scripts/modules/modules_common/ccc_asmt_player/services/ToolsPermissionService.svc.js',
            'scripts/modules/modules_common/ccc_asmt_player/services/AsmtPlayerEventTransformService.svc.js',
            'scripts/modules/modules_common/ccc_asmt_player/services/CurrentUserService.svc.js',

            // classes
            'scripts/modules/modules_common/ccc_asmt_player/classes/TaskSetServiceClass.cls.js',
            'scripts/modules/modules_common/ccc_asmt_player/classes/TimerClass.cls.js',

            // directives
            'scripts/modules/modules_common/ccc_asmt_player/directives/ccc-asmt-player/ccc-asmt-player.dir.js',
            'scripts/modules/modules_common/ccc_asmt_player/directives/ccc-asmt-player/ccc-asmt-player-toolbar-top.dir.js',
            'scripts/modules/modules_common/ccc_asmt_player/directives/ccc-asmt-player/ccc-asmt-player-toolbar-bottom.dir.js',
            'scripts/modules/modules_common/ccc_asmt_player/directives/ccc-asmt-player/ccc-asmt-player-content.dir.js',
            'scripts/modules/modules_common/ccc_asmt_player/directives/ccc-asmt-player/ccc-asmt-player-student-options.dir.js',
            'scripts/modules/modules_common/ccc_asmt_player/directives/ccc-asmt-player/ccc-asmt-player-navigation.dir.js',
            'scripts/modules/modules_common/ccc_asmt_player/directives/ccc-asmt-player/ccc-draggable-calculator.dir.js',
            'scripts/modules/modules_common/ccc_asmt_player/directives/ccc-asmt-player-view/ccc-asmt-player-view.dir.js',

            // modal directives
            'scripts/modules/modules_common/ccc_asmt_player/directives/ccc-asmt-player/modals/ccc-asmt-task-warning-modal.dir.js',
            'scripts/modules/modules_common/ccc_asmt_player/directives/ccc-asmt-player/modals/ccc-asmt-task-score-modal.dir.js',
            'scripts/modules/modules_common/ccc_asmt_player/directives/ccc-asmt-player/modals/ccc-asmt-task-pause-modal.dir.js',
            'scripts/modules/modules_common/ccc_asmt_player/directives/ccc-asmt-player/modals/ccc-asmt-progress-warning-modal.dir.js',
            'scripts/modules/modules_common/ccc_asmt_player/directives/ccc-asmt-player/modals/ccc-asmt-modal-not-found.dir.js',
            'scripts/modules/modules_common/ccc_asmt_player/directives/ccc-asmt-player/modals/ccc-asmt-modal-server-error.dir.js'
        ]
    });


    /*============= MODULE : CCC.View.AsmtPlayer ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.view.asmtPlayer',
        targetJsp: dynamicJSPTarget + 'scripts.view.assessmentPlayer.jsp',
        targetMinScript: 'scripts/build/module.ccc_view_assessment_player.min.js',
        sources: [

            // module and configs
            'scripts/modules/modules_view/ccc_view_assessment_player/module.js',
            'scripts/modules/modules_view/ccc_view_assessment_player/conf/routes.conf.js',

            // services
            'scripts/modules/modules_view/ccc_view_assessment_player/services/PlayerTaskSetService.svc.js',

            // view directives
            'scripts/modules/modules_view/ccc_view_assessment_player/view/ccc-view-assessment-player.dir.js',
            'scripts/modules/modules_view/ccc_view_assessment_player/view/ccc-view-assessment-complete.dir.js'
        ]
    });


    /*============= MODULE : CCC.ActivityMonitor ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.activityMonitor',
        targetJsp: dynamicJSPTarget + 'scripts.activityMonitor.jsp',
        targetMinScript: 'scripts/build/module.ccc_activity_monitor.min.js',
        libraries: [
            // library dependencies
            '../../resources/static/ui_lib/d3/d3.min.js'
        ],
        sources: [
            // module and configs
            'scripts/modules/modules_common/ccc_activity_monitor/module.js',
            'scripts/modules/modules_common/ccc_activity_monitor/directives/ccc-activity-monitor.dir.js'
        ]
    });


    /*============= MODULE : CCC.Activations ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.activations',
        targetJsp: dynamicJSPTarget + 'scripts.activations.jsp',
        targetMinScript: 'scripts/build/module.ccc_activations.min.js',
        sources: [

            // module and configs
            'scripts/modules/modules_common/ccc_activations/module.js',

            // services
            'scripts/modules/modules_common/ccc_activations/services/ActivationLogService.svc.js',
            'scripts/modules/modules_common/ccc_activations/services/CoerceActivationService.svc.js',
            'scripts/modules/modules_common/ccc_activations/services/CollisionService.svc.js',

            // directives
            'scripts/modules/modules_common/ccc_activations/directives/ccc-activation-logs/ccc-activation-logs.dir.js',
            'scripts/modules/modules_common/ccc_activations/directives/ccc-activation-card/ccc-activation-card.dir.js'
        ]
    });


    /*============= MODULE : CCC.Placement ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.placement',
        targetJsp: dynamicJSPTarget + 'scripts.placement.jsp',
        targetMinScript: 'scripts/build/module.ccc_placement.min.js',
        sources: [

            // module and configs
            'scripts/modules/modules_common/ccc_placement/module.js',

            // directives
            'scripts/modules/modules_common/ccc_placement/directives/ccc-competency-results/ccc-competency-results.dir.js',
            'scripts/modules/modules_common/ccc_placement/directives/ccc-overall-performance/ccc-overall-performance.dir.js',
            'scripts/modules/modules_common/ccc_placement/directives/ccc-performance-by-category/ccc-performance-by-category.dir.js',
            'scripts/modules/modules_common/ccc_placement/directives/ccc-student-test-results/ccc-student-test-results.dir.js'
        ]
    });

    /*============= MODULE : CCC.API.PlacementRequest ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.placementrequest',
        targetJsp: dynamicJSPTarget + 'scripts.api.placementrequest.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_placement_request.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_placement_request/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_placement_request/services/PlacementRequestAPIService.svc.js'
        ]
    });


    /*============= MODULE : CCC.View.AssessmentPreview ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.view.assessmentPreview',
        baseUrl: 'scripts/modules/ccc_view_assessment_player',
        targetJsp: dynamicJSPTarget + 'scripts.view.assessmentPreview.jsp',
        targetMinScript: 'scripts/build/module.ccc_view_assessment_preview.min.js',
        sources: [

            // module and configs
            'scripts/modules/modules_view/ccc_view_assessment_preview/module.js',
            'scripts/modules/modules_view/ccc_view_assessment_preview/conf/routes.conf.js',

            //services
            'scripts/modules/modules_view/ccc_view_assessment_preview/services/PreviewTaskSetService.svc.js',

            // view directive
            'scripts/modules/modules_view/ccc_view_assessment_preview/view/ccc-view-assessment-preview.dir.js',

            // directives
            'scripts/modules/modules_view/ccc_view_assessment_preview/directives/ccc-asmt-preview-complete-modal.dir.js'
        ]
    });


    /*============= MODULE : CCC.View.AssessmentPrototype ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.view.assessmentPrototype',
        targetJsp: dynamicJSPTarget + 'scripts.view.assessmentPrototype.jsp',
        targetMinScript: 'scripts/build/module.ccc_view_assessment_prototype.min.js',
        sources: [

            // module and configs
            'scripts/modules/modules_view/ccc_view_assessment_prototype/module.js',
            'scripts/modules/modules_view/ccc_view_assessment_prototype/conf/routes.conf.js',

            // services
            'scripts/modules/modules_view/ccc_view_assessment_prototype/services/PrototypeTaskSetService.svc.js',

            // view directives
            'scripts/modules/modules_view/ccc_view_assessment_prototype/view/ccc-view-assessment-prototype.dir.js',
            'scripts/modules/modules_view/ccc_view_assessment_prototype/view/ccc-view-assessment-complete.dir.js'
        ]
    });

    /*============= MODULE : CCC.AssessmentPrint ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.assessmentPrint',
        targetJsp: dynamicJSPTarget + 'scripts.assessmentPrint.jsp',
        targetMinScript: 'scripts/build/module.ccc_assessment_print.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_common/ccc_assessment_print/module.js',

            // classes
            'scripts/modules/modules_common/ccc_assessment_print/classes/BookRendererClass.cls.js',
            'scripts/modules/modules_common/ccc_assessment_print/classes/MultipleChoiceOptionsPrintClass.cls.js',
            'scripts/modules/modules_common/ccc_assessment_print/classes/interactions/ChoiceInteractionPrintClass.cls.js',

            // services
            'scripts/modules/modules_common/ccc_assessment_print/services/AssessmentSessionItemPrintService.svc.js',
            'scripts/modules/modules_common/ccc_assessment_print/services/AssessmentItemTemplateService.svc.js',
            'scripts/modules/modules_common/ccc_assessment_print/services/InteractionTemplateService.svc.js',
            'scripts/modules/modules_common/ccc_assessment_print/services/PrintCSSService.svc.js'
        ]
    });


    /*============= MODULE : CCC.View.Common ( for standard admin pages ) ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.view.common',
        targetJsp: dynamicJSPTarget + 'scripts.viewStandardCommon.jsp',
        targetMinScript: 'scripts/build/module.ccc_view_standard_common.min.js',
        sources: [

            // module and configs
            'scripts/modules/modules_common/ccc_view_standard_common/module.js',

            // shared services

            // directives
            'scripts/modules/modules_common/ccc_view_standard_common/directives/ccc-user/ccc-user.dir.js',
            'scripts/modules/modules_common/ccc_view_standard_common/directives/ccc-grouped-activations-selector/ccc-grouped-activations-selector.dir.js',
            'scripts/modules/modules_common/ccc_view_standard_common/directives/ccc-placement-course-details/ccc-placement-course-details.dir.js',
            'scripts/modules/modules_common/ccc_view_standard_common/directives/ccc-placement-report-student/ccc-placement-report-student.dir.js',
            'scripts/modules/modules_common/ccc_view_standard_common/directives/ccc-remote-event-summary/ccc-remote-event-summary.dir.js',
            'scripts/modules/modules_common/ccc_view_standard_common/directives/ccc-modal-remote-proctor-passcode/ccc-modal-remote-proctor-passcode.dir.js',
            'scripts/modules/modules_common/ccc_view_standard_common/directives/ccc-student-card-list/ccc-student-card-list.dir.js',
            'scripts/modules/modules_common/ccc_view_standard_common/directives/ccc-student-card/ccc-student-card.dir.js',
            'scripts/modules/modules_common/ccc_view_standard_common/directives/ccc-help/ccc-help.dir.js',
            'scripts/modules/modules_common/ccc_view_standard_common/directives/ccc-college-brand-list/ccc-college-brand-list.dir.js',
            'scripts/modules/modules_common/ccc_view_standard_common/directives/ccc-terms-of-use/ccc-terms-of-use.dir.js',
            'scripts/modules/modules_common/ccc_view_standard_common/directives/ccc-privacy-statement/ccc-privacy-statement.dir.js',
            'scripts/modules/modules_common/ccc_view_standard_common/directives/ccc-accessibility/ccc-accessibility.dir.js'
        ]
    });

    /*============= MODULE : CCC.View.Layout ( for standard admin pages ) ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.view.layout',
        targetJsp: dynamicJSPTarget + 'scripts.viewStandardLayout.jsp',
        targetMinScript: 'scripts/build/module.ccc_view_standard_layout.min.js',
        libraries: [
            // libraries for standard common views
            '../../resources/static/ui_lib/angular-input-masks/angular-input-masks-standalone.min.js'
        ],
        sources: [

            // module and configs
            'scripts/modules/modules_common/ccc_view_standard_layout/module.js',

            // directives
            'scripts/modules/modules_common/ccc_view_standard_layout/directives/ccc-view-common-header/ccc-view-common-header.dir.js',
            'scripts/modules/modules_common/ccc_view_standard_layout/directives/ccc-view-common-header/ccc-view-common-header-logo.dir.js',
            'scripts/modules/modules_common/ccc_view_standard_layout/directives/ccc-view-common-subnav/ccc-view-common-subnav.dir.js',
            'scripts/modules/modules_common/ccc_view_standard_layout/directives/ccc-view-common-footer/ccc-view-common-footer.dir.js',
            'scripts/modules/modules_common/ccc_view_standard_layout/directives/ccc-view-common-layout/ccc-view-common-layout.dir.js',
            'scripts/modules/modules_common/ccc_view_standard_layout/directives/ccc-view-common-not-available/ccc-view-common-not-available.dir.js',

            // services
            'scripts/modules/modules_common/ccc_view_standard_layout/services/ViewManagerWatchService.svc.js',
            'scripts/modules/modules_common/ccc_view_standard_layout/services/CommonLayoutService.svc.js'
        ]
    });


    /*============= MODULE : CCC.View.Home ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.view.home',
        targetJsp: dynamicJSPTarget + 'scripts.view.home.jsp',
        targetMinScript: 'scripts/build/module.ccc_view_standard_home.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_view/ccc_view_standard_home/module.js',
            'scripts/modules/modules_view/ccc_view_standard_home/conf/routes.conf.js',
            'scripts/modules/modules_view/ccc_view_standard_home/conf/home.conf.js',

            // view directives
            'scripts/modules/modules_view/ccc_view_standard_home/view/ccc-view-home.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/view/ccc-home-options/ccc-home-options.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/view/ccc-home-header/ccc-home-header.dir.js',

            // classes
            'scripts/modules/modules_view/ccc_view_standard_home/classes/WizardClass.cls.js',

            // shared services
            'scripts/modules/modules_view/ccc_view_standard_home/services/CurrentUserService.svc.js',
            'scripts/modules/modules_view/ccc_view_standard_home/services/AccommodationsListService.svc.js',
            'scripts/modules/modules_view/ccc_view_standard_home/services/AssessmentSessionScoringService.svc.js',
            'scripts/modules/modules_view/ccc_view_standard_home/services/LocationService.svc.js',
            'scripts/modules/modules_view/ccc_view_standard_home/services/RemoteEventService.svc.js',
            'scripts/modules/modules_view/ccc_view_standard_home/services/PrintActivationService.svc.js',
            'scripts/modules/modules_view/ccc_view_standard_home/services/SubjectAreaWizardService.svc.js',
            'scripts/modules/modules_view/ccc_view_standard_home/services/CompetencyMapsModelService.svc.js',
            'scripts/modules/modules_view/ccc_view_standard_home/services/StudentPlacementService.svc.js',
            'scripts/modules/modules_view/ccc_view_standard_home/services/RulesCollegesService.svc.js',
            'scripts/modules/modules_view/ccc_view_standard_home/services/RulesCollegesService.svc.js',
            'scripts/modules/modules_view/ccc_view_standard_home/services/StudentPlacementService.svc.js',
            'scripts/modules/modules_view/ccc_view_standard_home/services/RolesService.svc.js',

            // shared directives
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-action-list/ccc-action-list.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-action-button-list/ccc-action-button-list.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-action-button-descriptions-list/ccc-action-button-descriptions-list.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-user-badge/ccc-user-badge.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-radio-box/ccc-radio-box.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-activation-faceted-search/ccc-activation-faceted-search.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-activation-statistics/ccc-activation-statistics.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-activation/ccc-activation.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-proctor-location-summary/ccc-proctor-location-summary.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-proctor-location/ccc-proctor-location.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-activation-status-and-date-filter/ccc-activation-status-and-date-filter.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-location-card/ccc-location-card.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-locations-list/ccc-locations-list.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-colleges-dropdown/ccc-colleges-dropdown.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-colleges-list/ccc-colleges-list.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-recent-activations/ccc-recent-activations.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-student-lookup/ccc-student-lookup.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-student-results/ccc-student-results.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-student-profile/ccc-student-profile.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-modal-deactivate-activation/ccc-modal-deactivate-activation.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-modal-passcodes/ccc-modal-passcodes.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-proctor-passcodes/ccc-proctor-passcodes.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-activate-student/ccc-activate-student.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-activate-student-summary/ccc-activate-student-summary.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-assessments-list/ccc-assessments-list.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-assessments-list-read-only/ccc-assessments-list-read-only.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-accommodations-list/ccc-accommodations-list.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-assessment-scoring-setup/ccc-assessment-scoring-setup.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-assessment-scoring-results/ccc-assessment-scoring-results.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-assessment-item-score-entry/ccc-assessment-item-score-entry.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-assessment-item-score-entry/interactions/ccc-assessment-item-score-interaction-choice.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-bulk-activation-add-assessment/ccc-bulk-activation-add-assessment.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-bulk-activation-student-search/ccc-bulk-activation-student-search.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-bulk-activation-summary/ccc-bulk-activation-summary.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-user-lookup/ccc-user-lookup.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-user-faceted-search/ccc-user-faceted-search.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-role-navbar/ccc-role-navbar.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-home/ccc-home.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-manage-users/ccc-manage-users.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-browse-users/ccc-browse-users.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-user-list/ccc-user-list.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-user-edit/ccc-user-edit.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-user-edit-by-id/ccc-user-edit-by-id.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-user-college-selector/ccc-user-college-selector.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-user-form/ccc-user-form.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-user-create/ccc-user-create.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-user-create-confirm/ccc-user-create-confirm.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-user-edit-confirm/ccc-user-edit-confirm.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-college-selector/ccc-college-selector.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-college-selector-pills/ccc-college-selector-pills.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-college-selector-for-student-dropdown/ccc-college-selector-for-student-dropdown.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-college-users/ccc-college-users.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-college-test-locations/ccc-college-test-locations.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-college-test-location-users/ccc-college-test-location-users.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-user-details-form/ccc-user-details-form.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-user-login-details-form/ccc-user-login-details-form.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-user-permissions-selector/ccc-user-permissions-selector.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-user-temporary-account-selector/ccc-user-temporary-account-selector.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-modal-activation-collision/ccc-modal-activation-collision.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-manage-locations/ccc-manage-locations.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-test-location-summary/ccc-test-location-summary.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-test-location-create/ccc-test-location-create.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-test-location-edit/ccc-test-location-edit.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-test-location-form/ccc-test-location-form.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-college-selection-view/ccc-college-selection-view.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-manage-subject-areas/ccc-manage-subject-areas.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-sequence-course-list/ccc-sequence-course-list.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-subject-area-course-card/ccc-subject-area-course-card.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-subject-area-course-card/ccc-subject-area-course-card-light.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-subject-area-course-form/ccc-subject-area-course-form.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-subject-area-course-edit/ccc-subject-area-course-edit.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-subject-areas-list/ccc-subject-areas-list.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-subject-area-card/ccc-subject-area-card.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-subject-area-overview/ccc-subject-area-overview.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-subject-area-overview-published/ccc-subject-area-overview-published.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-subject-area-overview-edits/ccc-subject-area-overview-edits.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-subject-area-publish/ccc-subject-area-publish.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-subject-area-form/ccc-subject-area-form.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-cccid-search/ccc-cccid-search.dir.js',

            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-sa-wizard/ccc-sa-wizard.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-sa-wizard/ccc-sa-wzd-step-meta-data.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-sa-wizard/ccc-sa-wzd-step-show-ela-placements.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-sa-wizard/ccc-sa-wzd-step-mm-opt-in.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-sa-wizard/ccc-sa-wzd-step-mm-selection.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-sa-wizard/ccc-sa-wzd-step-mm-use-self-reported.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-sa-wizard/ccc-sa-wzd-step-mm-ela.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-sa-wizard/ccc-sa-wzd-step-mm-math.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-sa-wizard/ccc-sa-wzd-step-mm-decision-logic.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-sa-wizard/ccc-sa-wzd-step-mm-components-logic.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-sa-wizard/ccc-sa-wzd-step-no-placement.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-sa-wizard/ccc-sa-wzd-step-summary.dir.js',

            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-subject-area-edit-options/ccc-subject-area-edit-options.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-subject-area-sequence-view/ccc-subject-area-sequence-view.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-subject-area-sequence/ccc-subject-area-sequence.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-subject-area-sequence-explanation/ccc-subject-area-sequence-explanation.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-subject-area-sequence-sis-code/ccc-subject-area-sequence-sis-code.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-subject-area-course-competency-groups/ccc-subject-area-course-competency-groups.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-subject-area-course-competency-map/ccc-subject-area-course-competency-map.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-subject-area-summary/ccc-subject-area-summary-configs.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-subject-area-summary/ccc-subject-area-summary-sequences.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-subject-area-course-summary/ccc-subject-area-course-summary.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-course-competency-group-list-item/ccc-course-competency-group-list-item.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-course-competency-group-edit/ccc-course-competency-group-edit.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-course-competency-group-form/ccc-course-competency-group-form.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-course-competency-group-tree/ccc-course-competency-group-tree.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-course-competency-group-tree-summary/ccc-course-competency-group-tree-summary.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-student-placement/ccc-student-placement.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-placement-component-groups/ccc-placement-component-groups.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-placement-report-counselor/ccc-placement-report-counselor.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-placement-card/ccc-placement-card.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-placement-card-course/ccc-placement-card-course.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-activations-by-student/ccc-activations-by-student.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-activation-details-card/ccc-activation-details-card.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-district-list/ccc-district-list.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-add-student-by-id/ccc-add-student-by-id.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-remote-events/ccc-remote-events.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-remote-event-form/ccc-remote-event-form.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-remote-events-details-create/ccc-remote-events-details-create.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-remote-events-details-edit/ccc-remote-events-details-edit.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-remote-events-students/ccc-remote-events-students.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-remote-events-students-summary/ccc-remote-events-students-summary.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-remote-events-create-summary/ccc-remote-events-create-summary.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-remote-events-edit-summary/ccc-remote-events-edit-summary.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-test-event-list/ccc-test-event-list.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-activation-edit/ccc-activation-edit.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-paper-pencil-landing/ccc-paper-pencil-landing.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-paper-pencil-print/ccc-paper-pencil-print.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-class-report-setup/ccc-class-report-setup.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-paper-pencil-scoring/ccc-paper-pencil-scoring.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-wizard/ccc-wizard.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-wizard-summary/ccc-wizard-summary.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-class-report/ccc-class-report.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/directives/ccc-class-report-errors/ccc-class-report-errors.dir.js',

            // routes
            'scripts/modules/modules_view/ccc_view_standard_home/routes/home/ccc-route-home.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/routes/proctor/ccc-route-proctor.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/routes/proctor_location/ccc-route-proctor-location.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/routes/student_lookup/ccc-route-student-lookup.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/routes/paper_pencil/ccc-route-paper-pencil.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/routes/create_activation/ccc-route-create-activation.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/routes/bulk_activation/ccc-route-bulk-activation.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/routes/manage_users/ccc-route-manage-users.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/routes/manage_locations/ccc-route-manage-locations.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/routes/manage_placements/ccc-route-manage-placements.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/routes/remote_events/ccc-route-remote-events.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/routes/class_report/ccc-route-class-report.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/routes/help/ccc-route-help.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/routes/student_result/ccc-route-student-result.dir.js',

            // workflows
            'scripts/modules/modules_view/ccc_view_standard_home/workflows/ccc-workflow-student-activation/ccc-workflow-student-activation.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/workflows/ccc-workflow-proctor-location/ccc-workflow-proctor-location.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/workflows/ccc-workflow-student-lookup/ccc-workflow-student-lookup.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/workflows/ccc-workflow-paper-pencil/ccc-workflow-paper-pencil.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/workflows/ccc-workflow-bulk-activation/ccc-workflow-bulk-activation.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/workflows/ccc-workflow-home/ccc-workflow-home.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/workflows/ccc-workflow-manage-users/ccc-workflow-manage-users.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/workflows/ccc-workflow-user-search/ccc-workflow-user-search.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/workflows/ccc-workflow-create-user/ccc-workflow-create-user.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/workflows/ccc-workflow-manage-locations/ccc-workflow-manage-locations.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/workflows/ccc-workflow-manage-placements/ccc-workflow-manage-placements.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/workflows/ccc-workflow-subject-area-create/ccc-workflow-subject-area-create.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/workflows/ccc-workflow-subject-area-edit/ccc-workflow-subject-area-edit.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/workflows/ccc-workflow-subject-area-sequence/ccc-workflow-subject-area-sequence.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/workflows/ccc-workflow-subject-area-course/ccc-workflow-subject-area-course.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/workflows/ccc-workflow-student-profile/ccc-workflow-student-profile.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/workflows/ccc-workflow-remote-events/ccc-workflow-remote-events.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/workflows/ccc-workflow-remote-events-create/ccc-workflow-remote-events-create.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/workflows/ccc-workflow-remote-events-edit/ccc-workflow-remote-events-edit.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/workflows/ccc-workflow-class-report/ccc-workflow-class-report.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/workflows/ccc-workflow-assessment-scoring/ccc-workflow-assessment-scoring.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_home/workflows/ccc-workflow-help/ccc-workflow-help.dir.js'
        ]
    });


    /*============= MODULE : CCC.View.Student ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.view.student',
        targetJsp: dynamicJSPTarget + 'scripts.view.student.jsp',
        targetMinScript: 'scripts/build/module.ccc_view_standard_student.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_view/ccc_view_standard_student/module.js',
            'scripts/modules/modules_view/ccc_view_standard_student/conf/routes.conf.js',
            'scripts/modules/modules_view/ccc_view_standard_student/conf/user.conf.js',

            // shared services
            'scripts/modules/modules_view/ccc_view_standard_student/services/CurrentStudentService.svc.js',

            // view directives
            'scripts/modules/modules_view/ccc_view_standard_student/view/ccc-view-student.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_student/view/ccc-student-header/ccc-student-header.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_student/view/ccc-student-options/ccc-student-options.dir.js',

            // routes
            'scripts/modules/modules_view/ccc_view_standard_student/routes/student-home/ccc-route-student-home.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_student/routes/student-activation-status/ccc-route-student-activation-status.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_student/routes/student-help/ccc-route-student-help.dir.js',

            // shared directives
            'scripts/modules/modules_view/ccc_view_standard_student/directives/ccc-student-activations/ccc-student-activations.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_student/directives/ccc-student-authorize-activation/ccc-student-authorize-activation.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_student/directives/ccc-student-activation-status/ccc-student-activation-status.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_student/directives/ccc-student-general-instructions/ccc-student-general-instructions.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_student/directives/ccc-student-assessment-instructions/ccc-student-assessment-instructions.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_student/directives/ccc-student-start-activation-instructions/ccc-student-start-activation-instructions.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_student/directives/ccc-student-college-selection-view/ccc-student-college-selection-view.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_student/directives/ccc-student-placement-view/ccc-student-placement-view.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_student/directives/ccc-student-test-results-view/ccc-student-test-results-view.dir.js',

            // workflows
            'scripts/modules/modules_view/ccc_view_standard_student/workflows/ccc-workflow-student-dashboard/ccc-workflow-student-dashboard.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_student/workflows/ccc-workflow-student-activation-status/ccc-workflow-student-activation-status.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_student/workflows/ccc-workflow-student-help/ccc-workflow-student-help.dir.js'

        ]
    });


    /*============= MODULE : CCC.View.Dashboard ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.view.dashboard',
        targetJsp: dynamicJSPTarget + 'scripts.view.dashboard.jsp',
        targetMinScript: 'scripts/build/module.ccc_view_standard_dashboard.min.js',
        libraries: [
            '../../resources/static/ui_lib/d3/d3.min.js',
            '../../resources/static/ui_lib/c3/c3.min.js',
            '../../resources/static/ui_lib/jquery-highlight/jquery-highlight.js'
        ],
        sources: [

            // module & configs
            'scripts/modules/modules_view/ccc_view_standard_dashboard/module.js',
            'scripts/modules/modules_view/ccc_view_standard_dashboard/conf/routes.conf.js',
            'scripts/modules/modules_view/ccc_view_standard_dashboard/conf/user.conf.js',

            // shared services
            'scripts/modules/modules_view/ccc_view_standard_dashboard/services/MetricsService.svc.js',
            'scripts/modules/modules_view/ccc_view_standard_dashboard/services/MetricsGraphService.svc.js',

            // view directives
            'scripts/modules/modules_view/ccc_view_standard_dashboard/view/ccc-view-dashboard.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_dashboard/view/ccc-dashboard-header/ccc-dashboard-header.dir.js',

            // routes
            'scripts/modules/modules_view/ccc_view_standard_dashboard/routes/dashboard-home/ccc-route-dashboard-home.dir.js',

            // shared directives
            'scripts/modules/modules_view/ccc_view_standard_dashboard/directives/ccc-dashboard-main/ccc-dashboard-main.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_dashboard/directives/ccc-dashboard-metrics-health-check/ccc-dashboard-metrics-health-check.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_dashboard/directives/ccc-dashboard-metrics-versions/ccc-dashboard-metrics-versions.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_dashboard/directives/ccc-dashboard-metrics-students/ccc-dashboard-metrics-students.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_dashboard/directives/ccc-dashboard-metrics-lambdas/ccc-dashboard-metrics-lambdas.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_dashboard/directives/ccc-dashboard-metrics-rds/ccc-dashboard-metrics-rds.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_dashboard/directives/ccc-dashboard-metrics-sqs/ccc-dashboard-metrics-sqs.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_dashboard/directives/ccc-dashboard-metrics-ddb/ccc-dashboard-metrics-ddb.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_dashboard/directives/ccc-dashboard-metrics-rules/ccc-dashboard-metrics-rules.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_dashboard/directives/ccc-dashboard-metrics-rest-client-status/ccc-dashboard-metrics-rest-client-status.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_dashboard/directives/ccc-dashboard-onboard-college/ccc-dashboard-onboard-college.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_dashboard/directives/ccc-dashboard-seed-data/ccc-dashboard-seed-data.dir.js',            
            'scripts/modules/modules_view/ccc_view_standard_dashboard/directives/ccc-micro-service-properties/ccc-micro-service-properties.dir.js',
            'scripts/modules/modules_view/ccc_view_standard_dashboard/directives/ccc-micro-service-rest-client-results/ccc-micro-service-rest-client-results.dir.js',

            // workflows
            'scripts/modules/modules_view/ccc_view_standard_dashboard/workflows/ccc-workflow-dashboard-home/ccc-workflow-dashboard-home.dir.js'
        ]
    });


    /*============= MODULE : CCC.View.RemoteProctor ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.view.remoteProctor',
        targetJsp: dynamicJSPTarget + 'scripts.view.remoteProctor.jsp',
        targetMinScript: 'scripts/build/module.ccc_view_remote_proctor.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_view/ccc_view_remote_proctor/module.js',
            'scripts/modules/modules_view/ccc_view_remote_proctor/conf/routes.conf.js',

            // shared services
            'scripts/modules/modules_view/ccc_view_remote_proctor/services/RemoteProctorService.svc.js',

            // view directives
            'scripts/modules/modules_view/ccc_view_remote_proctor/view/ccc-view-remote-proctor.dir.js',
            'scripts/modules/modules_view/ccc_view_remote_proctor/view/ccc-remote-proctor-header/ccc-remote-proctor-header.dir.js',

            // routes
            'scripts/modules/modules_view/ccc_view_remote_proctor/routes/remote_proctor/ccc-route-remote-proctor.dir.js',

            // shared directives
            'scripts/modules/modules_view/ccc_view_remote_proctor/directives/ccc-remote-proctor-agreement/ccc-remote-proctor-agreement.dir.js',
            'scripts/modules/modules_view/ccc_view_remote_proctor/directives/ccc-remote-proctor-event/ccc-remote-proctor-event.dir.js',

            // workflows
            'scripts/modules/modules_view/ccc_view_remote_proctor/workflows/ccc-workflow-remote-proctor/ccc-workflow-remote-proctor.dir.js'
        ]
    });


    /*============= MODULE : CCC.View.AssessmentPrint ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.view.assessmentPrint',
        targetJsp: dynamicJSPTarget + 'scripts.view.assessmentPrint.jsp',
        targetMinScript: 'scripts/build/module.ccc_view_assessment_print.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_view/ccc_view_assessment_print/module.js',
            'scripts/modules/modules_view/ccc_view_assessment_print/conf/routes.conf.js',

            // directives
            'scripts/modules/modules_view/ccc_view_assessment_print/view/ccc-view-assessment-print.dir.js'
        ]
    });

    /*============= MODULE : CCC.View.AssessmentPrintDetached (no assessment session or student) ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.view.assessmentPrintDetached',
        targetJsp: dynamicJSPTarget + 'scripts.view.assessmentPrintDetached.jsp',
        targetMinScript: 'scripts/build/module.ccc_view_assessment_print_detached.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_view/ccc_view_assessment_print_detached/module.js',
            'scripts/modules/modules_view/ccc_view_assessment_print_detached/conf/routes.conf.js',

            // directives
            'scripts/modules/modules_view/ccc_view_assessment_print_detached/view/ccc-view-assessment-print-detached.dir.js'
        ]
    });

    /*============= MODULE : CCC.Api.Accommodations ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.accommodations',
        targetJsp: dynamicJSPTarget + 'scripts.api.accommodations.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_accommodations.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_accommodations/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_accommodations/services/AccommodationsAPIService.svc.js'
        ]
    });

    /*============= MODULE : CCC.Api.Activations ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.activations',
        targetJsp: dynamicJSPTarget + 'scripts.api.activations.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_activations.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_activations/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_activations/services/ActivationsAPIService.svc.js',
            'scripts/modules/modules_api/ccc_api_activations/services/AssessmentLaunchService.svc.js',

            // classes
            'scripts/modules/modules_api/ccc_api_activations/classes/ActivationClass.cls.js'
        ]
    });

    /*============= MODULE : CCC.Api.ActivationSearch ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.activationSearch',
        targetJsp: dynamicJSPTarget + 'scripts.api.activationSearch.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_activation_search.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_activation_search/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_activation_search/services/ActivationSearchAPIService.svc.js'
        ]
    });

    /*============= MODULE : CCC.Api.Activity ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.activity',
        targetJsp: dynamicJSPTarget + 'scripts.api.activity.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_activity.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_activity/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_activity/services/ActivityAPIService.svc.js'
        ]
    });

    /*============= MODULE : CCC.Api.AssessmentMetadata ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.assessmentMetadata',
        targetJsp: dynamicJSPTarget + 'scripts.api.assessmentMetadata.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_assessment_metadata.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_assessment_metadata/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_assessment_metadata/services/AssessmentMetadataAPIService.svc.js'
        ]
    });

    /*============= MODULE : CCC.Api.Assessments ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.assessments',
        targetJsp: dynamicJSPTarget + 'scripts.api.assessments.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_assessments.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_assessments/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_assessments/services/AssessmentsAPIService.svc.js'
        ]
    });

    /*============= MODULE : CCC.Api.AssessmentSessions ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.assessmentSessions',
        targetJsp: dynamicJSPTarget + 'scripts.api.assessmentSessions.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_assessment_sessions.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_assessment_sessions/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_assessment_sessions/services/AssessmentSessionsAPIService.svc.js'
        ]
    });

    /*============= MODULE : CCC.Api.AssessmentSessionsPreview ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.assessmentSessionsPreview',
        targetJsp: dynamicJSPTarget + 'scripts.api.assessmentSessionsPreview.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_assessment_sessions_preview.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_assessment_sessions_preview/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_assessment_sessions_preview/services/AssessmentSessionsPreviewAPIService.svc.js'
        ]
    });

    /*============= MODULE : CCC.Api.BatchActivation ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.batchActivations',
        targetJsp: dynamicJSPTarget + 'scripts.api.batchActivations.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_batch_activations.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_batch_activations/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_batch_activations/services/BatchActivationsAPIService.svc.js'
        ]
    });

    /*============= MODULE : CCC.Api.FakeData ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.fakeData',
        targetJsp: dynamicJSPTarget + 'scripts.api.fakeData.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_fake_data.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_fake_data/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_fake_data/services/FakeData.svc.js'
        ]
    });

    /*============= MODULE : CCC.API.Districts ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.districts',
        targetJsp: dynamicJSPTarget + 'scripts.api.districts.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_districts.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_districts/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_districts/services/DistrictsAPIService.svc.js'
        ]
    });
    
    /*============= MODULE : CCC.API.Dashboard ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.dashboard',
        targetJsp: dynamicJSPTarget + 'scripts.api.dashboard.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_dashboard.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_dashboard/module.js',
            // shared services
            'scripts/modules/modules_api/ccc_api_dashboard/services/DashboardAPIService.svc.js',

        ]
    });
    
    /*============= MODULE : CCC.API.Dashboard ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.onboard.college',
        targetJsp: dynamicJSPTarget + 'scripts.api.onboard.college.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_onboard_college.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_onboard_college/module.js',
            // shared services
            'scripts/modules/modules_api/ccc_api_onboard_college/services/OnboardCollegeAPIService.svc.js',

        ]
    });
    

    /*============= MODULE : CCC.API.Colleges ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.colleges',
        targetJsp: dynamicJSPTarget + 'scripts.api.colleges.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_colleges.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_colleges/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_colleges/services/CollegesAPIService.svc.js'
        ]
    });

    /*============= MODULE : CCC.API.CollegeAttributes ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.collegeAttributes',
        targetJsp: dynamicJSPTarget + 'scripts.api.collegeAttributes.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_college_attributes.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_college_attributes/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_college_attributes/services/CollegeAttributesAPIService.svc.js'
        ]
    });

    /*============= MODULE : CCC.Api.Passcodes ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.passcodes',
        targetJsp: dynamicJSPTarget + 'scripts.api.passcodes.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_passcodes.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_passcodes/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_passcodes/services/PasscodesAPIService.svc.js'
        ]
    });

    /*============= MODULE : CCC.Api.Students ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.students',
        targetJsp: dynamicJSPTarget + 'scripts.api.students.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_students.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_students/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_students/services/StudentsAPIService.svc.js',

            // classes
            'scripts/modules/modules_api/ccc_api_students/classes/StudentClass.cls.js'
        ]
    });

    /*============= MODULE : CCC.Api.Users ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.users',
        targetJsp: dynamicJSPTarget + 'scripts.api.users.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_users.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_users/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_users/services/UsersAPIService.svc.js',

            // classes
            'scripts/modules/modules_api/ccc_api_users/classes/UserClass.cls.js'
        ]
    });

    /*============= MODULE : CCC.API.Roles ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.roles',
        targetJsp: dynamicJSPTarget + 'scripts.api.roles.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_roles.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_roles/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_roles/services/RolesAPIService.svc.js'
        ]
    });

    /*============= MODULE : CCC.API.SubjectAreas ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.subjectAreas',
        targetJsp: dynamicJSPTarget + 'scripts.api.disciplines.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_subject_areas.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_subject_areas/module.js',

            // classes
            'scripts/modules/modules_api/ccc_api_subject_areas/classes/SubjectAreaClass.cls.js',
            'scripts/modules/modules_api/ccc_api_subject_areas/classes/SubjectAreaSequenceClass.cls.js',
            'scripts/modules/modules_api/ccc_api_subject_areas/classes/SubjectAreaCourseClass.cls.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_subject_areas/services/SubjectAreasAPIService.svc.js'
        ]
    });

    /*============= MODULE : CCC.API.MMSubjectAreas ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.mmSubjectAreas',
        targetJsp: dynamicJSPTarget + 'scripts.api.mmsubjectareas.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_mm_subject_areas.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_mm_subject_areas/module.js',

            // classes
            'scripts/modules/modules_api/ccc_api_mm_subject_areas/classes/MMSubjectAreaClass.cls.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_mm_subject_areas/services/MMSubjectAreasAPIService.svc.js'
        ]
    });

    /*============= MODULE : CCC.API.CompetencyMapSubjectArea ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.competencymapsubjectarea',
        targetJsp: dynamicJSPTarget + 'scripts.api.competencymapdiscipline.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_competencymapdiscipline.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_competency_map_subject_area/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_competency_map_subject_area/services/CompetencyMapSubjectAreaAPIService.svc.js'
        ]
    });

    /*============= MODULE : CCC.API.CompetencyMaps ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.competencymaps',
        targetJsp: dynamicJSPTarget + 'scripts.api.competencymaps.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_competencymaps.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_competency_maps/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_competency_maps/services/CompetencyMapsAPIService.svc.js'
        ]
    });

    /*============= MODULE : CCC.API.Courses ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.courses',
        targetJsp: dynamicJSPTarget + 'scripts.api.courses.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_courses.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_courses/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_courses/services/CoursesAPIService.svc.js'
        ]
    });

    /*============= MODULE : CCC.API.CompetencyGroups ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.competencygroups',
        targetJsp: dynamicJSPTarget + 'scripts.api.competencygroups.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_competencygroups.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_competency_groups/module.js',

            // classes
            'scripts/modules/modules_api/ccc_api_competency_groups/classes/CompetencyGroupClass.cls.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_competency_groups/services/CompetencyGroupsAPIService.svc.js'
        ]
    });

    /*============= MODULE : CCC.API.TestLocations ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.testlocations',
        targetJsp: dynamicJSPTarget + 'scripts.api.testlocations.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_testlocations.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_test_locations/module.js',

            // classes
            'scripts/modules/modules_api/ccc_api_test_locations/classes/TestLocationClass.cls.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_test_locations/services/TestLocationsAPIService.svc.js'
        ]
    });

    /*============= MODULE : CCC.API.CollegeSubjectAreas ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.collegesubjectareas',
        targetJsp: dynamicJSPTarget + 'scripts.api.collegedisciplines.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_collegedisciplines.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_college_subject_areas/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_college_subject_areas/services/CollegeSubjectAreasAPIService.svc.js'
        ]
    });

    /*============= MODULE : CCC.API.Placement ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.placement',
        targetJsp: dynamicJSPTarget + 'scripts.api.placement.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_placement.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_placement/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_placement/services/PlacementAPIService.svc.js'
        ]
    });

    /*============= MODULE : CCC.API.PlacementColleges ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.placementcolleges',
        targetJsp: dynamicJSPTarget + 'scripts.api.placementcolleges.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_placement_colleges.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_placement_colleges/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_placement_colleges/services/PlacementCollegesAPIService.svc.js'
        ]
    });

    /*============= MODULE : CCC.API.Events ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.events',
        targetJsp: dynamicJSPTarget + 'scripts.api.events.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_events.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_events/module.js',

            // classes
            'scripts/modules/modules_api/ccc_api_events/classes/EventClass.cls.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_events/services/EventsAPIService.svc.js'
        ]
    });

    /*============= MODULE : CCC.API.RemoteProctor ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.remoteproctor',
        targetJsp: dynamicJSPTarget + 'scripts.api.remoteproctor.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_remoteproctor.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_remote_proctor/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_remote_proctor/services/RemoteProctorAPIService.svc.js'
        ]
    });

    /*============= MODULE : CCC.API.CompetencyDisciplines ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.competencydisciplines',
        targetJsp: dynamicJSPTarget + 'scripts.api.competencydisciplines.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_competencydisciplines.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_competency_disciplines/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_competency_disciplines/services/CompetencyDisciplinesAPIService.svc.js'
        ]
    });

    /*============= MODULE : CCC.API.ClassReport ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.classreport',
        targetJsp: dynamicJSPTarget + 'scripts.api.classreport.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_classreport.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_class_report/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_class_report/services/ClassReportAPIService.svc.js'
        ]
    });

    /*============= MODULE : CCC.API.Rules ============*/

    javascriptConfigs.groups.push({
        id: 'ccc.api.rulescolleges',
        targetJsp: dynamicJSPTarget + 'scripts.api.rulescolleges.jsp',
        targetMinScript: 'scripts/build/module.ccc_api_rules_colleges.min.js',
        sources: [

            // module & configs
            'scripts/modules/modules_api/ccc_api_rules_colleges/module.js',

            // shared services
            'scripts/modules/modules_api/ccc_api_rules_colleges/services/RulesCollegesAPIService.svc.js'
        ]
    });


    return javascriptConfigs;
};
