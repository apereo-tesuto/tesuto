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

    angular.module('CCC.View.Student').directive('cccStudentTestResultsView', function () {

        return {

            restrict: 'E',

            scope: {
                collegeId: "=",
                placementId: "="
            },

            controller: [

                '$scope',
                'PlacementCollegesAPIService',

                function ($scope, PlacementCollegesAPIService) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/


                    /*============ MODEL ============*/

                    $scope.loading = false;

                    $scope.assessmentSessionId = false;
                    $scope.assessmentTitle = false;
                    $scope.collegeName = false;
                    $scope.completedDate = false;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var initialize = function () {

                        $scope.loading = true;

                        PlacementCollegesAPIService.getPlacementAssessmentComponentMetaData($scope.collegeId, $scope.placementId).then(function (placementData) {

                            $scope.assessmentSessionId = placementData.assessmentSessionId;
                            $scope.assessmentTitle = placementData.assessmentTitle;
                            $scope.collegeName = placementData.collegeName;
                            $scope.completedDate = placementData.assessmentCompletedDate;

                        }).finally(function () {
                            $scope.loading = false;
                        });
                    };


                    /*============ BEHAVIOR ============*/

                    /*============ LISTENERS ============*/


                    /*============ INITIALIZATION ============*/

                    initialize();
                }
            ],

            template: [

                '<ccc-content-loading-placeholder ng-if="loading || !loading && !assessmentSessionId" no-results-info="!loading && !assessmentSessionId" hide-default-no-results-text="true">',
                    '<div><strong><i class="fa fa-info-circle" aria-hidden="true"></i> <span translate="CCC_STUDENT.CCC-STUDENT-TEST-RESULTS-VIEW.ERROR_TITLE"></span></strong> <span translate="CCC_STUDENT.CCC-STUDENT-TEST-RESULTS-VIEW.ERROR_MESSAGE"></span></div>',
                '</ccc-content-loading-placeholder>',

                '<ccc-student-test-results ng-if="!loading && assessmentSessionId" ',
                    ' assessment-session-id="assessmentSessionId" ',
                    ' assessment-title="assessmentTitle" ',
                    ' completed-date="completedDate" ',
                    ' college-name="collegeName" ',
                '></ccc-student-test-results>'

            ].join('')

        };

    });

})();
