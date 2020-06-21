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

    angular.module('CCC.View.Home').directive('cccSaWzdStepMmMath', function () {

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
                'UtilsService',

                function ($scope, $element, UtilsService) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    $scope.submitted = false;

                    $scope.mathPrereqOptions = [
                        {
                            value: 'none',
                            label: 'None'
                        },
                        {
                            value: 'Elementary Algebra',
                            label: 'Elementary Algebra'
                        },
                        {
                            value: 'Intermediate Algebra',
                            label: 'Intermediate Algebra'
                        }
                    ];

                    $scope.statisticsPrereqOptions = [
                        {
                            value: 'none',
                            label: 'None'
                        },
                        {
                            value: 'Elementary Algebra',
                            label: 'Elementary Algebra'
                        },
                        {
                            value: 'Intermediate Algebra',
                            label: 'Intermediate Algebra'
                        }
                    ];


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var setValueMapper = function () {

                        $scope.setValueMapper('competencyAttributes.prerequisiteGeneralEducation', function (value) {

                            var selectedOption = _.find($scope.mathPrereqOptions, function (option) {
                                return option.value === value;
                            });

                            if (selectedOption) {
                                return selectedOption.label;
                            } else {
                                return value;
                            }
                        });

                        $scope.setValueMapper('competencyAttributes.prerequisiteStatistics', function (value) {

                            var selectedOption = _.find($scope.statisticsPrereqOptions, function (option) {
                                return option.value === value;
                            });

                            if (selectedOption) {
                                return selectedOption.label;
                            } else {
                                return value;
                            }
                        });
                    };


                    /*============= BEHAVIOR =============*/

                    $scope.getOptionId = function (option) {
                        return option.value;
                    };
                    $scope.getOptionName = function (option) {
                        return option.label;
                    };


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-wizard.stepSubmitted', function () {
                        $scope.submitted = true;
                        UtilsService.focusOnFirstInvalidInput($element);
                    });

                    $scope.$on('ccc-item-dropdown.itemSelected', function (e, dropdownId, itemId, item) {

                        if (dropdownId === 'prerequisiteGeneralEducationSelectDropdown') {

                            $scope.model.competencyAttributes.prerequisiteGeneralEducation = itemId;

                        } else if (dropdownId === 'prerequisiteStatisticsSelectDropdown') {

                            $scope.model.competencyAttributes.prerequisiteStatistics = itemId;
                        }
                    });


                    /*============ INITIALIZATION ==============*/

                    setValueMapper();

                    // fire off callback that this view is ready and return the isValid function that says that this step is complete and the next step can be loaded
                    $scope.onReady(function (immutableData, model) {
                        return $scope.mmForm.$valid;
                    });
                }
            ],

            template: [

                '<h3 translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-MM-MATH.TITLE" class="form-title"></h3>',

                '<ccc-label-required></ccc-label-required>',

                '<div class="ccc-form-container margin-top-md" ng-form name="mmForm">',

                    '<fieldset class="margin-bottom-md" ng-class="{\'ccc-show-errors\': submitted || mmForm.prerequisiteGeneralEducationSelectDropdown.$dirty}">',

                        '<legend class="mimic-label margin-top-sm" id="prerequisiteGeneralEducationLabel"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-MM-MATH.GENERAL_MATH_LABEL"></span></legend>',

                        '<ccc-item-dropdown ',
                            'id="prerequisiteGeneralEducationSelectDropdown" ',
                            'class="btn-full-width" ',
                            'is-required="true" ',
                            'required-error-msg="CCC_VIEW_HOME.CCC-SA-WZD-STEP-SHOW-ELA-PLACEMENTS.REQUIRED" ',
                            'initial-items="mathPrereqOptions" ',
                            'initial-item-id="model.competencyAttributes.prerequisiteGeneralEducation" ',
                            'loading="false" ',
                            'get-item-id="getOptionId" ',
                            'get-item-name="getOptionName" ',
                        '></ccc-item-dropdown>',

                    '</fieldset>',

                    '<fieldset class="margin-bottom-md" ng-class="{\'ccc-show-errors\': submitted || mmForm.prerequisiteStatisticsSelectDropdown.$dirty}">',

                        '<legend class="mimic-label margin-top-sm" id="generalMathPrerequisite"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-MM-MATH.STATS_MATH_LABEL"></span></legend>',

                        '<ccc-item-dropdown ',
                            'id="prerequisiteStatisticsSelectDropdown" ',
                            'class="btn-full-width" ',
                            'is-required="true" ',
                            'required-error-msg="CCC_VIEW_HOME.CCC-SA-WZD-STEP-SHOW-ELA-PLACEMENTS.REQUIRED" ',
                            'initial-items="statisticsPrereqOptions" ',
                            'initial-item-id="model.competencyAttributes.prerequisiteStatistics" ',
                            'loading="false" ',
                            'get-item-id="getOptionId" ',
                            'get-item-name="getOptionName" ',
                        '></ccc-item-dropdown>',

                    '</fieldset>',

                '</div>'

            ].join('')

        };

    });

})();
