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

    angular.module('CCC.View.Common').directive('cccPlacementCourseDetails', function () {

        return {

            restrict: 'E',

            scope: {
                placementData: '=',
                isStudent: '=?'
            },

            controller: [

                '$scope',
                '$element',
                'Moment',
                '$translate',

                function ($scope, $element, Moment, $translate) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    var MAX_COURSE_DESCRIPTION_LENTH = 300;

                    var getSubjectAreaCourses = function (subjectArea) {

                        var courses = subjectArea.placementDecision['courseViewDto'];

                        _.each(courses, function (course) {

                            course.hideDescription = false;

                            if (course.description && course.description.length > MAX_COURSE_DESCRIPTION_LENTH) {
                                course.descriptionTruncated = course.description.substr(0, MAX_COURSE_DESCRIPTION_LENTH) + '...';
                                course.hideDescription = true;
                            }
                        });

                        return courses;
                    };

                    var getSubjectAreaExplanation = function (subjectArea) {
                        if (subjectArea.placementDecision.sequenceInfo.explanation && $.trim(subjectArea.placementDecision.sequenceInfo.explanation)) {
                            return subjectArea.placementDecision.sequenceInfo.explanation;
                        } else {
                            return $translate.instant('CCC_VIEW_STANDARD_COMMON.CCC-PLACEMENT-COURSE-DETAILS.DEFAULT_SEQUENCE_PLACEMENT_EXPLANATION');
                        }
                    };

                    var getSubjectAreaTitle = function (subjectArea) {
                        return subjectArea.placementDecision.disciplineViewDto.title;
                    };


                    /*============ MODEL ============*/

                    $scope.isStudent = $scope.isStudent || false;

                    $scope.subjectAreas = _.map($scope.placementData, function (subjectArea) {

                        // we are normalizing this a bit
                        return {
                            placementDate: subjectArea.placementDate,
                            placementDateFormatted: new Moment(subjectArea.placementDate).format("MMMM D, YYYY"),
                            courses: getSubjectAreaCourses(subjectArea),
                            explanation: getSubjectAreaExplanation(subjectArea),
                            title: getSubjectAreaTitle(subjectArea),
                            showCourses: subjectArea.placementDecision.sequenceInfo ? subjectArea.placementDecision.sequenceInfo.showCourses : false,
                            showStudent: subjectArea.placementDecision.sequenceInfo ? subjectArea.placementDecision.sequenceInfo.showStudent : false
                        };
                    });


                    /*============= MODEL DEPENDENT METHODS ============*/

                    var initialize = function () {
                        if ($scope.isStudent) {
                            $element.addClass('student-report');
                        }
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.toggleClassDescription = function (subjectAreaClass) {
                    	subjectAreaClass.hideDescription = !subjectAreaClass.hideDescription;
                    };


                    /*============ LISTENERS ============*/


                    /*============ INITIALIZATION ============*/

                    initialize();
                }
            ],

            template: [

                '<div class="subject-area" ng-repeat="subjectArea in subjectAreas">',

                    '<h3 class="subject-area-title">{{subjectArea.title}}</h3>',

                    '<div class="placement-date">',
                        '<span>Placement Date:</span> ',
                        '<span>{{subjectArea.placementDateFormatted}}</span>',
                    '</div>',

                    '<div class="placement-card" ng-repeat="course in subjectArea.courses" ng-if="subjectArea.showStudent || !isStudent">',
                        '<div ng-if="isStudent" class="icon"></div>',
                        '<div class="course">',
                            '<h3 class="common-name">{{::course.name}}</h3>',
                            '<h4 class="course-name">{{::course.subject}} {{::course.number}}</h4>',
                            '<p class="description" ng-if="isStudent && course.hideDescription">',
                                '{{::course.descriptionTruncated}}',
                            '</p>',
                            '<p class="description" ng-if="isStudent &&!course.hideDescription">',
                                '{{::course.description}}',
                            '</p>',
                            '<a class="btn btn-link" ng-if="isStudent && course.descriptionTruncated" ng-click="toggleClassDescription(course)">',
                                '<span ng-if="course.hideDescription">Show more <span class="icon fa fa-angle-double-down"></span></span>',
                                '<span ng-if="!course.hideDescription">Show less <span class="icon fa fa-angle-double-up"></span></span>',
                            '</a>',
                        '</div>',
                    '</div>',

                    '<div class="placement-explanation" ng-if="isStudent">',
                        '<h3 ng-if="subjectArea.showStudent" translate="CCC_VIEW_STANDARD_COMMON.CCC-PLACEMENT-COURSE-DETAILS.PLACEMENT_REASON_TITLE_SHOW_COURSES"></h3>',
                        '<h3 ng-if="!subjectArea.showStudent" translate="CCC_VIEW_STANDARD_COMMON.CCC-PLACEMENT-COURSE-DETAILS.PLACEMENT_REASON_TITLE_HIDE_COURSES"></h3>',
                        '<p>{{::subjectArea.explanation}}</p>',
                    '</div>',

                '</div>'

            ].join('')

        };

    });

})();
