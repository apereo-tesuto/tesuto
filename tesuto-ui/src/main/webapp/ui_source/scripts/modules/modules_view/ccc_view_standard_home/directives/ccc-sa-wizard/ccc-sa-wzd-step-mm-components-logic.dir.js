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

    angular.module('CCC.View.Home').directive('cccSaWzdStepMmComponentsLogic', function () {

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
                '$q',
                'UtilsService',
                'RulesCollegesService',

                function ($scope, $element, $q, UtilsService, RulesCollegesService) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    $scope.submitted = false;

                    $scope.loading = true;

                    $scope.mmOptionsPlacement = [];
                    $scope.mmOptionsAssignment = [];


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var setSelectedOption = function () {

                        var selectedPlacementLogicFound = false;

                        // set the initial selected placement option
                        _.each($scope.mmOptionsPlacement, function (option) {
                            option.selected = $scope.model.competencyAttributes.mmPlacementLogic === option.value;
                            selectedPlacementLogicFound = selectedPlacementLogicFound || option.selected;
                        });

                        if (!selectedPlacementLogicFound) {
                            $scope.model.competencyAttributes.mmPlacementLogic = null;
                        }


                        var selectedAssignedPlacementLogicFound = false;

                        // set the initial selected assignment option
                        _.each($scope.mmOptionsAssignment, function (option) {
                            option.selected = $scope.model.competencyAttributes.mmAssignedPlacementLogic === option.value;
                            selectedAssignedPlacementLogicFound = selectedAssignedPlacementLogicFound || option.selected;
                        });

                        if (!selectedAssignedPlacementLogicFound) {
                            $scope.model.competencyAttributes.mmAssignedPlacementLogic = null;
                        }
                    };

                    var setValueMapper = function () {

                        $scope.setValueMapper('competencyAttributes.mmPlacementLogic', function (value) {

                            var selectedOption = _.find($scope.mmOptionsPlacement, function (option) {
                                return option.value === value;
                            });

                            if (selectedOption) {
                                return selectedOption.label;
                            } else {
                                return value;
                            }
                        });

                        $scope.setValueMapper('competencyAttributes.mmAssignedPlacementLogic', function (value) {

                            var selectedOption = _.find($scope.mmOptionsAssignment, function (option) {
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

                        var promises = [];

                        promises.push(
                            RulesCollegesService.getRulesByCategory(collegeMisCode, competency, 'mmPlacementLogic').then(function (results) {

                                $scope.mmOptionsPlacement = _.map(results, function (result) {
                                    return {
                                        value: result.id,
                                        label: result.title,
                                        help: result.description
                                    };
                                });
                            })
                        );

                        promises.push(
                            RulesCollegesService.getRulesByCategory(collegeMisCode, competency, 'mmAssignedPlacementLogic').then(function (results) {

                                $scope.mmOptionsAssignment = _.map(results, function (result) {
                                    return {
                                        value: result.id,
                                        label: result.title,
                                        help: result.description
                                    };
                                });

                            })
                        );

                        return $q.all(promises).finally(function () {
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

                        if (selectId === 'mmPlacementLogic') {

                            $scope.model.competencyAttributes.mmPlacementLogic = selectedOptions[0].value;
                            $scope.mmForm.mmPlacementLogic.$setDirty();

                        } else if (selectId === 'mmAssignedPlacementLogic') {

                            $scope.model.competencyAttributes.mmAssignedPlacementLogic = selectedOptions[0].value;
                            $scope.mmForm.mmAssignedPlacementLogic.$setDirty();

                        }
                    });


                    /*============ INITIALIZATION ==============*/

                    initialize();
                }
            ],

            template: [

                '<h3 translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-MM-COMPONENTS-LOGIC.TITLE_PLACEMENT" class="form-title"></h3>',

                '<p class="help-block" translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-MM-COMPONENTS-LOGIC.HELP_BLOCK_PLACEMENT"></p>',

                '<div ng-form name="mmForm">',

                    '<div class="ccc-form-container margin-bottom-md margin-top-xs">',

                        '<fieldset class="margin-bottom-md" ng-class="{\'ccc-show-errors\': submitted || mmForm.mmPlacementLogic.$dirty}">',

                            '<legend class="sr-only" id="mmPlacementLogicLabel" translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-MM-COMPONENTS-LOGIC.TITLE_PLACEMENT"></legend>',

                            '<ccc-content-loading-placeholder ng-if="loading || !mmOptionsPlacement.length" no-results="mmOptionsPlacement.length === 0 && !loading"></ccc-content-loading-placeholder>',
                            '<ccc-detailed-select ng-if="!loading && mmOptionsPlacement.length" id="mmPlacementLogic" options="mmOptionsPlacement" multi-select="false" is-required="true" described-by="mmErrors" is-invalid="mmForm.mmPlacementLogic.$invalid"></ccc-detailed-select>',

                            '<input class="hidden" name="mmPlacementLogic" required ng-model="model.competencyAttributes.mmPlacementLogic" ng-model-options="{allowInvalid: true}" autocomplete="off"/>',
                            '<div id="mmErrors" class="ccc-validation-messages ccc-validation-messages-standalone noanim" ng-messages="mmForm.mmPlacementLogic.$error">',
                                '<p ng-message="required" class="noanim"><i class="fa fa-exclamation-triangle color-warning"></i> <span translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-SHOW-ELA-PLACEMENTS.REQUIRED"></span></p>',
                            '</div>',

                        '</fieldset>',

                    '</div>',

                    '<h3 translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-MM-COMPONENTS-LOGIC.TITLE_ASSIGNMENT" class="form-title"></h3>',

                    '<p class="help-block" translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-MM-COMPONENTS-LOGIC.HELP_BLOCK_ASSIGNMENT"></p>',

                    '<div class="ccc-form-container margin-bottom-md margin-top-xs">',

                        '<fieldset class="margin-bottom-md" ng-class="{\'ccc-show-errors\': submitted || mmForm.mmAssignedPlacementLogic.$dirty}">',

                            '<legend class="sr-only" id="mmAssignedPlacementLogicLabel" translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-MM-COMPONENTS-LOGIC.TITLE_ASSIGNMENT"></legend>',

                            '<ccc-content-loading-placeholder ng-if="loading || !mmOptionsAssignment.length" no-results="mmOptionsAssignment.length === 0 && !loading"></ccc-content-loading-placeholder>',
                            '<ccc-detailed-select ng-if="!loading && mmOptionsAssignment.length" id="mmAssignedPlacementLogic" options="mmOptionsAssignment" multi-select="false" is-required="true" described-by="mmErrorsAssignment" is-invalid="mmForm.mmAssignedPlacementLogic.$invalid"></ccc-detailed-select>',

                            '<input class="hidden" name="mmAssignedPlacementLogic" required ng-model="model.competencyAttributes.mmAssignedPlacementLogic" ng-model-options="{allowInvalid: true}" autocomplete="off"/>',
                            '<div id="mmErrorsAssignment" class="ccc-validation-messages ccc-validation-messages-standalone noanim" ng-messages="mmForm.mmAssignedPlacementLogic.$error">',
                                '<p ng-message="required" class="noanim"><i class="fa fa-exclamation-triangle color-warning"></i> <span translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-SHOW-ELA-PLACEMENTS.REQUIRED"></span></p>',
                            '</div>',

                        '</fieldset>',

                    '</div>',

                '</div>'

            ].join('')

        };

    });

})();
