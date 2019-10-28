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

    angular.module('CCC.Placement').directive('cccStudentTestResults', function () {

        return {

            restrict: 'E',

            scope: {
                assessmentSessionId: '=',
                assessmentTitle: '=',
                completedDate: '=',
                collegeName: '=?' // conditionally will use for display purposes if available
            },

            controller: [

                '$scope',
                '$q',
                'Moment',
                '$translate',
                'ActivationsAPIService',
                'ActivationClass',
                'AssessmentSessionsAPIService',

                function ($scope, $q, Moment, $translate, ActivationsAPIService, ActivationClass, AssessmentSessionsAPIService) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/


                    /*============ MODEL ============*/

                    $scope.testResults = [];
                    $scope.activationLoading = false;
                    $scope.loading = false;
                    $scope.testResultsError = false;

                    $scope.completeDateString = '';


                    /*============= MODEL DEPENDENT METHODS ============*/

                    var getTestResults = function (assessmentSessionId) {
                        return AssessmentSessionsAPIService.getCompetencyStudentMastery(assessmentSessionId);
                    };

                    var processTestResults = function (results) {
                        return _.map(results, function(value, key) {
                            return {
                                assessmentType: key,
                                results: value,
                                position: value.competenciesForMap.position
                            };
                        });
                    };

                    var getCompleteDateString = function (completedDate) {

                        var completeMoment = new Moment(completedDate);
                        var completedDateText = $translate.instant('CCC_PLACEMENT.CCC_STUDENT_TEST_RESULTS.COMPLETED');

                        return completedDateText + ' ' + completeMoment.calendar();
                    };

                    var initialize = function () {

                        // setup the completion string
                        $scope.completeDateString = getCompleteDateString($scope.completedDate);

                        $scope.loading = true;

                        return getTestResults($scope.assessmentSessionId).then(function(results) {

                            $scope.testResults = processTestResults(results);

                        }, function (err) {

                            $scope.testResultsError = true;

                        })
                        .finally(function () {
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

                '<div class="competency-explanation">',
                    '<p translate="CCC_PLACEMENT.CCC_STUDENT_TEST_RESULTS.EXPLAIN"></p>',
                '</div>',

                '<div class="discipline-header" ng-if="!loading">',
                    '<h2 class="title">{{::assessmentTitle}}</h2>',
                    '<div class="header-details">',
                        '<span>{{::completeDateString}} <span ng-if="collegeName">at {{::collegeName}}</span></span>',
                    '</div>',
                '</div>',

                '<ccc-content-loading-placeholder ng-if="loading"></ccc-content-loading-placeholder>',
                '<div ng-if="!loading && testResultsError" class="alert alert-warning testResults-error" role="alert" translate="CCC_PLACEMENT.CCC_STUDENT_TEST_RESULTS.RESULTS_ERROR"></div>',

                '<div ng-if="!testResultsError && !loading" class="discipline-body">',
                    '<ccc-overall-performance test-results="testResults"></ccc-overall-performance>',
                    '<div class="row">',
                        '<div class="col col-md-6">',
                            '<h3 class="title" translate="CCC_PLACEMENT.CCC_STUDENT_TEST_RESULTS.TITLE.CATEGORY"></h3>',
                            '<ccc-performance-by-category test-results="testResults"></ccc-performance-by-category>',
                        '</div>',
                        '<div class="col col-md-6">',
                            '<h3 class="title" translate="CCC_PLACEMENT.CCC_STUDENT_TEST_RESULTS.TITLE.TYPICALLY"></h3>',
                            '<ccc-competency-results is-student="true" assessment-session-id="assessmentSessionId"></ccc-competency-results>',
                        '</div>',
                    '</div>',
                '</div>',

            ].join('')

        };

    });

})();
