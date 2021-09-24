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

    angular.module('CCC.View.Home').directive('cccSaWzdStepMetaData', function () {

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

                    $scope.subjectAreaFormLoading = true;

                    $scope.submitted = false;

                    // the ccc-subject-area-form will look for subjectArea
                    $scope.subjectArea = $scope.model;


                    /*============ MODEL DEPENDENT METHODS ============*/


                    /*============= BEHAVIOR =============*/


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-subject-area-form.loaded', function () {
                        $scope.subjectAreaFormLoading = false;
                    });

                    $scope.$on('ccc-wizard.stepSubmitted', function () {
                        $scope.submitted = true;
                        UtilsService.focusOnFirstInvalidInput($element);
                    });


                    /*============ INITIALIZATION ==============*/

                    // fire off callback that this view is ready and return the isValid function that says that this step is complete and the next step can be loaded
                    $scope.onReady(function (immutableData, model) {
                        return !$scope.subjectAreaFormLoading && $scope.cccCreateSubjectAreaForm.$valid;
                    });
                }
            ],

            template: [

                '<h3 translate="CCC_VIEW_HOME.CCC-SA-WZD-STEP-META-DATA.TITLE" class="form-title"></h3>',

                '<form name="cccCreateSubjectAreaForm" novalidate>',

                    '<ccc-label-required></ccc-label-required>',

                    '<ccc-subject-area-form></ccc-subject-area-form>',

                '</form>'

            ].join('')

        };

    });

})();
