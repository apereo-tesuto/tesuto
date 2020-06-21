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

    angular.module('CCC.View.Home').directive('cccSaWzdStepNoPlacement', function () {

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
                '$translate',
                'UtilsService',
                'MMSubjectAreaClass',

                function ($scope, $element, $translate, UtilsService, MMSubjectAreaClass) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    $scope.submitted = false;

                    $scope.isMMSubjectArea = $scope.model instanceof MMSubjectAreaClass;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var initialize = function () {

                        if (!$scope.model.noPlacementMessage) {
                            $scope.model.noPlacementMessage = $translate.instant('CCC_VIEW_HOME.CCC-SA-WZD-STEP-NO-PLACEMENT.DEFAULT_MESSAGE');
                        }

                        // fire off callback that this view is ready and return the isValid function that says that this step is complete and the next step can be loaded
                        $scope.onReady(function (immutableData, model) {
                            return $scope.mmNoPlacementForm.$valid;
                        });
                    };


                    /*============= BEHAVIOR =============*/


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-wizard.stepSubmitted', function () {
                        $scope.submitted = true;
                        UtilsService.focusOnFirstInvalidInput($element);
                    });


                    /*============ INITIALIZATION ==============*/

                    initialize();
                }
            ],

            template: [

                '<h3 translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-NO-PLACEMENT.TITLE" class="form-title"></h3>',
                '<p class="help-block" translate="{{ isMMSubjectArea ? \'CCC_VIEW_HOME.CCC-SA-WZD-STEP-NO-PLACEMENT.HELP_BLOCK_MM\' : \'CCC_VIEW_HOME.CCC-SA-WZD-STEP-NO-PLACEMENT.HELP_BLOCK\' }}"></p>',

                '<ccc-label-required></ccc-label-required>',

                '<div class="ccc-form-container margin-top-xs" ng-form name="mmNoPlacementForm">',

                    '<fieldset class="margin-bottom-md" ccc-show-errors="submitted || mmNoPlacementForm.noPlacementMessage.$dirty">',

                        '<legend class="mimic-label margin-top-sm" id="prerequisiteGeneralEducationLabel"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-NO-PLACEMENT.LABEL"></span></legend>',

                        '<textarea class="form-control" ',
                            'name="noPlacementMessage" ',
                            'required ',
                            'ng-model="model.noPlacementMessage" ',
                            'ng-model-options="{allowInvalid: true}" ',
                            'width="100%" ',
                            'rows="12" ',
                            'aria-describedby="noPlacementMessageErrors" ',
                            'ccc-validation-badge-style="fullWidth" ',
                            'ccc-validation-badge="mmNoPlacementForm" ',
                            'ng-maxlength="3000" ',
                        '></textarea>',

                        '<div id="noPlacementMessageErrors" class="ccc-validation-messages ccc-validation-messages-standalone noanim" ng-messages="mmNoPlacementForm.noPlacementMessage.$error">',
                            '<p ng-message="required" class="noanim"><i class="fa fa-exclamation-triangle color-warning"></i> <span translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-NO-PLACEMENT.REQUIRED"></span></p>',
                            '<p ng-message="maxlength" class="noanim"><i class="fa fa-exclamation-triangle color-warning"></i> <span translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-NO-PLACEMENT.MAX_LENGTH"></span></p>',
                        '</div>',

                    '</fieldset>',

                '</div>'

            ].join('')

        };

    });

})();
