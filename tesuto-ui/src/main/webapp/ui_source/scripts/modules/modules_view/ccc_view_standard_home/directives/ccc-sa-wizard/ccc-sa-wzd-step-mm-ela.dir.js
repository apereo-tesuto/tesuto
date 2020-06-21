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

    angular.module('CCC.View.Home').directive('cccSaWzdStepMmEla', function () {

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

                    $scope.highestReadingOptions = [
                        {
                            value: 'integrated',
                            label: 'Reading is integrated with ELA'
                        },
                        {
                            value: '0',
                            label: 'Y -Transfer'
                        },
                        {
                            value: '1',
                            label: 'A - 1 level below'
                        },
                        {
                            value: '2',
                            label: 'B - 2 levels below'
                        },
                        {
                            value: '3',
                            label: 'C - 3 levels below'
                        },
                        {
                            value: '4',
                            label: 'D - 4 levels below'
                        },
                        {
                            value: '5',
                            label: 'E - 5 levels below'
                        },
                        {
                            value: '6',
                            label: 'F - 6 levels below'
                        },
                        {
                            value: '7',
                            label: 'G - 7 levels below'
                        },
                        {
                            value: '8',
                            label: 'H - 8 levels below'
                        }
                    ];


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var setValueMapper = function () {

                        $scope.setValueMapper('competencyAttributes.highestLevelReadingCourse', function (value) {

                            var selectedReadingOption = _.find($scope.highestReadingOptions, function (readingOption) {
                                return readingOption.value === value;
                            });

                            if (selectedReadingOption) {
                                return selectedReadingOption.label;
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
                        $scope.model.competencyAttributes.highestLevelReadingCourse = itemId;
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

                '<h3 translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-MM-ELA.TITLE" class="form-title"></h3>',

                '<ccc-label-required></ccc-label-required>',

                '<div class="ccc-form-container margin-bottom-md margin-top-md" ng-form name="mmForm">',

                    '<fieldset class="margin-bottom-md" ng-class="{\'ccc-show-errors\': submitted || mmForm.elaHighestLevelDropdown.$dirty}">',

                        '<legend class="mimic-label margin-top-sm" id="highestLevelReadingCourseLabel"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-MM-ELA.HIGHEST_READING_LABEL"></span></legend>',

                        '<ccc-item-dropdown ',
                            'id="elaHighestLevelDropdown" ',
                            'class="btn-full-width" ',
                            'is-required="true" ',
                            'required-error-msg="CCC_VIEW_HOME.CCC-SA-WZD-STEP-SHOW-ELA-PLACEMENTS.REQUIRED" ',
                            'initial-items="highestReadingOptions" ',
                            'initial-item-id="model.competencyAttributes.highestLevelReadingCourse" ',
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
