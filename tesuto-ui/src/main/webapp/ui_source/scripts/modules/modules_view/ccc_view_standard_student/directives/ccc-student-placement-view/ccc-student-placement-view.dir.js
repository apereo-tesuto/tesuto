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

    angular.module('CCC.View.Student').directive('cccStudentPlacementView', function () {

        return {

            restrict: 'E',

            scope: {
                college: '='
            },

            controller: [

                '$scope',
                '$filter',
                '$q',
                'CurrentStudentService',
                'PlacementCollegesAPIService',
                'SubjectAreasAPIService',
                'CollegeSubjectAreasAPIService',
                'CCCUtils',
                'ASSESSMENTS_DISABLED',

                function ($scope, $filter, $q, CurrentStudentService, PlacementCollegesAPIService, SubjectAreasAPIService, CollegeSubjectAreasAPIService, CCCUtils, ASSESSMENTS_DISABLED) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/


                    /*============ MODEL ============*/

                    $scope.loading = true;

                    $scope.subjectAreaGroups = [];

                    $scope.placementExists = false;


                    /*============= MODEL DEPENDENT METHODS ============*/

                    var parsePlacementData = function (placementData) {

                        // sort them
                        var placements = _.sortBy(placementData, function (placement) {
                            return placement.createdOn;
                        });

                        // format the dates and set the college specific messaging
                        _.each(placements, function (placement) {

                            if (placement.courses.length) {
                                placement.transferLevelMsg = placement.sequenceInfo.explanation || '';
                            }

                            placement.dateFormatted = $filter('date')(placement.createdOn, 'MMMM d, yyyy');

                            if (placement._assessmentData) {
                                placement._assessmentDateFormatted = $filter('date')(placement._assessmentData.assessmentCompletedDate, 'MMMM d, yyyy');
                            }

                        });

                        // sort the courses by highest
                        _.each(placements, function (placement) {

                            placement.courses = _.sortBy(placement.courses, function (course) {
                                return course.number;
                            });

                            placement.courses.reverse();
                        });

                        return placements;
                    };

                    var getStudentPlacements = function () {

                        return PlacementCollegesAPIService.getStudentPlacementByCollege($scope.college.cccId).then(function (placements) {
                            return placements;
                        });
                    };

                    var getSubjectAreas = function () {

                        return CollegeSubjectAreasAPIService.getSubjectAreasByCollegeIdForStudent($scope.college.cccId).then(function (subjectAreas) {

                            return _.map(subjectAreas, function (subjectArea) {
                                if (ASSESSMENTS_DISABLED) {
                                    return CCCUtils.coerce('SubjectAreaClass', subjectArea);
                                    // return CCCUtils.coerce('MMSubjectAreaClass', subjectArea);
                                } else {
                                    return CCCUtils.coerce('SubjectAreaClass', subjectArea);
                                }
                            });
                        });
                    };

                    var groupPlacementData = function (placements, subjectAreas) {

                        // map the groups to groups with names
                        var subjectAreaGroupMap = {};
                        var subjectAreaGroups = _.map(subjectAreas, function (subjectArea) {

                            if (!subjectArea.noPlacementMessage) {
                                subjectArea.noPlacementMessage = 'No placement has been generated for this Subject Area. If you have questions, please see your counselor.';
                            }

                            var subjectAreaGroup = {
                                group: subjectArea,
                                placements: []
                            };

                            subjectAreaGroupMap[subjectArea.disciplineId] = subjectAreaGroup;

                            return subjectAreaGroup;
                        });

                        // put the placements in their proper bucket
                        _.each(placements, function (placement) {

                            subjectAreaGroupMap[placement.disciplineId].placements.push(placement);

                            $scope.placementExists = true;

                            // the placement generated for the student may have been generated by a different version of the discipline
                            // so we use the title that was more familiar with the student when their placment was generated
                            if (placement.publishedTitle) {
                                subjectAreaGroupMap[placement.disciplineId].title = placement.publishedTitle;
                            }
                        });

                        // sort the subject area groups by name
                        subjectAreaGroups = _.sortBy(subjectAreaGroups, function (subjectAreaGroup) {
                            return subjectAreaGroup.group.title.toLowerCase();
                        });

                        return subjectAreaGroups;
                    };

                    var attachPlacementAssessmentData = function (placements) {

                        var placementAssessmentPromises = [];

                        _.each(placements, function (placement) {

                            placement._assessmentData = false;

                            var placementAssessmentPromise = PlacementCollegesAPIService.getPlacementAssessmentComponentMetaData($scope.college.cccId, placement.id).then(function (assessmentData) {

                                // an empty string means there is no assessment component... assume that the placement was generated by mm.
                                placement._assessmentData = assessmentData === "" ? false : assessmentData;
                            });

                            placementAssessmentPromises.push(placementAssessmentPromise);
                        });

                        return $q.all(placementAssessmentPromises).then(function () {
                            return placements;
                        });
                    };

                    var initialize = function () {

                        $scope.loading = true;

                        return getSubjectAreas().then(function (subjectAreas) {

                            return getStudentPlacements().then(function (placements) {

                                return attachPlacementAssessmentData(placements).then(function (placements) {

                                    var parsedPlacements = parsePlacementData(placements);
                                    $scope.subjectAreaGroups = groupPlacementData(parsedPlacements, subjectAreas);

                                });
                            });

                        }).finally(function () {
                            $scope.loading = false;
                        });
                    };



                    /*============ BEHAVIOR ============*/

                    $scope.viewAssessment = function (placement) {
                        $scope.$emit('ccc-student-placement-view.viewAssessment', $scope.college.cccId, placement.id);
                    };


                    /*============ LISTENERS ============*/

                    /*============ INITIALIZATION ============*/

                    initialize();
                }
            ],

            template: [

                '<div class="college-banner">',
                    '<div class="college-logo">',
                        '<div class="logomark">',
                            '<ccc-image image-url="{{college.iconUrl}}" default-url="ui/resources/images/ccc-logomark-small.png"></ccc-image>',
                        '</div>',
                        '<div class="text-label">',
                            '<h2 id="collegeLogoName">{{::college.name}}</h2>',
                        '</div>',
                    '</div>',
                '</div>',

                '<ccc-content-loading-placeholder class="margin-bottom-sm" ng-if="loading || !loading && !placementExists" no-results-info="!loading && !placementExists" hide-default-no-results-text="true">',
                    '<div><strong><i class="fa fa-info-circle" aria-hidden="true"></i> <span translate="Your placement is not available yet."></span></strong> <span translate="Contact your district administrator if you think there should be a placement for this college."></span></div>',
                '</ccc-content-loading-placeholder>',

                '<div ng-repeat="subjectArea in subjectAreaGroups track by $index" class="discipline">',

                    '<div ng-if="subjectArea.placements.length" ng-repeat="placement in subjectArea.placements">',

                        '<div class="discipline-header">',
                            '<h2 class="title">{{::subjectArea.group.title}}</h2>',
                        '</div>',

                        '<div class="header-details placement-date">',
                            '<span>Placement Date: </span>',
                            '<span>{{::placement.dateFormatted}}</span>',
                        '</div>',

                        '<div class="discipline-body">',
                            '<div class="row">',
                                '<div class="col col-md-7">',
                                    '<div class="placements">',
                                        '<div ng-repeat="course in placement.courses track by $index" class="placement-card">',
                                            '<div class="course">',
                                                '<h3 class="common-name">{{::course.name}}</h3>',
                                                '<h4 class="course-name">{{::course.subject}} {{::course.number}}</h4>',
                                                '<p class="description">{{::course.description}}</p>',
                                            '</div>',
                                        '</div>',
                                    '</div>',
                                '</div>',
                                '<div class="col col-md-5">',
                                    '<div class="placement-explanation">',
                                        '<div class="assessment-attempt" ng-if="::placement._assessmentData">',
                                            '<p>This placement is based in part on the assessment taken at {{::college.name}} on {{::placement._assessmentDateFormatted}}.</p>',
                                            '<p><a href="#" ng-click="viewAssessment(placement)">View assessment results <span class="icon fa fa-chevron-circle-right" role="presentation" aria-hidden="true"></span></a></p>',
                                        '</div>',
                                        '<div class="assessment-attempt" ng-if="::!placement._assessmentData">',
                                            '<p>This placement is based on multiple factors made available to Tesuto. Please see a counselor if you have any questions about your placement.</p>',
                                        '</div>',
                                        '<div class="college-specific">',
                                            '<p>{{::placement.transferLevelMsg}}</p>',
                                        '</div>',
                                    '</div>',
                                '</div>',
                            '</div>',
                        '</div>',

                    '</div>',
                '</div>',

            ].join('')

        };

    });

})();
