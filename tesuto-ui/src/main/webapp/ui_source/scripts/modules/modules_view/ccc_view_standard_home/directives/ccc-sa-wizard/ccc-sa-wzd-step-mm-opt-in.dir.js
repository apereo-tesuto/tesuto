/*-------------------------------------------------------------------------------
# Copyright © 2019 by California Community Colleges Chancellor's Office
# 
# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License.  You may obtain a copy
# of the License at
# 
#   http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
# License for the specific language governing permissions and limitations under
# the License.
#------------------------------------------------------------------------------*/
(function () {

    angular.module('CCC.View.Home').directive('cccSaWzdStepMmOptIn', function () {

        return {

            restrict: 'E',

            scope: {
                immutableData: '=',
                model: '=',
                onReady: '='
            },

            controller: [

                '$scope',
                '$element',
                'UtilsService',

                function ($scope, $element, UtilsService) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    $scope.submitted = false;

                    $scope.showMultipleMeasuresHelp = false;
                    $scope.showMultipleMeasuresDisjunctiveHelp = false;

                    $scope.showMMOptions = [
                        {
                            value: true,
                            label: 'Yes',
                            selected: $scope.model.competencyAttributes.optInMultiMeasure === true,
                            helpHeader: 'Students will see a placement within Tesuto when data is available'
                        },
                        {
                            value: false,
                            label: 'No',
                            selected: $scope.model.competencyAttributes.optInMultiMeasure === false,
                            type: 'NO',
                            helpHeader: 'Students will not see a placement within Tesuto',
                            help: 'Your college will apply multiple measures and assign a placement outside of Tesuto.'
                        }
                    ];


                    /*============ MODEL DEPENDENT METHODS ============*/


                    /*============= BEHAVIOR =============*/

                    $scope.toggleHelpText = function () {
                        $scope.showMultipleMeasuresHelp = !$scope.showMultipleMeasuresHelp;
                    };
                    $scope.toggleDisjunctiveHelpText = function () {
                        $scope.showMultipleMeasuresDisjunctiveHelp = !$scope.showMultipleMeasuresDisjunctiveHelp;
                    };


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-wizard.stepSubmitted', function () {
                        $scope.submitted = true;
                        UtilsService.focusOnFirstInvalidInput($element);
                    });

                    $scope.$on('ccc-detailed-select.changed', function (event, selectId, selectedOptions, options) {
                        $scope.model.competencyAttributes.optInMultiMeasure = selectedOptions[0].value;
                        $scope.useMMForm.optInMultiMeasure.$setDirty();
                    });



                    /*============ INITIALIZATION ==============*/

                    // fire off callback that this view is ready and return the isValid function that says that this step is complete and the next step can be loaded
                    $scope.onReady(function (immutableData, model) {
                        return $scope.useMMForm.$valid;
                    });
                }
            ],

            template: [

                '<h3 translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-MM-OPT-IN.TITLE" class="form-title"></h3>',

                '<ccc-label-required></ccc-label-required>',

                '<p class="help-block" translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-MM-OPT-IN.HELP_BLOCK"></p>',

                '<div class="mm-explanation">',

                    '<div>',
                        '<a class="" href="#" ng-click="toggleHelpText()">',
                            'Multiple measures are required for placements within Tesuto',
                            ' <span class="icon fa fa-question-circle" role="presentation" aria-hidden="true"></span>',
                        '</a>',
                    '</div>',
                    '<div ng-if="showMultipleMeasuresHelp" class="user-account-id-help-text help-tip-box margin-bottom-sm margin-top-xs" aria-hidden="false">',
                        '<a ng-click="toggleHelpText()" href="#"><i class="fa fa-times close-help-text" aria-hidden="true"></i>',
                            '<span class="sr-only">close help text</span>',
                        '</a>',
                        '<p translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-MM-OPT-IN.HELP_MM_REQUIRED"></p>',
                    '</div>',

                    '<div>',
                        '<a class="" href="#" ng-click="toggleDisjunctiveHelpText()">',
                            'Multiple measures are applied disjunctively',
                            ' <span class="icon fa fa-question-circle" role="presentation" aria-hidden="true"></span>',
                        '</a>',
                    '</div>',
                    '<div ng-if="showMultipleMeasuresDisjunctiveHelp" class="user-account-id-help-text help-tip-box margin-bottom-sm margin-top-xs" aria-hidden="false">',
                        '<a ng-click="toggleDisjunctiveHelpText()" href="#"><i class="fa fa-times close-help-text" aria-hidden="true"></i>',
                            '<span class="sr-only">close help text</span>',
                        '</a>',
                        '<p translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-MM-OPT-IN.HELP_MM_DISJUNCTIVE"></p>',
                    '</div>',

                '</div>',

                '<div class="ccc-form-container margin-bottom-md margin-top-md" ng-form name="useMMForm">',

                    '<fieldset class="margin-bottom-md" ng-class="{\'ccc-show-errors\': submitted || useMMForm.optInMultiMeasure.$dirty}">',

                        '<legend class="mimic-label margin-top-sm" id="useMMLabel"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> Use multiple measures to place students in this Subject Area?</legend>',
                        '<ccc-detailed-select options="showMMOptions" multi-select="false" is-required="true" id="identifyAsEnglishOptions" described-by="useMMErrors" is-invalid="useMMForm.optInMultiMeasure.$invalid"></ccc-detailed-select>',

                        '<input class="hidden" name="optInMultiMeasure" required ng-model="model.competencyAttributes.optInMultiMeasure" ng-model-options="{allowInvalid: true}" autocomplete="off"/>',
                        '<div id="useMMErrors" class="ccc-validation-messages ccc-validation-messages-standalone noanim" ng-messages="useMMForm.optInMultiMeasure.$error">',
                            '<p ng-message="required" class="noanim"><i class="fa fa-exclamation-triangle color-warning"></i> <span translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-SHOW-ELA-PLACEMENTS.REQUIRED"></span></p>',
                        '</div>',

                    '</fieldset>',

                '</div>'

            ].join('')

        };

    });

})();
