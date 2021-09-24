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

    angular.module('CCC.View.Home').directive('cccStudentProfile', function () {

        return {

            restrict: 'E',

            scope: {
                student: '=',
                location: '='
            },

            controller: [

                '$scope',
                '$element',
                '$filter',
                '$state',
                'LocationService',
                'CurrentUserService',
                'ActivationSearchAPIService',
                'PrintActivationService',
                'ACTIVATION_STATUS_MAPS',
                'ASSESSMENTS_DISABLED',

                function ($scope, $element, $filter, $state, LocationService, CurrentUserService, ActivationSearchAPIService, PrintActivationService, ACTIVATION_STATUS_MAPS, ASSESSMENTS_DISABLED) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/

                    var testCenterMap = {};

                    var isUserAffiliated = function () {
                        // Checks if the currentUser is affiliated to at least 1 college the student applied/enrolled to.
                        var isAffiliated = false;

                        var currentUserColleges = _.map(CurrentUserService.getUser().colleges, function (college) {
                            return college.cccId;
                        });

                        var studentColleges = _.map($scope.student.collegeStatuses, function (value, collegeCccId) {
                            // 1 = applied, 2 = enrolled
                            if (value === 1 || value === 2) {
                                return collegeCccId;
                            }
                        });

                        for (var i=0; i < currentUserColleges.length; i++) {

                            if (_.contains(studentColleges, currentUserColleges[i])) {
                                isAffiliated = true;
                                break;
                            }
                        }

                        return isAffiliated;
                    };


                    /*============= MODEL =============*/

                    $scope.isAssessmentsDisabled = ASSESSMENTS_DISABLED;

                    $scope.activationsLoadingComplete = false;
                    $scope.activeActivations = [];
                    $scope.inactiveActivations = [];
                    $scope.completedActivations = [];

                    $scope.allowActivationCreation = CurrentUserService.hasPermission('CREATE_ACTIVATION') && !ASSESSMENTS_DISABLED;
                    $scope.allowDeactivate = CurrentUserService.hasPermission('CANCEL_ACTIVATION');
                    $scope.allowReactivate = CurrentUserService.hasPermission('REACTIVATE_ACTIVATION');
                    $scope.allowTestEventReactivate = CurrentUserService.hasPermission('VIEW_TEST_EVENT_BY_COLLEGE');
                    $scope.allowPrint = CurrentUserService.hasPermission('PRINT_ASSESSMENT_SESSION');

                    // TODO: Reimplement once Counselor permissions are fleshed out
                    $scope.allowPlacement = CurrentUserService.hasAtLeastOnePermission(['VIEW_PLACEMENT_DECISION']) && isUserAffiliated();

                    $scope.allowEdit = CurrentUserService.hasPermission('UPDATE_ACTIVATION');
                    $scope.allowScore = CurrentUserService.hasPermission('PRINT_ASSESSMENT_SESSION');


                    /*============= MODEL DEPENDENT METHODS =============*/

                    var populateTestCenterList = function () {
                        return LocationService.getTestCenterMap().then(function (testCenterMap_in) {
                            testCenterMap = testCenterMap_in;
                        });
                    };

                    var displayActivations = function (activations) {

                        $scope.activeActivations = [];
                        $scope.inactiveActivations = [];
                        $scope.completedActivations = [];

                        _.each(activations, function (activation) {

                            if (ACTIVATION_STATUS_MAPS['ACTIVE'][activation.status]) {
                                $scope.activeActivations.push(activation);
                            }
                            if (ACTIVATION_STATUS_MAPS['INACTIVE'][activation.status]) {
                                $scope.inactiveActivations.push(activation);
                            }
                            if (ACTIVATION_STATUS_MAPS['COMPLETE'][activation.status]) {
                                $scope.completedActivations.push(activation);
                            }
                        });

                        $scope.activeActivations = $filter('orderBy')($scope.activeActivations, ['-createDate']);
                        $scope.inactiveActivations = $filter('orderBy')($scope.inactiveActivations, ['-statusChangeHistory[0].changeDate']);
                        $scope.completedActivations = $filter('orderBy')($scope.completedActivations, ['-statusChangeHistory[0].changeDate']);
                    };

                    var refreshActivationList = function () {

                        if (ASSESSMENTS_DISABLED) {
                            return;
                        }

                        // This is to simulate loading for UX purposes
                        $scope.activationsLoadingComplete = false;
                        $scope.activeActivations = [];
                        $scope.inactiveActivations = [];
                        $scope.completedActivations = [];

                        populateTestCenterList().then(function () {

                            return ActivationSearchAPIService.getAllActivationsForStudent([$scope.student.cccId]).then(function (activationsList) {

                                displayActivations(activationsList);

                            }, function (err) {

                                displayActivations([]);
                            });

                        }).finally(function () {

                            $scope.activationsLoadingComplete = true;
                            $scope.$emit('ccc-student-profile.assessmentHistoryLoaded', $scope.completedActivations);
                        });
                    };


                    /*============= BEHAVIOR =============*/

                    $scope.createNewActivation = function (student) {
                        $scope.$emit('ccc-student-profile.createNewActivation', $scope.student);
                    };

                    $scope.viewPlacements = function () {
                        $scope.$emit('ccc-student-profile.viewPlacements', $scope.student);
                    };

                    $scope.canReactivate = function (activation) {
                        return $scope.allowReactivate && testCenterMap[activation.locationId];
                    };


                    /*============= LISTENERS =============*/

                    $scope.$on('ccc-student-profile.requestRefresh', function () {
                        refreshActivationList();
                        $($element).find('[ccc-autofocus]').focus();
                    });

                    $scope.$on('ccc-activation-card.testEventActivationReactivate', function (e, activation) {
                        $state.go('remoteEvents', {testEventId: activation.testEvent.testEventId});
                    });

                    $scope.$on('ccc-activation-card.score', function (e, activation, student) {
                        $scope.$emit('ccc-student-profile.score', activation, student);
                    });

                    $scope.$on('ccc-activation-card.requestPrint', function (e, activation) {
                        PrintActivationService.print(activation);
                    });


                    /*============ INITIALIZATION ============*/

                    refreshActivationList();
                }
            ],

            template: [

                '<div class="ccc-student-profile-user-badge">',
                    '<ccc-student-user class="ccc-student-user ccc-user" user="student"></ccc-student-user>',
                '</div>',

                '<div class="well well-sm action-bar" ng-if="allowActivationCreation || allowPlacement">',
                    '<div class="actions">',

                        '<button ccc-autofocus ccc-focusable ng-if="allowActivationCreation" class="btn btn-sm btn-primary btn-right" ng-click="createNewActivation()"><span class="fa fa-plus-circle" aria-hidden="true"></span> <span translate="CCC_VIEW_HOME.CCC-STUDENT-PROFILE.ACTIVATE_STUDENT"></span></button>',
                        '<button ccc-autofocus ccc-focusable ng-if="allowPlacement" class="btn btn-sm btn-primary btn-icon-right btn-right" ng-click="viewPlacements()"><span translate="CCC_VIEW_HOME.CCC-STUDENT-PROFILE.COLLEGE_PLACEMENTS"></span> <i class="fa fa-chevron-right"></i></button>',

                    '</div>',
                '</div>',

                '<div ng-if="::!isAssessmentsDisabled">',

                    '<div class="ccc-student-profile-subsection">',
                        '<h3 class="ccc-student-profile-section-title" translate="CCC_VIEW_HOME.CCC-STUDENT-PROFILE.SECTION.ACTIVE.TITLE"></h3>',
                        '<p class="ccc-student-profile-section-desc" translate="CCC_VIEW_HOME.CCC-STUDENT-PROFILE.SECTION.ACTIVE.DESCRIPTION"></p>',
                        '<div class="ccc-student-profile-activations">',
                            '<div ng-if="activationsLoadingComplete && activeActivations.length == 0" class="ccc-student-profile-zero-state">',
                                '<h4 translate="CCC_VIEW_HOME.CCC-STUDENT-PROFILE.SECTION.ACTIVE.ZERO"></h4>',
                                '<button ng-if="allowActivationCreation" ng-click="createNewActivation(student)" class="btn btn-success btn-sm create-activation"><span class="icon fa fa-plus" role="presentation" aria-hidden="true"></span> <span translate="CCC_VIEW_HOME.CCC-STUDENT-PROFILE.ACTIVATE_STUDENT"></span></button>',
                            '</div>',
                            '<ccc-activation-card ',
                                'allow-logs="true" ',
                                'allow-deactivate="allowDeactivate" ',
                                'allow-print="allowPrint" ',
                                'allow-score="allowScore" ',
                                'allow-edit="allowEdit" ',
                                'activation="activation" ',
                                'student="student" ',
                                'header-controls="true" ',
                                'footer-controls="true" ',
                                'activation-details="true" ',
                                'ng-repeat="activation in activeActivations track by activation.activationId">',
                            '</ccc-activation-card>',
                        '</div>',
                    '</div>',

                    '<ccc-content-loading-placeholder ng-hide="activationsLoadingComplete"></ccc-content-loading-placeholder>',

                    '<div class="ccc-student-profile-subsection">',
                        '<h3 class="ccc-student-profile-section-title" translate="CCC_VIEW_HOME.CCC-STUDENT-PROFILE.SECTION.COMPLETE.TITLE"></h3>',
                        '<p class="ccc-student-profile-section-desc" translate="CCC_VIEW_HOME.CCC-STUDENT-PROFILE.SECTION.COMPLETE.DESCRIPTION"></p>',
                        '<div class="ccc-student-profile-activations">',
                            '<ccc-activation-card ',
                                'allow-logs="true" ',
                                'activation="activation" ',
                                'allow-placement="allowPlacement" ',
                                'header-controls="true" ',
                                'footer-controls="true" ',
                                'activation-details="true" ',
                                'student="student" ',
                                'ng-repeat="activation in completedActivations track by activation.activationId">',
                            '</ccc-activation-card>',
                        '</div>',
                    '</div>',

                    '<ccc-content-loading-placeholder ng-hide="completedActivations.length > 0" no-results-info="activationsLoadingComplete && completedActivations.length == 0" hide-default-no-results-text="true">{{student.firstName}} <span translate="CCC_VIEW_HOME.CCC-STUDENT-PROFILE.SECTION.COMPLETE.ZERO"></span></ccc-content-loading-placeholder>',

                    '<div class="ccc-student-profile-subsection">',
                        '<h3 class="ccc-student-profile-section-title" translate="CCC_VIEW_HOME.CCC-STUDENT-PROFILE.SECTION.INACTIVE.TITLE"></h3>',
                        '<p class="ccc-student-profile-section-desc" translate="CCC_VIEW_HOME.CCC-STUDENT-PROFILE.SECTION.INACTIVE.DESCRIPTION"></p>',
                        '<div class="ccc-student-profile-activations">',
                            '<ccc-activation-card ',
                                'allow-logs="true" ',
                                'activation="activation" ',
                                'allow-reactivate="canReactivate(activation)" ',
                                'allow-test-event-reactivate="canReactivate(activation) && allowTestEventReactivate" ',
                                'allow-placement="allowPlacement" ',
                                'header-controls="true" ',
                                'footer-controls="true" ',
                                'activation-details="true" ',
                                'student="student" ',
                                'ng-repeat="activation in inactiveActivations track by activation.activationId">',
                            '</ccc-activation-card>',
                        '</div>',
                    '</div>',

                    '<ccc-content-loading-placeholder ng-hide="inactiveActivations.length > 0" no-results-info="activationsLoadingComplete && inactiveActivations.length == 0" hide-default-no-results-text="true">{{student.firstName}} <span translate="CCC_VIEW_HOME.CCC-STUDENT-PROFILE.SECTION.INACTIVE.ZERO"></span></ccc-content-loading-placeholder>',

                '</div>'

            ].join('')

        };

    });

})();
