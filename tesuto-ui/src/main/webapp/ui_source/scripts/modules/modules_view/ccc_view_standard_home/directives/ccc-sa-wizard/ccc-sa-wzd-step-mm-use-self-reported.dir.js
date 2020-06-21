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

    angular.module('CCC.View.Home').directive('cccSaWzdStepMmUseSelfReported', function () {

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

                    $scope.mmOptions = [
                        {
                            value: true,
                            label: 'Yes',
                            selected: $scope.model.competencyAttributes.useSelfReportedDataForMM === true,
                            help: 'When processing the MMAP decision logic, this flag will allow use of transcript data (GPA, specific courses, etc.) self-reported by the student. Self-reported information is not verified in any manner by Tesuto.'
                        },
                        {
                            value: false,
                            label: 'No',
                            type: 'NO',
                            selected: $scope.model.competencyAttributes.useSelfReportedDataForMM === false,
                            help: 'When processing the MMAP decision logic, only verified transcipt data will be used. As verified data is less common, using only verified data may result in more students for which Tesuto cannot generate a placement because the MMAP component does not have enough transcript data.'
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
                        $scope.model.competencyAttributes.useSelfReportedDataForMM = selectedOptions[0].value;
                        $scope.mmForm.useSelfReportedDataForMM.$setDirty();
                    });



                    /*============ INITIALIZATION ==============*/

                    // fire off callback that this view is ready and return the isValid function that says that this step is complete and the next step can be loaded
                    $scope.onReady(function (immutableData, model) {
                        return $scope.mmForm.$valid;
                    });
                }
            ],

            template: [

                '<h3 translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-MM-USE-SELF-REPORTED.TITLE" class="form-title"></h3>',

                '<ccc-label-required></ccc-label-required>',

                '<div class="ccc-form-container margin-bottom-md margin-top-md" ng-form name="mmForm">',

                    '<fieldset class="margin-bottom-md" ng-class="{\'ccc-show-errors\': submitted || mmForm.useSelfReportedDataForMM.$dirty}">',

                        '<legend class="mimic-label margin-top-sm" id="mmSelfReportLabel"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-MM-USE-SELF-REPORTED.SELF_REPORT_LABEL"></span></legend>',
                        '<ccc-detailed-select options="mmOptions" multi-select="false" is-required="true" described-by="mmErrors" is-invalid="mmForm.useSelfReportedDataForMM.$invalid"></ccc-detailed-select>',

                        '<input class="hidden" name="useSelfReportedDataForMM" required ng-model="model.competencyAttributes.useSelfReportedDataForMM" ng-model-options="{allowInvalid: true}" autocomplete="off"/>',
                        '<div id="mmErrors" class="ccc-validation-messages ccc-validation-messages-standalone noanim" ng-messages="mmForm.useSelfReportedDataForMM.$error">',
                            '<p ng-message="required" class="noanim"><i class="fa fa-exclamation-triangle color-warning"></i> <span translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-SHOW-ELA-PLACEMENTS.REQUIRED"></span></p>',
                        '</div>',

                    '</fieldset>',

                '</div>'

            ].join('')

        };

    });

})();
