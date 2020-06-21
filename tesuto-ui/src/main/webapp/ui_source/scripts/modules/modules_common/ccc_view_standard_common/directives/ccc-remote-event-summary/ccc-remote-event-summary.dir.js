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

    angular.module('CCC.View.Common').directive('cccRemoteEventSummary', function () {

        return {

            restrict: 'E',

            scope: {
                remoteEvent: '=',
                actions: '=',
                isDisabled: '=?',
                viewInstructions: '=?'
            },

            controller: [

                '$scope',
                '$timeout',
                'Moment',
                'UtilsService',

                function ($scope, $timeout, Moment, UtilsService) {

                    /*============ PRIVATE VARIABLES ============*/


                    /*============ MODEL ============*/

                    $scope.LIST_SIZE_LARGE = 10;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var updateDisplayModel = function () {

                        var startMoment = new Moment($scope.remoteEvent.startDate);
                        var endMoment = new Moment($scope.remoteEvent.endDate);

                        $scope.startMonth = startMoment.format('MMM');
                        $scope.startDay = startMoment.format('D');
                        $scope.startDayText = startMoment.format('dddd');

                        $scope.endMonth = endMoment.format('MMM');
                        $scope.endDay = endMoment.format('D');
                        $scope.endDayText = endMoment.format('dddd');

                        $scope.phoneFormatted = UtilsService.formatTenDigitPhone($scope.remoteEvent.proctorPhone);
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.editStudents = function () {
                        $scope.$emit('ccc-remote-event-summary.editStudents');
                    };

                    $scope.done = function () {
                        $scope.$emit('ccc-remote-event-summary.done');
                    };


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-remote-event-summary.refresh', function (e, remoteEvent) {

                        if (remoteEvent) {
                            $scope.remoteEvent = remoteEvent;
                        }

                        // if we ever allow editing of collges we will need to change flag logic to allow updated meta data from assessments and locations to update
                        updateDisplayModel();
                    });


                    /*============ INITIALIZATION ============*/

                    updateDisplayModel();
                }
            ],

            template: [

                '<h2 class="title">{{remoteEvent.name}}</h2>',

                '<div class="test-event-info">',
                    '<div class="event-header well" tabindex="0" ccc-autofocus>',
                        '<div class="row event-header-details">',
                            '<div class="col col-sm-6 col-md-3">',
                                '<section class="dates">',
                                    '<h3 class="title" translate="CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.DATE"></h3>',
                                    '<div class="date start">',
                                        '<h4 class="title sr-only" translate="CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.START_DATE"></h4>',
                                        '<div class="date-block">',
                                            '<span class="month">{{startMonth}}</span>',
                                            '<span class="day">{{startDay}}</span>',
                                            '<span class="weekday">{{startDayText}}</span>',
                                        '</div>',
                                    '</div>',
                                    '<div class="date end">',
                                        '<h4 class="title sr-only" translate="CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.END_DATE"></h4>',
                                        '<div class="date-block">',
                                            '<span class="month">{{endMonth}}</span>',
                                            '<span class="day">{{endDay}}</span>',
                                            '<span class="weekday">{{endDayText}}</span>',
                                        '</div>',
                                    '</div>',
                                '</section>',
                            '</div>',
                            '<div class="col col-sm-6 col-md-3">',
                                '<section class="location">',
                                    '<h3 class="title" translate="CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.LOCATION"></h3>',

                                    '<ccc-content-loading-placeholder ng-if="remoteEvent.metaData.locationLoading"></ccc-content-loading-placeholder>',

                                    '<div ng-if="!remoteEvent.metaData.locationLoading">',

                                        '<div class="college">',
                                            '<span class="fa-stack" role="presentation" aria-hidden="true">',
                                                '<i class="fa fa-square fa-stack-2x"></i>',
                                                '<i class="fa fa-university fa-stack-1x fa-inverse"></i>',
                                            '</span>',
                                            '{{remoteEvent.metaData.college.name}}',
                                        '</div>',
                                        '<div class="test-location">',
                                            '<span class="fa-stack" role="presentation" aria-hidden="true">',
                                                '<i class="fa fa-square fa-stack-2x"></i>',
                                                '<i class="fa fa-map-marker fa-stack-1x fa-inverse"></i>',
                                            '</span>',
                                            '{{remoteEvent.metaData.location.name}}',
                                        '</div>',

                                    '</div>',

                                '</section>',
                            '</div>',
                            '<div class="col col-sm-6 col-md-3">',
                                '<section class="proctor">',
                                    '<h3 class="title" translate="CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.PROCTOR"></h3>',
                                    '<div class="name">',
                                        '<span class="fa-stack" role="presentation" aria-hidden="true">',
                                            '<i class="fa fa-square fa-stack-2x"></i>',
                                            '<i class="fa fa-user fa-stack-1x fa-inverse"></i>',
                                        '</span>',
                                        '{{remoteEvent.proctorFirstName}} {{remoteEvent.proctorLastName}}',
                                    '</div>',
                                    '<div class="email">',
                                        '<span class="fa-stack" role="presentation" aria-hidden="true">',
                                            '<i class="fa fa-square fa-stack-2x"></i>',
                                            '<i class="fa fa-envelope fa-stack-1x fa-inverse"></i>',
                                        '</span>',
                                        '<a href="mailto:{{remoteEvent.proctorEmail}}">{{remoteEvent.proctorEmail}}</a>',
                                    '</div>',
                                    '<div class="phone">',
                                        '<span class="fa-stack" role="presentation" aria-hidden="true">',
                                            '<i class="fa fa-square fa-stack-2x"></i>',
                                            '<i class="fa fa-phone fa-stack-1x fa-inverse"></i>',
                                        '</span>',
                                        '<span class="number">{{phoneFormatted}}</span>',
                                    '</div>',
                                '</section>',
                            '</div>',
                            '<div class="col col-sm-6 col-md-3">',
                                '<section class="assessments">',

                                    '<h3 class="title" translate="CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.ASSESSMENTS"></h3>',

                                    '<ccc-content-loading-placeholder ng-if="remoteEvent.metaData.assessmentsLoading"></ccc-content-loading-placeholder>',

                                    '<ul class="assessment-list" ng-if="!remoteEvent.metaData.assessmentsLoading">',
                                        '<li ng-repeat="assessment in remoteEvent.metaData.assessments track by $index">',
                                            '<span class="fa-stack" role="presentation" aria-hidden="true">',
                                                '<i class="fa fa-square fa-stack-2x"></i>',
                                                '<i class="fa fa-pencil-square-o fa-stack-1x fa-inverse"></i>',
                                            '</span>',
                                            '{{::assessment.title}}',
                                        '</li>',
                                    '</ul>',

                                '</section>',
                            '</div>',
                        '</div>',

                        '<div class="actions">',

                            '<button ng-repeat="action in actions" ng-click="action.onClick(remoteEvent)" class="btn btn-primary" ccc-focusable ng-disabled="isDisabled">',
                                '<span class="fa {{::action.icon}}" role="presentation" aria-hidden="true"></span> ',
                                '<span translate="{{::action.title}}"></span>',
                            '</button>',

                        '</div>',

                    '</div>',

                    '<div ng-if="viewInstructions" class="row">',
                        '<div class="col col-md-6">',
                            '<div class="proctor-instructions">',
                                '<h3 translate="CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.INSTRUCTIONS.TITLE"></h3>',
                                '<span translate="CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.INSTRUCTIONS.BODY"></span>',
                            '</div>',
                        '</div>',
                    '</div>',

                    '<div class="student-list">',

                        '<h3 class="title" ng-if="students.length <= LIST_SIZE_LARGE"><span translate="CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.STUDENTS"></span> ({{remoteEvent.students.length}})</h3>',

                        '<div class="row" ng-if="students.length > LIST_SIZE_LARGE">',
                            '<div class="col-md-6 text-left">',
                                '<button class="btn btn-primary btn-full-width-when-small btn-submit-button margin-bottom-sm" ccc-focusable ng-click="done()" ng-disabled="isDisabled">',
                                    '<i class="fa fa-check" aria-hidden="true"></i> ',
                                    '<span translate="CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.DONE"></span>',
                                '</button>',
                            '</div>',
                            '<div class="col-md-6 text-right">',
                                '<h3 class="title"><span translate="CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.STUDENTS"></span> ({{remoteEvent.students.length}})</h3>',
                            '</div>',
                        '</div>',

                        '<div class="row margin-bottom-xs">',
                            '<div class="col-sm-6">',
                                '<h3><span translate="CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.STUDENTS"></span></h3>',
                            '</div>',
                            '<div class="col-sm-6 text-right">',
                                '<button class="btn btn-primary btn-full-width-when-small" ng-click="editStudents()" ng-disabled="loading  || isDisabled" ccc-focusable translate="CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.EDIT_STUDENTS"></button>',
                            '</div>',
                        '</div>',

                        '<ccc-content-loading-placeholder class="margin-bottom-sm" ng-if="(!remoteEvent.metaData.students.length || remoteEvent.metaData.studentsLoading) && !remoteEvent.metaData.hasStudentsLoadingError" no-results-info="!remoteEvent.metaData.studentsLoading && !remoteEvent.metaData.students.length" hide-default-no-results-text="true">',
                            '<div><span translate="CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.NO_STUDENTS_YET"></span> <a href="#" class="btn-link" ng-click="editStudents()" translate="CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.NO_STUDENTS_YET_ADD_NOW"></a></div>',
                        '</ccc-content-loading-placeholder>',

                        '<ccc-content-loading-placeholder class="margin-bottom-sm" ng-if="(!remoteEvent.metaData.students.length || remoteEvent.metaData.studentsLoading) && remoteEvent.metaData.hasStudentsLoadingError" no-results="!remoteEvent.metaData.studentsLoading && !remoteEvent.metaData.students.length" hide-default-no-results-text="true">',
                            '<div><span translate="CCC_VIEW_STANDARD_COMMON.CCC-REMOTE-EVENT-SUMMARY.STUDENTS_ERROR"></span></div>',
                        '</ccc-content-loading-placeholder>',

                        '<ccc-student-card-list ng-if="!remoteEvent.metaData.studentsLoading && remoteEvent.metaData.students.length" students="remoteEvent.metaData.students" is-disabled="loading  || isDisabled"></ccc-student-card-list>',

                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();
