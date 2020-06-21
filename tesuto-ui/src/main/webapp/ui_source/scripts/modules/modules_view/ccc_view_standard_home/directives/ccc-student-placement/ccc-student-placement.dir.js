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

    angular.module('CCC.View.Home').directive('cccStudentPlacement', function () {

        return {

            restrict: 'E',

            scope: {
                student: '=',
                initialCollegeId: '='
            },

            controller: [

                '$scope',
                '$q',
                '$translate',
                'CurrentUserService',
                'StudentPlacementService',
                'RulesCollegesService',
                'ModalService',
                'PlacementRequestAPIService',
                'NotificationService',

                function ($scope, $q, $translate, CurrentUserService, StudentPlacementService, RulesCollegesService, ModalService, PlacementRequestAPIService, NotificationService) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/

                    var studentColleges = [];
                    _.each($scope.student.collegeStatuses, function(val, key) {
                        studentColleges.push(val);
                    });


                    /*============= MODEL =============*/

                    $scope.subjectAreas = [];

                    $scope.showHistory = false;

                    $scope.selectedCollegeId = false;

                    $scope.studentHasColleges = studentColleges.length > 0;

                    $scope.loading = true;

                    $scope.currentPlacements = [];
                    $scope.pastPlacements = [];

                    $scope.componentGroups = []; // will be a nested list of components grouped by subject area

                    $scope.canGeneratePlacement = CurrentUserService.hasPermission('CREATE_PLACEMENT_DECISION');

                    $scope.generatingPlacements = false;




                    /*============= MODEL DEPENDENT METHODS =============*/

                    // need to gather currentPlacements, pastPlacements, and components grouped by subject area
                    var parsePlacementData = function (placementData) {

                        // gather the placement objects
                        $scope.currentPlacements = _.filter(placementData.placements, function (placement) {
                            // we could use one loop to push those that don't have this boolean into another array.. worth it?
                            return placement.assigned === true;
                        });
                        $scope.pastPlacements = _.filter(placementData.placements, function (placement) {
                            return placement.assigned !== true;
                        });

                        // sort them
                        $scope.currentPlacements = _.sortBy($scope.currentPlacements, function (placement) {
                            return placement.createdOn;
                        });
                        $scope.pastPlacements = _.sortBy($scope.pastPlacements, function (placement) {
                            return placement.createdOn;
                        });

                        // group the components
                        var subjectAreaTitleMap = {};
                        var groupMap = _.groupBy(placementData.components, function (component) {
                            subjectAreaTitleMap[component.versionedSubjectAreaViewDto.disciplineId] = component.versionedSubjectAreaViewDto.title;
                            return component.versionedSubjectAreaViewDto.disciplineId;
                        });

                        // map the groups to groups with names
                        $scope.componentGroups = _.map(groupMap, function (value, key) {
                            return {
                                group: subjectAreaTitleMap[key],
                                components: value
                            };
                        });

                        // sort the groups by name
                        $scope.componentGroups = _.sortBy($scope.componentGroups, function (group) {
                            return group.group.toLowerCase();
                        });
                    };

                    var updatePlacementData = function () {

                        $scope.loading = true;
                        $scope.showHistory = false;

                        return StudentPlacementService.getPlacementDataForStudentByCollege($scope.student.cccId, $scope.selectedCollegeId)
                            .then(

                                parsePlacementData,

                                function () {
                                    // let's clear out the data on error so we don't show data from a previously successful showing
                                    parsePlacementData({
                                        placements: [],
                                        components: []
                                    });
                                }
                            )
                            .finally(function() {
                                $scope.loading = false;
                            });
                    };

                    var initialize = function () {

                        var selectedCollege = _.find(CurrentUserService.getUser().colleges, function (college) {
                            return college.cccId === $scope.initialCollegeId;
                        });

                        if (selectedCollege !== undefined) {
                            $scope.selectedCollegeId = selectedCollege.cccId;
                        }
                    };


                    /*============= BEHAVIOR =============*/

                    $scope.toggleHistory = function () {
                        $scope.showHistory = !$scope.showHistory;
                    };

                    $scope.done = function () {
                        $scope.$emit('ccc-student-placement.done');
                    };

                    $scope.regeneratePlacement = function () {

                        var buttonConfigs = {
                            cancel: {
                                title: 'Cancel',
                                btnClass: 'btn-default'
                            },
                            okay: {
                                title: $translate.instant('CCC_VIEW_HOME.CCC-STUDENT-PLACEMENT.GENERATE_PLACEMENT_CONFIRM'),
                                btnClass: 'btn-primary',
                                btnIcon: 'fa fa-refresh'
                            }
                        };

                        var placementModal = ModalService.openConfirmModal({
                            title: $translate.instant('CCC_VIEW_HOME.CCC-STUDENT-PLACEMENT.GENERATE_PLACEMENT_COLLEGE', {collegeName: $scope.collegeName}),
                            message: 'CCC_VIEW_HOME.CCC-STUDENT-PLACEMENT.GENERATE_PLACEMENT_MESSAGE',
                            buttonConfigs: buttonConfigs
                        });

                        placementModal.result.then(function () {

                            $scope.generatingPlacements = true;

                            PlacementRequestAPIService.triggerPlacementsForCollegeAndStudent($scope.selectedCollegeId, $scope.student.cccId).then(function () {

                                NotificationService.open({
                                    icon: 'fa fa-thumbs-up',
                                    title: ' Success.',
                                    message: ' ' + $translate.instant('CCC_VIEW_HOME.CCC-STUDENT-PLACEMENT.GENERATE_PLACEMENT_SUCCESS')
                                },
                                {
                                    delay: 5000,
                                    type: "success",
                                    allow_dismiss: true
                                });

                            }).finally(function () {
                                $scope.generatingPlacements = false;
                            });
                        });
                    };


                    /*============= LISTENERS =============*/

                    $scope.$on('ccc-placement-component-groups.assessmentComponentClicked', function (e, component) {
                        $scope.$emit('ccc-student-placement.assessmentComponentClicked', component);
                    });

                    $scope.$on('ccc-placement-card.assessmentComponentClicked', function (e, component) {
                        $scope.$emit('ccc-student-placement.assessmentComponentClicked', component);
                    });

                    // this will fire on initialization and when a selection changes
                    $scope.$on('ccc-college-selector-for-student-dropdown.collegeSelected', function (e, selectedCollege) {

                        $scope.selectedCollegeId = selectedCollege.id;
                        $scope.collegeName = selectedCollege.name;

                        updatePlacementData();
                    });


                    /*============ INITIALIZATION ============*/

                    initialize();
                }
            ],

            template: [

                '<div class="row" ng-if="!studentHasColleges">',
                    '<div class="col-xs-12">',
                        '<ccc-info>',
                            '<strong translate="CCC_VIEW_HOME.CCC-STUDENT-PLACEMENT.NO_COLLEGE.TITLE"></strong> ',
                            '<span translate="CCC_VIEW_HOME.CCC-STUDENT-PLACEMENT.NO_COLLEGE.MESSAGE"></span>',
                        '</ccc-info>',
                        '<div>',
                            '<button class="btn btn-primary btn-icon-left"  ng-click="done()"><i class="fa fa-chevron-left" aria-hidden="true"></i> Back to Student Profile</button>',
                        '</div>',
                    '</div>',
                '</div>',

                '<div ng-if="studentHasColleges">',

                    '<div class="row">',

                        '<div class="col-xs-12">',

                            '<ccc-student-user user="student" class="ccc-user ccc-student-user"></ccc-student-user>',

                            '<div class="well well-sm action-bar">',
                                '<div class="actions">',
                                    '<ccc-college-selector-for-student-dropdown student="student" selected-college-id="selectedCollegeId"></ccc-college-selector-for-student-dropdown>',
                                    '<button ccc-autofocus ccc-focusable class="btn btn-primary" ng-click="regeneratePlacement()" ng-disabled="selectedCollegeId === undefined" ng-if="canGeneratePlacement">',
                                        '<i class="fa fa-refresh" ng-class="{\'fa-spin\': generatingPlacements}"></i>',
                                        ' <span translate="CCC_VIEW_HOME.CCC-STUDENT-PLACEMENT.GENERATE_PLACEMENT"></span>',
                                    '</button>',
                                '</div>',
                            '</div>',

                        '</div>',

                    '</div>',

                    '<div class="row">',

                        '<div class="col-xs-12">',

                            '<h3 class="title" translate="CCC_VIEW_HOME.CCC-STUDENT-PLACEMENT.TITLE.CURRENT_PLACEMENT"></h3>',

                            '<ccc-content-loading-placeholder class="margin-bottom-sm" ng-if="loading || !loading && !currentPlacements.length" no-results-info="!loading && !currentPlacements.length" hide-default-no-results-text="true">',
                                '<div><strong><i class="fa fa-info-circle" aria-hidden="true"></i> <span translate="CCC_VIEW_HOME.CCC-STUDENT-PLACEMENT.NO_CURRENT_PLACEMENTS_STRONG"></span></strong> <span translate="CCC_VIEW_HOME.CCC-STUDENT-PLACEMENT.NO_CURRENT_PLACEMENTS"></span></div>',
                            '</ccc-content-loading-placeholder>',

                            '<div ng-if="!loading" class="placement-cards">',
                                '<ccc-placement-card placement="placement" ng-repeat="placement in currentPlacements track by $index" class="active"></ccc-placement-card>',
                            '</div>',

                        '</div>',

                    '</div>',

                    '<div class="row">',

                        '<div class="col-xs-12">',

                            '<div>',
                                '<hr>',
                                '<button class="btn btn-default" ng-click="toggleHistory()">',
                                    '<span ng-if="!showHistory"><span translate="CCC_VIEW_HOME.CCC-STUDENT-PLACEMENT.SHOW_HISTORY"></span> <i class="fa fa-chevron-down" aria-hidden="true"></i></span>',
                                    '<span ng-if="showHistory"><span translate="CCC_VIEW_HOME.CCC-STUDENT-PLACEMENT.HIDE_HISTORY"></span> <i class="fa fa-chevron-up" aria-hidden="true"></i></span>',
                                '</button>',
                            '</div>',

                            '<div ng-if="showHistory">',

                                '<h3 class="title margin-top-md" translate="CCC_VIEW_HOME.CCC-STUDENT-PLACEMENT.TITLE.PLACEMENT_HISTORY"></h3>',

                                '<ccc-content-loading-placeholder class="margin-bottom-sm" ng-if="loading || !loading && !pastPlacements.length" no-results-info="!loading && !pastPlacements.length" hide-default-no-results-text="true">',
                                    '<div><span translate="CCC_VIEW_HOME.CCC-STUDENT-PLACEMENT.NO_HISTORY_PLACEMENTS"></span></div>',
                                '</ccc-content-loading-placeholder>',

                                '<div ng-if="!loading" class="placement-cards">',
                                    '<ccc-placement-card placement="placement" ng-repeat="placement in pastPlacements track by $index"></ccc-placement-card>',
                                '</div>',

                                '<h3 class="title margin-top-md" translate="CCC_VIEW_HOME.CCC-STUDENT-PLACEMENT.TITLE.PLACEMENT_COMPONENTS"></h3>',

                                '<ccc-content-loading-placeholder class="margin-bottom-sm" ng-if="loading || !loading && !componentGroups.length" no-results-info="!loading && !componentGroups.length" hide-default-no-results-text="true">',
                                    '<div><span translate="CCC_VIEW_HOME.CCC-STUDENT-PLACEMENT.NO_COMPONENTS"></span></div>',
                                '</ccc-content-loading-placeholder>',

                                '<ccc-placement-component-groups component-groups="componentGroups" ng-if="componentGroups.length && !loading"></ccc-placement-component-groups>',

                            '</div>',

                        '</div>',

                    '</div>',

                '</div>'

            ].join('')

        };

    });

})();
