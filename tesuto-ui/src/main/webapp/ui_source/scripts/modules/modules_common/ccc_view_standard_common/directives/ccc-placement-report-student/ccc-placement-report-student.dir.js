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

    angular.module('CCC.View.Common').directive('cccPlacementReportStudent', function () {

        return {

            restrict: 'E',

            scope: {
                collegeName: '=',
                collegeId: '=',
                activation: '='
            },

            controller: [

                '$scope',
                'PlacementAPIService',

                function ($scope, PlacementAPIService) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/


                    /*============ MODEL ============*/

                    $scope.coursePlacementLoading = false;
                    $scope.placementInfoNotFound = false;
                    $scope.placementUnauthorized = false;
                    $scope.placementDataError = false;


                    /*============= MODEL DEPENDENT METHODS ============*/

                    var loadCoursePlacementData = function () {

                        $scope.coursePlacementLoading = true;
                        $scope.placementInfoNotFound = false;
                        $scope.placementUnauthorized = false;
                        $scope.placementDataError = false;

                        var assessmentSessionId = $scope.activation.assessmentSessionIds[0];

                        PlacementAPIService.getPlacementDecisionStudent($scope.collegeId, assessmentSessionId).then(function (placementData) {

                            $scope.placementData = placementData;

                        }, function (e) {

                            $scope.placementDataError = true;

                            // 404 means that likely it just isn't ready
                            if (e.status + '' === '404') {

                                $scope.placementInfoNotFound = true;

                            // something strange is going on, so let's display some messaging to help users report better info for troubleshooting
                            } else if (e.status + '' === '401') {
                                $scope.placementUnauthorized = true;
                            }

                        }).finally(function () {
                            $scope.coursePlacementLoading = false;
                        });
                    };

                    var debounced_loadCoursePlacementData = _.debounce(loadCoursePlacementData, 10);


                    /*============ BEHAVIOR ============*/


                    /*============ LISTENERS ============*/

                    $scope.$watch('collegeId', debounced_loadCoursePlacementData);
                    $scope.$watch('activation.assessmentSessionIds[0]', debounced_loadCoursePlacementData);


                    /*============ INITIALIZATION ============*/

                }
            ],

            template: [

                '<h2 class="college-name" ng-if="collegeName">{{collegeName}}</h2>',

                '<div class="alert alert-info" role="alert" ng-if="!coursePlacementLoading && placementInfoNotFound"> <strong><i class="fa fa-info-circle" aria-hidden="true"></i> Your placement is not available yet.</strong> If you continue to receive this message, please see your counselor. </div>',
                '<div class="alert alert-warning" role="alert" ng-if="!coursePlacementLoading && placementUnauthorized"> <strong><i class="fa fa-exclamation-triangle" aria-hidden="true"></i> Unauthorized.</strong> You are not authorized to view this placement. Contact your counselor for help.</div>',

                '<ccc-content-loading-placeholder ng-if="coursePlacementLoading"></ccc-content-loading-placeholder>',

                '<ccc-placement-course-details ng-if="!coursePlacementLoading && !placementDataError" placement-data="placementData" is-student="true"></ccc-placement-course-details>',

                '<h3 class="competency-results-title" translate="CCC_VIEW_STANDARD_COMMON.CCC-PLACEMENT-REPORT-STUDENT.COMPETENCY_RESULTS_TITLE"></h3>',
                '<p class="text-info"><span class="icon fa fa-info-circle" role="presentation" aria-hidden="true"></span> <span translate="CCC_VIEW_STANDARD_COMMON.CCC-PLACEMENT-REPORT-STUDENT.COMPETENCY_RESULTS_DESCRIPTION"></span></p>',

                '<ccc-competency-results ng-if="activation" assessment-session-id="activation.currentAssessmentSessionId" is-student="true"></ccc-competency-results>'

            ].join('')

        };

    });

})();

