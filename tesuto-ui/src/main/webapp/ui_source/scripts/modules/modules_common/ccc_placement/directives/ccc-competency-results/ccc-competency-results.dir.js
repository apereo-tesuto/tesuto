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

    angular.module('CCC.Placement').directive('cccCompetencyResults', function () {

        return {

            restrict: 'E',

            scope: {
                assessmentSessionId: '=?',
                isStudent: '=',
                isCounselorReport: '=?',
                competencyMapResults: '=?'
            },

            controller: [

                '$scope',
                'AssessmentSessionsAPIService',

                function ($scope, AssessmentSessionsAPIService) {


                    /*============= PRIVATE VARIABLES AND METHODS =============*/

                    /*============ MODEL ============*/

                    $scope.loading = true;
                    $scope.isStudent = $scope.isStudent || false;
                    $scope.assessmentSessionId = $scope.assessmentSessionId || false;
                    $scope.isCounselorReport = $scope.isCounselorReport || false;
                    $scope.competencyError = false;
                    $scope.competencyMapResults = $scope.competencyMapResults || {};
                    $scope.competencyTopicResults = {};


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var setCompetencyResults = function (results) {
                        // We're only using the first returned competency
                        var key = _.keys(results)[0];

                        _.each(results, function (result, index) {
                            if (index === key) {
                                $scope.competencyMapResults = result.competenciesForMap;

                                if ($scope.isCounselorReport) {
                                    $scope.competencyTopicResults = result.competenciesByTopic;

                                    // Generate a scaled measure percent for the template to render
                                    _.each($scope.competencyTopicResults, function (topic) {
                                        var scaledMeasurePercent = ((topic.measure - topic.minimumMeasure) / (topic.maximumMeasure - topic.minimumMeasure)) * 100;

                                        if (scaledMeasurePercent < 0) {
                                            scaledMeasurePercent = 0;
                                        } else if (scaledMeasurePercent > 100) {
                                            scaledMeasurePercent = 100;
                                        }

                                        topic.scaledMeasurePercent = scaledMeasurePercent;
                                    });
                                }
                            }
                        });
                    };

                    var getCompetencyResults = function (assessmentSessionId) {
                        $scope.loading = true;
                        $scope.competencyError = false;

                        if ($scope.isStudent) {

                            AssessmentSessionsAPIService.getCompetencyStudentMastery(assessmentSessionId).then(function (results) {
                                setCompetencyResults(results);
                            }, function (err) {
                                $scope.competencyError = true;
                            }).finally(function () {
                                $scope.loading = false;
                            });

                        } else {

                            AssessmentSessionsAPIService.getCompetencyMastery(assessmentSessionId).then(function (results) {
                                setCompetencyResults(results);
                            }, function (err) {
                                $scope.competencyError = true;
                            }).finally(function () {
                                $scope.loading = false;
                            });

                        }
                    };

                    var debounced_getCompetencyResults = _.debounce(function (assessmentSessionId) {
                        getCompetencyResults(assessmentSessionId);
                    }, 10);


                    /*============ BEHAVIOR ============*/


                    /*============ LISTENERS ============*/

                    if (_.isEmpty($scope.competencyMapResults)) {
                        $scope.$watch('assessmentSessionId', debounced_getCompetencyResults);
                    } else {
                        $scope.loading = false;
                    }


                    /*============ INITIALIZATION ============*/
                }
            ],

            template: [

                '<ccc-content-loading-placeholder ng-if="loading"></ccc-content-loading-placeholder>',

                // student report (this is only one grouping)
                '<div ng-if="!loading && !competencyError && !isCounselorReport">',

                    '<div class="row">',
                        '<div class="col-md-12" ng-if="competencyMapResults.mastered.length">',
                            '<h6 class="competency-title" translate="CCC_PLACEMENT.CCC_COMPETENCY_RESULTS.SKILLS_HAVE"></h6>',
                            '<ul class="competency-list">',
                                '<li ng-repeat="item in competencyMapResults.mastered"><span class="icon fa fa-check-square" role="presentation" aria-hidden="true"></span> ',
                                    '<span ng-if="item.studentDescription" ng-bind-html="item.studentDescription"></span>',
                                    '<span ng-if="!item.studentDescription" ng-bind-html="item.description"></span>',
                                '</li>',
                            '</ul>',
                        '</div>',
                        '<div class="col-md-12" ng-if="competencyMapResults.tolearn.length">',
                            '<h6 class="competency-title" translate="CCC_PLACEMENT.CCC_COMPETENCY_RESULTS.SKILLS_WORK"></h6>',
                            '<ul class="competency-list">',
                                '<li ng-repeat="item in competencyMapResults.tolearn"><span class="icon fa fa-square" role="presentation" aria-hidden="true"></span>',
                                    '<span ng-if="item.studentDescription" ng-bind-html="item.studentDescription"></span>',
                                    '<span ng-if="!item.studentDescription" ng-bind-html="item.description"></span>',
                                '</li>',
                            '</ul>',
                        '</div>',
                    '</div>',

                '</div>',

                // Specifically for counselor report page only (there will be an array of topics)
                '<div class="row competency-counselor-view" ng-if="!loading && !competencyError && isCounselorReport" ng-repeat="topic in competencyTopicResults">',
                    '<h4 class="name">{{topic.parent.description}}</h4>',
                    '<div>',
                        '<div class="bar-labels">',
                            '<div class="bar-label start">NOVICE</div>',
                            '<div class="bar-label middle">INTERMEDIATE</div>',
                            '<div class="bar-label end">ADVANCED</div>',
                        '</div>',
                        '<div class="bar-container">',
                            '<div class="div-container">',
                                '<div class="bar-div start"></div>',
                                '<div class="bar-div end"></div>',
                            '</div>',
                            '<div class="bar-wrapper" ng-style="{\'width\': topic.scaledMeasurePercent + \'%\'}">',
                                '<div class="bar"></div>',
                                '<div class="score-marker"></div>',
                            '</div>',
                        '</div>',
                        '<div class="row">',
                            '<div class="col-md-12" ng-if="topic.mastered.length">',
                                '<h6 class="competency-title" translate="CCC_PLACEMENT.CCC_COMPETENCY_RESULTS.SKILLS_HAVE"></h6>',
                                '<ul class="competency-list">',
                                    '<li ng-repeat="item in topic.mastered"><span class="icon fa fa-check-square" role="presentation" aria-hidden="true"></span> <span ng-bind-html="item.description"></span</li>',
                                '</ul>',
                            '</div>',
                            '<div class="col-md-12" ng-if="topic.tolearn.length">',
                                '<h6 class="competency-title" translate="CCC_PLACEMENT.CCC_COMPETENCY_RESULTS.SKILLS_WORK"></h6>',
                                '<ul class="competency-list">',
                                    '<li ng-repeat="item in topic.tolearn"><span class="icon fa fa-square" role="presentation" aria-hidden="true"></span> <span ng-bind-html="item.description"></span></li>',
                                '</ul>',
                            '</div>',
                        '</div>',
                    '</div>',
                '</div>',

                '<div ng-if="!loading && competencyError" class="alert alert-warning competency-error" role="alert" translate="CCC_PLACEMENT.CCC_COMPETENCY_RESULTS.ERROR"></div>'

            ].join('')

        };
    });

})();
