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

    angular.module('CCC.View.Home').directive('cccSaWzdStepShowElaPlacements', function () {

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

                    $scope.identifyAsEnglishOptions = [
                        {
                            value: true,
                            label: 'Yes',
                            selected: $scope.model.competencyAttributes.showPlacementToNativeSpeaker === true,
                        },
                        {
                            value: false,
                            label: 'No',
                            selected: $scope.model.competencyAttributes.showPlacementToNativeSpeaker === false,
                            type: 'NO'
                        }
                    ];

                    $scope.identifyAsESLOptions = [
                        {
                            value: true,
                            label: 'Yes',
                            selected: $scope.model.competencyAttributes.showPlacementToEsl === true,
                        },
                        {
                            value: false,
                            label: 'No',
                            selected: $scope.model.competencyAttributes.showPlacementToEsl === false,
                            type: 'NO'
                        }
                    ];


                    /*============ MODEL DEPENDENT METHODS ============*/

                    /*============= BEHAVIOR =============*/


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-wizard.stepSubmitted', function () {
                        $scope.submitted = true;
                        UtilsService.focusOnFirstInvalidInput($element);
                    });

                    $scope.$on('ccc-detailed-select.changed', function (event, selectId, selectedOptions, options) {

                        if (selectId === 'identifyAsEnglishOptions') {

                            $scope.model.competencyAttributes.showPlacementToNativeSpeaker = selectedOptions[0].value;
                            $scope.elaForm.showPlacementToNativeSpeaker.$setDirty();

                        } else if (selectId === 'identifyAsESLOptions') {

                            $scope.model.competencyAttributes.showPlacementToEsl = selectedOptions[0].value;
                            $scope.elaForm.showPlacementToEsl.$setDirty();
                        }
                    });


                    /*============ INITIALIZATION ==============*/

                    // fire off callback that this view is ready and return the isValid function that says that this step is complete and the next step can be loaded
                    $scope.onReady(function (immutableData, model) {
                        return $scope.elaForm.$valid;
                    });
                }
            ],

            template: [

                '<h3 translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-SHOW-ELA-PLACEMENTS.TITLE" class="form-title"></h3>',

                '<ccc-label-required></ccc-label-required>',

                '<p class="help-block" translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-SHOW-ELA-PLACEMENTS.HELP_BLOCK"></p>',

                '<div class="ccc-form-container margin-bottom-md" ng-form name="elaForm">',

                    '<h4 class="form-section-title margin-bottom-md margin-top-sm" translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-SHOW-ELA-PLACEMENTS.SUB_TITLE"></h4>',

                    '<fieldset class="margin-bottom-md" ng-class="{\'ccc-show-errors\': submitted || elaForm.showPlacementToNativeSpeaker.$dirty}">',

                        '<legend class="mimic-label margin-top-sm" id="showPlacementToNativeSpeakerLabel"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-SHOW-ELA-PLACEMENTS.FOR_ENGLISH_STUDENTS_LABEL"></span></legend>',
                        '<ccc-detailed-select options="identifyAsEnglishOptions" multi-select="false" is-required="true" id="identifyAsEnglishOptions" described-by="showPlacementToNativeSpeakerErrors" is-invalid="elaForm.showPlacementToNativeSpeaker.$invalid"></ccc-detailed-select>',

                        '<input class="hidden" name="showPlacementToNativeSpeaker" required ng-model="model.competencyAttributes.showPlacementToNativeSpeaker" ng-model-options="{allowInvalid: true}" autocomplete="off"/>',
                        '<div id="showPlacementToNativeSpeakerErrors" class="ccc-validation-messages ccc-validation-messages-standalone noanim" ng-messages="elaForm.showPlacementToNativeSpeaker.$error">',
                            '<p ng-message="required" class="noanim"><i class="fa fa-exclamation-triangle color-warning"></i> <span translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-SHOW-ELA-PLACEMENTS.REQUIRED"></span></p>',
                        '</div>',

                    '</fieldset>',

                    '<fieldset ng-class="{\'ccc-show-errors\': submitted || elaForm.showPlacementToEsl.$dirty}">',

                        '<legend class="mimic-label margin-top-sm" id="showPlacementToEslLabel"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-SHOW-ELA-PLACEMENTS.FOR_ELA_STUDENTS_LABEL"></span></legend>',
                        '<ccc-detailed-select options="identifyAsESLOptions" multi-select="false" is-required="true" id="identifyAsESLOptions" described-by="showPlacementToEslErrors" is-invalid="elaForm.showPlacementToEsl.$invalid"></ccc-detailed-select>',

                        '<input class="hidden" name="showPlacementToEsl" required ng-model="model.competencyAttributes.showPlacementToEsl" ng-model-options="{allowInvalid: true}" autocomplete="off"/>',
                        '<div id="showPlacementToEslErrors" class="ccc-validation-messages ccc-validation-messages-standalone noanim" ng-messages="elaForm.showPlacementToEsl.$error">',
                            '<p ng-message="required" class="noanim"><i class="fa fa-exclamation-triangle color-warning"></i> <span translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-SHOW-ELA-PLACEMENTS.REQUIRED"></span></p>',
                        '</div>',

                    '</fieldset>',

                '</div>'

            ].join('')

        };

    });

})();
