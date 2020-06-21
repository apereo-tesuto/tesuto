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

    angular.module('CCC.View.Home').directive('cccSaWzdStepMmDecisionLogic', function () {

        return {

            restrict: 'E',

            scope: {
                immutableData: '=',
                model: '=',
                onReady: '=',
                setValueMapper: '='
            },

            controller: [

                '$scope',
                '$element',
                'RulesCollegesService',
                'UtilsService',

                function ($scope, $element, RulesCollegesService, UtilsService) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    $scope.loading = true;

                    $scope.submitted = false;

                    $scope.mmOptions = [];


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var setSelectedOption = function () {

                        var selectedItemFound = false;

                        _.each($scope.mmOptions, function (option) {
                            option.selected = $scope.model.competencyAttributes.mmDecisionLogic === option.value;
                            selectedItemFound = selectedItemFound || option.selected;
                        });

                        if (!selectedItemFound) {
                            $scope.model.competencyAttributes.mmDecisionLogic = null;
                        }
                    };

                    var setValueMapper = function () {

                        $scope.setValueMapper('competencyAttributes.mmDecisionLogic', function (value) {

                            var selectedOption = _.find($scope.mmOptions, function (option) {
                                return option.value === value;
                            });

                            if (selectedOption) {
                                return selectedOption.label;
                            } else {
                                return value;
                            }
                        });
                    };

                    var getOptions = function () {

                        $scope.loading = true;

                        var collegeMisCode = $scope.model.collegeId;
                        var competency = $scope.model.competencyAttributes.competencyCode;

                        return RulesCollegesService.getRulesByCategory(collegeMisCode, competency, 'mmComponentPlacementLogic').then(function (results) {

                            $scope.mmOptions = _.map(results, function (result) {
                                return {
                                    value: result.id,
                                    label: result.title,
                                    help: result.description
                                };
                            });

                        }).finally(function () {
                            $scope.loading = false;
                        });
                    };

                    var initialize = function () {

                        getOptions().then(setSelectedOption);

                        // even though the options are async, as soon as the form is valid they can move forward so set the value mapper now in case the user can skip before the options call completes
                        setValueMapper();

                        // fire off callback that this view is ready and return the isValid function that says that this step is complete and the next step can be loaded
                        $scope.onReady(function (immutableData, model) {
                            return $scope.mmForm.$valid;
                        });
                    };


                    /*============= BEHAVIOR =============*/


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-wizard.stepSubmitted', function () {
                        $scope.submitted = true;
                        UtilsService.focusOnFirstInvalidInput($element);
                    });

                    $scope.$on('ccc-detailed-select.changed', function (event, selectId, selectedOptions, options) {
                        $scope.model.competencyAttributes.mmDecisionLogic = selectedOptions[0].value;
                        $scope.mmForm.mmDecisionLogic.$setDirty();
                    });


                    /*============ INITIALIZATION ==============*/

                    initialize();
                }
            ],

            template: [

                '<h3 translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-MM-DECISION-LOGIC.TITLE" class="form-title"></h3>',

                '<p class="help-block" translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-MM-DECISION-LOGIC.HELP_BLOCK"></p>',

                '<div class="ccc-form-container margin-bottom-md margin-top-xs" ng-form name="mmForm">',

                    '<fieldset class="margin-bottom-md" ng-class="{\'ccc-show-errors\': submitted || mmForm.mmDecisionLogic.$dirty}">',

                        '<legend class="sr-only" id="mmSelectionLabel" translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-MM-DECISION-LOGIC.TITLE"></legend>',

                        '<ccc-content-loading-placeholder ng-if="loading || !mmOptions.length" no-results="mmOptions.length === 0 && !loading"></ccc-content-loading-placeholder>',
                        '<ccc-detailed-select ng-if="!loading && mmOptions.length" options="mmOptions" multi-select="false" is-required="true" described-by="mmErrors" is-invalid="mmForm.mmDecisionLogic.$invalid"></ccc-detailed-select>',

                        '<input class="hidden" name="mmDecisionLogic" required ng-model="model.competencyAttributes.mmDecisionLogic" ng-model-options="{allowInvalid: true}" autocomplete="off"/>',
                        '<div id="mmErrors" class="ccc-validation-messages ccc-validation-messages-standalone noanim" ng-messages="mmForm.mmDecisionLogic.$error">',
                            '<p ng-message="required" class="noanim"><i class="fa fa-exclamation-triangle color-warning"></i> <span translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-SHOW-ELA-PLACEMENTS.REQUIRED"></span></p>',
                        '</div>',

                    '</fieldset>',

                '</div>'

            ].join('')

        };

    });

})();
