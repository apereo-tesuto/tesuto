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

    angular.module('CCC.View.Student').directive('cccStudentTestResults', function () {

        return {

            restrict: 'E',

            scope: {
                activation: '='
            },

            controller: [

                '$scope',
                'AssessmentSessionsAPIService',

                function ($scope, AssessmentSessionsAPIService) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    /*============ MODEL ============*/

                    $scope.testResults = [];
                    $scope.loading = true;
                    $scope.testResultsError = false;


                    /*============= MODEL DEPENDENT METHODS ============*/

                    var getTestResults = function (assessmentSessionId) {
                        return AssessmentSessionsAPIService.getCompetencyStudentMastery(assessmentSessionId);
                    };

                    var processTestResults = function (results) {
                        _.map(results, function(value, key) {

                            $scope.testResults.push({
                                assessmentType: key,
                                results: value,
                                position: value.competenciesForMap.position
                            });
                        });
                    };


                    /*============ BEHAVIOR ============*/

                    /*============ LISTENERS ============*/

                    /*============ INITIALIZATION ============*/

                    getTestResults($scope.activation.currentAssessmentSessionId).then(function(results) {
                        processTestResults(results);

                    }, function (err) {
                        $scope.testResultsError = true;
                        $scope.loading = false;

                    }).finally(function () {
                        $scope.loading = false;
                    });
                }
            ],

            template: [

                '<div class="competency-explanation">',
                    '<p translate="CCC_STUDENT.CCC_STUDENT_TEST_RESULTS.EXPLAIN"></p>',
                '</div>',

                '<div class="discipline-header">',
                    '<h2 class="title">{{activation.assessmentTitle}}</h2>',
                    '<div class="header-details">',
                        '<span>{{activation.asyncCompletionString}} at {{activation.collegeName}}</span>',
                    '</div>',
                '</div>',

                '<ccc-content-loading-placeholder ng-if="loading"></ccc-content-loading-placeholder>',
                '<div ng-if="!loading && testResultsError" class="alert alert-warning testResults-error" role="alert" translate="CCC_STUDENT.CCC_STUDENT_TEST_RESULTS.RESULTS_ERROR"></div>',

                '<div ng-if="!testResultsError && !loading" class="discipline-body">',
                    '<h3 class="title" translate="CCC_STUDENT.CCC_STUDENT_TEST_RESULTS.TITLE.OVERALL"></h3>',
                    '<ccc-overall-performance test-results="testResults"></ccc-overall-performance>',
                    '<div class="row">',
                        '<div class="col col-md-6">',
                            '<h3 class="title" translate="CCC_STUDENT.CCC_STUDENT_TEST_RESULTS.TITLE.CATEGORY"></h3>',
                            '<ccc-performance-by-category test-results="testResults"></ccc-performance-by-category>',
                        '</div>',
                        '<div class="col col-md-6">',
                            '<h3 class="title" translate="CCC_STUDENT.CCC_STUDENT_TEST_RESULTS.TITLE.TYPICALLY"></h3>',
                            '<ccc-competency-results is-student="true" activation="activation"></ccc-competency-results>',
                        '</div>',
                    '</div>',
                '</div>',

            ].join('')

        };

    });

})();
