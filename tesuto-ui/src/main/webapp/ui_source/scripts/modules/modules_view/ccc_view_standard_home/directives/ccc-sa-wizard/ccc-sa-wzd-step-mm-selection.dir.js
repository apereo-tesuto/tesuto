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

    angular.module('CCC.View.Home').directive('cccSaWzdStepMmSelection', function () {

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

                    $scope.model.competencyAttributes.placementComponentMmap = !!($scope.model.competencyAttributes.placementComponentMmap); // force into boolean

                    $scope.submitted = false;

                    $scope.mmSelectionOptions = [
                        {
                            value: 'placementComponentMmap',
                            label: 'MMAP - High School Transcript Based',
                            help: 'MMAP enables selection from both statewide and custom decision logic.'
                        }
                    ];

                    $scope.selectedOptions = [];


                    /*============ MODEL DEPENDENT METHODS ============*/

                    // NOTE: will need to redo this logic for each possible option to make sure a selectionFound is true for each option
                    var setMMSelections = function () {

                        _.each($scope.mmSelectionOptions, function (option) {
                            option.selected = $scope.model.competencyAttributes[option.value];
                        });
                    };

                    var updateSelectedOptions = function () {

                        $scope.selectedOptions = _.filter($scope.mmSelectionOptions, function (option) {
                            return option.selected;
                        });
                    };


                    /*============= BEHAVIOR =============*/


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-wizard.stepSubmitted', function () {
                        $scope.submitted = true;
                        UtilsService.focusOnFirstInvalidInput($element);
                    });

                    $scope.$on('ccc-detailed-select.changed', function (event, selectId, selectedOptions, options) {

                        // here we map from the selected options to the actual invidual model properties associated with each option
                        _.each(options, function (option) {
                            if (option.selected) {
                                $scope.model.competencyAttributes[option.value] = true;
                            } else {
                                $scope.model.competencyAttributes[option.value] = false;
                            }
                        });

                        updateSelectedOptions();

                        $scope.mmSelectionForm.mmSelections.$setDirty();
                    });



                    /*============ INITIALIZATION ==============*/

                    setMMSelections();
                    updateSelectedOptions();

                    // fire off callback that this view is ready and return the isValid function that says that this step is complete and the next step can be loaded
                    $scope.onReady(function (immutableData, model) {

                        // NOTE: When we add more MM options we will have to change this logic. For now we want to force the user to check the only box available
                        return $scope.model.competencyAttributes.placementComponentMmap === true;
                    });
                }
            ],

            template: [

                '<h3 translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-MM-SELECTION.TITLE" class="form-title"></h3>',

                '<ccc-label-required></ccc-label-required>',

                '<p class="help-block" translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-MM-SELECTION.HELP_BLOCK"></p>',

                '<div class="ccc-form-container margin-bottom-md margin-top-md" ng-form name="mmSelectionForm">',

                    '<fieldset class="margin-bottom-md" ng-class="{\'ccc-show-errors\': submitted || mmSelectionForm.$dirty}">',

                        '<legend class="mimic-label margin-top-sm" id="mmSelectionLabel"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-MM-SELECTION.MM_SELECTION_LABEL"></span></legend>',
                        '<ccc-detailed-select options="mmSelectionOptions" multi-select="true" is-required="true" described-by="mmSelectedErrors" is-invalid="mmSelectionForm.mmSelections.$invalid"></ccc-detailed-select>',

                        '<input class="hidden" name="mmSelections" required ng-model="selectedOptions" ng-model-options="{allowInvalid: true}" autocomplete="off"/>',

                        '<div id="mmSelectedErrors" class="ccc-validation-messages ccc-validation-messages-standalone noanim" ng-messages="mmSelectionForm.mmSelections.$error">',
                            '<p ng-message="required" class="noanim"><i class="fa fa-exclamation-triangle color-warning"></i> <span translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-SHOW-ELA-PLACEMENTS.REQUIRED"></span></p>',
                        '</div>',

                    '</fieldset>',

                '</div>'

            ].join('')

        };

    });

})();
