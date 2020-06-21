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

    angular.module('CCC.View.Home').directive('cccSaWzdStepSummary', function () {

        return {

            restrict: 'E',

            scope: {
                immutableData: '=',
                model: '=',
                onReady: '=',
                wizardInstance: '=',
                isDisabled: '='
            },

            controller: [

                '$scope',
                '$element',
                'UtilsService',
                'MMSubjectAreaClass',

                function ($scope, $element, UtilsService, MMSubjectAreaClass) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    $scope.submitted = false;

                    $scope.ignoreFields = {};

                    $scope.isMMSubjectArea = $scope.model instanceof MMSubjectAreaClass;

                    if ($scope.isMMSubjectArea) {
                        $scope.ignoreFields["usePrereqPlacementMethod"] = true;
                    }


                    /*============ MODEL DEPENDENT METHODS ============*/


                    /*============= BEHAVIOR =============*/


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-wizard.stepSubmitted', function () {
                        $scope.submitted = true;
                        UtilsService.focusOnFirstInvalidInput($element);
                    });


                    /*============ INITIALIZATION ==============*/

                    // fire off callback that this view is ready and return the isValid function that says that this step is complete and the next step can be loaded
                    $scope.onReady(function (immutableData, model) {
                        return true;
                    });
                }
            ],

            template: [

                '<h3 translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-SUMMARY.TITLE" class="form-title"></h3>',

                '<div class="ccc-form-container margin-bottom-md margin-top-md">',

                    '<fieldset class="margin-bottom-md">',

                        '<legend class="margin-top-sm" id="summaryLabel" translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-SUMMARY.TITLE_SUMMARY"></legend>',
                        '<ccc-wizard-summary wizard-instance="wizardInstance" ignore-fields="ignoreFields"></ccc-wizard-summary>',

                    '</fieldset>',

                '</div>'

            ].join('')

        };

    });

})();
