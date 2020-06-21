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

    angular.module('CCC.View.Home').directive('cccSubjectAreaSummarySequences', function () {

        return {

            restrict: 'E',

            scope: {
                subjectArea: '='
            },

            controller: [

                '$scope',
                '$q',
                'CurrentUserService',
                'CompetencyDisciplinesAPIService',
                'RulesCollegesService',
                'MMSubjectAreaClass',

                function ($scope, $q, CurrentUserService, CompetencyDisciplinesAPIService, RulesCollegesService, MMSubjectAreaClass) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    $scope.loading = false;
                    $scope.subjectAreaCollege = false;

                    $scope.mmapEquivalents = [];

                    $scope.sequencesWithCourses = [];

                    $scope.isMMSubjectArea = $scope.subjectArea instanceof MMSubjectAreaClass;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var loadSequences = function () {
                        return $scope.subjectArea.fetchSequences();
                    };

                    var loadMMAPEquivalents = function () {

                        $scope.loadingMMAPEquivalents = true;

                        return CompetencyDisciplinesAPIService.get($scope.subjectArea.competencyMapDiscipline).then(function (mmapEquivalents) {

                            $scope.mmapEquivalents = _.map(mmapEquivalents, function (equiv) {
                                return {
                                    id: equiv.code,
                                    title: equiv.mmapEquivalent
                                };
                            });

                            $scope.$broadcast('ccc-item-dropdown.setItems', $scope.mmapEquivalents, 'mmapEquivalentsDropdown');

                        }).finally(function () {
                            $scope.loadingMMAPEquivalents = false;
                        });
                    };

                    var loadCollege = function () {

                        return CurrentUserService.getCollegeBycccId($scope.subjectArea.collegeId).then(function (college) {
                            $scope.subjectAreaCollege = college;
                        });
                    };

                    var getValidSequences = function (sequences) {

                        return _.filter(sequences, function (sequence) {
                            return sequence.courses.length;
                        });
                    };

                    var loadSequenceData = function () {

                        $scope.loading = true;

                        var promises = [];

                        promises.push(loadSequences());

                        if (!$scope.isMMSubjectArea) {
                            promises.push(loadMMAPEquivalents());
                            promises.push(loadCollege());
                        }

                        $q.all(promises).finally(function () {

                            if (!$scope.isMMSubjectArea) {
                                $scope.sequencesWithCourses = getValidSequences($scope.subjectArea.sequences);
                            }

                            $scope.loading = false;
                        });
                    };

                    var initialize = function () {
                        loadSequenceData();
                    };


                    /*============= BEHAVIOR =============*/


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-subject-area-summary-sequences.update', function (e, subjectArea) {

                        $scope.subjectArea = subjectArea;
                        loadSequenceData();
                    });


                    /*============ INITIALIZATION ==============*/

                    initialize();
                }
            ],

            template: [

                '<div class="ccc-subject-area-summary-content">',
                    '<ccc-subject-area-sequence ng-if="!loading" subject-area="subjectArea" read-only="true"></ccc-subject-area-sequence>',
                '</div>',

                '<h3 class="margin-top-md margin-bottom-sm ccc-subject-area-summary-header" ng-if="::!isMMSubjectArea">',
                    '<span translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-SUMMARY.COURSE_MAPPINGS"></span>',
                '</h3>',

                '<ccc-content-loading-placeholder ng-if="!isMMSubjectArea && !loading && !sequencesWithCourses.length" no-results-info="true" hide-default-no-results-text="true">',
                    '<span translate="CCC_VIEW_HOME.CCC-SUBJECT-AREA-SUMMARY-SEQUENCES.NO_COURSES"></span>',
                '</ccc-content-loading-placeholder>',

                '<div ng-repeat="sequence in sequencesWithCourses track by (sequence.cb21Code + \'-\' + sequence.courseGroup)" ng-if="::!isMMSubjectArea">',
                    '<ccc-subject-area-course-summary cb21code="{{::sequence.cb21Code}}" ng-repeat="course in sequence.courses" course="course" subject-area="subjectArea"></ccc-subject-area-course-summary>',
                '</div>'

            ].join('')

        };

    });

})();
