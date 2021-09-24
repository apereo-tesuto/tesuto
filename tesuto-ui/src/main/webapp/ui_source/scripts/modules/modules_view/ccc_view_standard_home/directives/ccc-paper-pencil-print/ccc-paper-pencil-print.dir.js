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

    angular.module('CCC.View.Home').directive('cccPaperPencilPrint', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                'AssessmentsAPIService',

                function ($scope, AssessmentsAPIService) {

                    /*============ PRIVATE VARIABLES / METHODS =============*/

                    var DESIGNATED_GROUPS = [
                        {
                            id:'MATH',
                            title: 'CCC_VIEW_HOME.CCC-PAPER-PENCIL-PRINT.GROUP_MATH',
                            icon: 'ui/resources/images/icons/maths.png'
                        },
                        {
                            id:'ENGLISH',
                            title: 'CCC_VIEW_HOME.CCC-PAPER-PENCIL-PRINT.GROUP_ENGLISH',
                            icon: 'ui/resources/images/icons/book.png'
                        }
                    ];

                    var misCode = 'false';


                    /*============ MODEL =============*/

                    $scope.assessmentsLoading = true;

                    $scope.assessments = [];
                    $scope.paperAssessments = [];

                    $scope.assessmentGroups = [];
                    $scope.assessmentGroupsMap = {};

                    $scope.otherAssessments = [];


                    /*============ MODEL DEPENDENT METHODS =============*/

                    var getAssessmentGroups = function (assessmentsData) {

                        var groups = [];

                        _.each(assessmentsData.disciplines, function (subjectArea) {

                            if ($scope.assessmentGroupsMap[subjectArea]) {
                                groups.push($scope.assessmentGroupsMap[subjectArea].assessments);
                            }
                        });

                        // if it doesn't fit into one of the disciplien groups, then just send it to the other group
                        if (!groups.length) {
                            groups.push($scope.otherAssessments);
                        }

                        return groups;
                    };

                    var generateAssessmentData = function (assessments) {

                        $scope.assessments = assessments;

                        // first generate the groups data
                        $scope.assessmentGroupsMap = {};
                        $scope.assessmentGroups = _.map(DESIGNATED_GROUPS, function (groupInfo) {

                            $scope.assessmentGroupsMap[groupInfo.id] = {
                                info: groupInfo,
                                assessments: []
                            };

                            return $scope.assessmentGroupsMap[groupInfo.id];
                        });

                        // reset the other group
                        $scope.otherAssessments  = [];

                        // now loop through all the assessments and place them into the groups
                        $scope.paperAssessments = _.filter($scope.assessments, function (assessmentData) {
                            return assessmentData.paper === true;
                        });

                        _.each($scope.paperAssessments, function (assessmentData) {

                            // the assessment could be a part of multiple subject areas so we plan on getting an array of assessmentGroups
                            _.each(getAssessmentGroups(assessmentData), function (assessmentGroup) {
                                assessmentGroup.push(assessmentData);
                            });
                        });
                    };

                    var updateTestCenter = function (newTestCenter) {

                        misCode = newTestCenter.college.cccId;

                        $scope.assessmentsLoading = true;

                        $scope.assessmentGroups = [];

                        AssessmentsAPIService.getAssessmentsByTestLocationId(newTestCenter.id).then(function (assessmentsData) {

                            generateAssessmentData(assessmentsData);

                        }).finally(function () {
                            $scope.assessmentsLoading = false;
                        });
                    };


                    /*============ BEHAVIOR =============*/

                    $scope.getAssessmentPrintURL = function (assessment) {
                        return AssessmentsAPIService.getPrintURL(assessment, misCode);
                    };


                    /*============ LISTENERS =============*/

                    $scope.$on('ccc-locations-list.testCenterUpdated', function (e, newTestCenter) {
                        updateTestCenter(newTestCenter);
                    });
                }
            ],

            template: [

                '<p class="reading-width text-info"><span class="fa fa-info-circle" role="presentation" aria-hidden="true"></span> <span translate="CCC_VIEW_HOME.CCC-PAPER-PENCIL-PRINT.INFO"></span></p>',

                '<div class="row margin-bottom-double">',
                    '<div class="col-xs-12">',
                        '<ccc-locations-list list-style="primary" ccc-focusable></ccc-locations-list>',
                    '</div>',
                '</div>',

                '<ccc-content-loading-placeholder ng-if="assessmentsLoading || !assessmentsLoading && !paperAssessments.length" no-results-info="!assessmentsLoading && !paperAssessments.length" hide-default-no-results-text="true">',
                    '<span translate="CCC_VIEW_HOME.CCC-PAPER-PENCIL-PRINT.NO_ASSESSMENTS"></span>',
                '</ccc-content-loading-placeholder>',

                '<div ng-repeat="group in assessmentGroups track by group.info.id">',
                    '<div class="ccc-paper-pencil-group" ng-if="group.assessments.length">',
                        '<h2><img ng-src="{{::group.info.icon}}" aria-hidden="true"/> <span translate="{{::group.info.title}}"></span></h2>',
                        '<div class="ccc-paper-pencil-group-item" >{{assessment.title}}</div>',
                        '<ul class="nav nav-pills nav-stacked">',
                            '<li ng-repeat="assessment in group.assessments" class="ccc-list-item-big ng-scope">',
                                '<a ng-href="{{getAssessmentPrintURL(assessment)}}" target="_blank" class="btn btn-default college text-left ng-binding" role="button" ng-disabled="assessmentsLoading">',
                                    '{{assessment.title}}',
                                '</a>',
                            '</li>',
                        '</ul>',
                    '</div>',
                '</div>',

                '<div class="ccc-paper-pencil-group" ng-if="!assessmentsLoading && otherAssessments.length">',
                    '<h2>',
                        '<span ng-if="(paperAssessments.length - otherAssessments.length) > 0" translate="CCC_VIEW_HOME.CCC-PAPER-PENCIL-PRINT.OTHER_ASSESSMENTS"></span>',
                        '<span ng-if="(paperAssessments.length - otherAssessments.length) === 0" translate="CCC_VIEW_HOME.CCC-PAPER-PENCIL-PRINT.OTHER_ASSESSMENTS_ONLY"></span>',
                    '</h2>',
                    '<ul class="nav nav-pills nav-stacked">',
                        '<li ng-repeat="assessment in otherAssessments" class="ccc-list-item-big ng-scope">',
                            '<a ng-href="{{getAssessmentPrintURL(assessment)}}" target="_blank" class="btn btn-default college text-left ng-binding" role="button" ng-disabled="assessmentsLoading">',
                                '{{assessment.title}}',
                            '</a>',
                        '</li>',
                    '</ul>',
                '</div>'

            ].join('')

        };

    });

})();





