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

    angular.module('CCC.View.Home').directive('cccAssessmentsListReadOnly', function () {

        return {

            restrict: 'E',

            scope: {
                assessmentIdentifiers: '=',
                testLocationId: '='
            },

            controller: [

                '$scope',
                'AssessmentsAPIService',

                function ($scope, AssessmentsAPIService) {

                    /*============ PRIVATE VARIABLES ============*/


                    /*============ MODEL ============*/

                    $scope.loading = false;

                    $scope.assessments = [];


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var generateAssessments = function (assessmentsData) {

                        $scope.assessments = [];

                        var matchingAssessmentData;
                        _.each($scope.assessmentIdentifiers, function (assessmentIdentifier) {

                            matchingAssessmentData = _.find(assessmentsData, function (assessmentData) {
                                return assessmentData.identifier === assessmentIdentifier.identifier && assessmentData.namespace === assessmentIdentifier.namespace;
                            });

                            if (matchingAssessmentData) {

                                $scope.assessments.push({
                                    deliveryType: matchingAssessmentData.online ? "ONLINE" : "PAPER",
                                    title: matchingAssessmentData.title,
                                    value: matchingAssessmentData.id,
                                    id: matchingAssessmentData.identifier,
                                    identifier: matchingAssessmentData.identifier,
                                    namespace: matchingAssessmentData.namespace
                                });
                            }
                        });
                    };

                    var loadAssessments = function () {

                        $scope.loading = true;

                        AssessmentsAPIService.getAssessmentsByTestLocationId($scope.testLocationId).then(function (assessmentsData) {

                            generateAssessments(assessmentsData);

                        }, function (err) {

                            generateAssessments([]);

                        }).finally(function () {

                            $scope.loading = false;
                        });
                    };


                    /*============ BEHAVIOR ============*/

                    /*============ LISTENERS ============*/


                    /*============ INITIALIZATION ============*/

                    loadAssessments();
                }
            ],

            template: [

                '<div class="well">',

                    '<ccc-content-loading-placeholder ng-if="loading || (!loading && !assessments.length)" no-results="(!loading && !assessments.length)"></ccc-content-loading-placeholder>',

                    '<ul ng-if="!loading && assessments.length">',
                        '<li ng-repeat="assessment in assessments"><strong>{{::assessment.title}}</strong></li>',
                    '</ul>',

                '</div>'

            ].join('')

        };

    });

})();
