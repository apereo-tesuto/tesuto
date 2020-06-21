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

    angular.module('CCC.Activations').directive('cccActivationCard', function () {

        return {

            restrict: 'E',

            scope: {
                activation: "=",
                student: '=?',
                isStudent: '=?',
                headerControls: '=?',
                footerControls: '=?',
                activationDetails: '=?',
                allowLogs: '=?',
                allowDeactivate: '=?',
                allowReactivate: '<',
                allowTestEventReactivate: '<',
                allowPrint: '=?',
                allowPlacement: '=?',
                allowEdit: '=?',
                allowScore: '=?'
            },

            controller: [

                '$rootScope',
                '$scope',
                '$filter',
                'ActivationClass',
                'ActivationsAPIService',
                'ModalService',
                'Moment',
                'CollisionService',

                function ($rootScope, $scope, $filter, ActivationClass, ActivationsAPIService, ModalService, Moment, CollisionService) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/

                    var STATUS_CLASS_MAP = {
                        'ACTIVATED': 'status-ready',
                        'UPCOMING': 'status-ready',
                        'READY': 'status-ready',
                        'PENDING_SCORING': 'status-ready',
                        'IN_PROGRESS': 'status-in-progress',
                        'PAUSED': 'status-paused',
                        'COMPLETE': 'status-complete',
                        'DEACTIVATED': 'status-deactivated',
                        'EXPIRED': 'status-expired',
                        'INCOMPLETE': 'status-incomplete'
                    };

                    var ACTIONS_MAP = {
                        edit: {
                            name: 'CCC_ACTIVATIONS.CARD.CONTROLS.EDIT',
                            icon: 'fa fa-pencil',
                            method: function () {
                                edit();
                            }
                        },
                        deactivate: {
                            name: 'CCC_ACTIVATIONS.CARD.CONTROLS.DEACTIVATE',
                            icon: 'fa fa-stop-circle',
                            method: function () {
                                deactivate($scope.activation);
                            }
                        },
                        print: {
                            name: 'CCC_ACTIVATIONS.CARD.CONTROLS.PRINT',
                            icon: 'fa fa-print',
                            method: function () {
                                print();
                            }
                        },
                        reactivate: {
                            name: 'CCC_ACTIVATIONS.CARD.CONTROLS.REACTIVATE',
                            icon: 'fa fa-play-circle',
                            method: function () {
                                reactivate($scope.activation);
                            }
                        },
                        score: {
                            name: 'CCC_ACTIVATIONS.CARD.CONTROLS.SCORE',
                            icon: 'fa fa-pencil-square-o',
                            method: function () {
                                score();
                            }
                        }
                    };

                    var getActivationStatus = function (activation) {

                        if (activation.isInFuture && (activation.status === 'READY' || activation.status === 'ACTIVATED')) {
                            return 'UPCOMING';
                        } else {
                            return activation.status;
                        }
                    };


                    /*============ MODEL ============*/

                    $scope.allowLogs = $scope.allowLogs || false;
                    $scope.allowDeactivate = $scope.allowDeactivate || false;
                    $scope.allowReactivate = $scope.allowReactivate || false;
                    $scope.allowTestEventReactivate = $scope.allowTestEventReactivate || false;
                    $scope.allowPrint = $scope.allowPrint || false;
                    $scope.allowPlacement = $scope.allowPlacement || false;
                    $scope.allowEdit = $scope.allowEdit || false;
                    $scope.allowScore = $scope.allowScore || false;

                    $scope.usableActions = [];

                    $scope.student = $scope.student || null;
                    $scope.isStudent = $scope.isStudent || false;
                    $scope.headerControls = $scope.headerControls || false;
                    $scope.footerControls = $scope.footerControls || false;
                    $scope.activationDetails = $scope.activationDetails || false;

                    $scope.logsVisible = false;
                    $scope.resultsVisible = false;
                    $scope.expiresToday = false;

                    // TODO: We need to pass in a list of affiliated colleges of the current user to determine if they are a match with the college this activation was created at. This is going to allow us to have better control over which actions are available per activation card.
                    //
                    // Example: Activation was created at Unicon College. Proctor is not affiliated with Unicon, but has permission to Score, Print, etc at their college so the actions display in the dropdown. When they try to score or print this activation, they will get errors. We can prevent the errors by not displaying the triggers in the first place.


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var checkIfExpiresToday = function () {
                        var now = new Moment.utc();
                        var expirationStart = Moment.utc($scope.activation.endDate).subtract(1, 'day').valueOf();
                        var expirationEnd = $scope.activation.endDate;

                        if (now >= expirationStart && now <= expirationEnd) {
                            $scope.expiresToday = true;
                        }
                    };

                    // We need to calculate some properties derived from the ActivationClass instance
                    var refresh = function () {

                        $scope.activationEndDate = $filter('date')($scope.activation.endDate, 'M/d/yyyy');
                        $scope.activationEndDateFromNow = new Moment.utc($scope.activation.endDate).fromNow(true);

                        // there are some custom UI status values derived from the activation data (mostly around start and end dates)
                        $scope.activationStatus = getActivationStatus($scope.activation);

                        $scope.activationStatusClass = STATUS_CLASS_MAP[$scope.activation.status];
                        if ($scope.activation.statusChangeHistory.length) {
                            $scope.activationActivatedOnFormatted = new Moment.utc($scope.activation.statusChangeHistory[0].changeDate).fromNow();
                        } else {
                            $scope.activationActivatedOnFormatted = new Moment.utc($scope.activation.createDate).fromNow();
                        }
                        $scope.activationStatusChangeBy = $scope.activation.statusChangeHistory.length === 0 ? "" : $scope.activation.statusChangeHistory[0].userName;
                        $scope.activationStatusChangeOn = $scope.activation.statusChangeHistory.length === 0 ? "" : $scope.activation.statusChangeHistory[0].changeDate;

                        $scope.activationLastActivity = new Moment.utc($scope.activationStatusChangeOn).fromNow();
                        $scope.activationStatusChangeOnFormatted = $filter('date')($scope.activationStatusChangeOn, 'M/d/yyyy');

                        $scope.activationTimeSpentOnAssessment = $scope.activation.timeSpentOnAssessment === 0 ? '--' : new Moment.utc($scope.activation.timeSpentOnAssessment).format('HH:mm');
                        $scope.activationEventStart = $scope.activation.testEvent ? $filter('date')($scope.activation.testEvent.startDate, 'MMM d') : null;
                        $scope.activationEventEnd = $scope.activation.testEvent ? $filter('date')($scope.activation.testEvent.endDate, 'MMM d') : null;

                        checkIfExpiresToday();
                        setUsableActions();
                    };

                    var reactivateActivation = function (activation) {
                        ActivationsAPIService.reactivateActivation(activation.activationId).then(function () {
                            refresh();
                            $scope.$emit('ccc-activation-card.reactivate', activation);
                        });
                    };

                    var reactivateNormalActivation = function (activation) {

                        return CollisionService.getDuplicateActivations([activation], $scope.student).then(function (duplicateActivations) {

                            if (duplicateActivations.length) {
                                CollisionService.openResolutionModal(duplicateActivations, $scope.student).then(function () {

                                    // success (reactivate)
                                    reactivateActivation(activation);
                                });

                            } else {
                                reactivateActivation(activation);
                            }
                        });
                    };

                    var reactivateTestEventActivation = function (activation) {

                        if ($scope.allowTestEventReactivate) {

                            var confirmButtonConfigs = {
                                cancel: {
                                    title: 'CCC_ACTIVATIONS.TEST_EVENT_REACTIVATE.BUTTON_CANCEL',
                                    btnClass: 'btn-default'
                                },
                                okay: {
                                    title: 'CCC_ACTIVATIONS.TEST_EVENT_REACTIVATE.BUTTON_CONFIRM',
                                    btnClass: 'btn-primary'
                                }
                            };

                            var confirmReactivateModal = ModalService.openConfirmModal({
                                title: 'CCC_ACTIVATIONS.TEST_EVENT_REACTIVATE.TITLE',
                                message: ['CCC_ACTIVATIONS.TEST_EVENT_REACTIVATE.MSG_WARNING', 'CCC_ACTIVATIONS.TEST_EVENT_REACTIVATE.MSG_PERMISSION'],
                                buttonConfigs: confirmButtonConfigs,
                                type: 'info'
                            });

                            confirmReactivateModal.result.then(function () {
                                $scope.$emit('ccc-activation-card.testEventActivationReactivate', $scope.activation);
                            });

                        } else {
                            ModalService.openAlertModal('CCC_ACTIVATIONS.TEST_EVENT_REACTIVATE.TITLE', ['CCC_ACTIVATIONS.TEST_EVENT_REACTIVATE.MSG_WARNING', 'CCC_ACTIVATIONS.TEST_EVENT_REACTIVATE.MSG_NO_PERMISSION'], 'info');
                        }
                    };

                    // Used to determine which actions will be available to each activation card based on the activation state.
                    var setUsableActions = function () {
                        var usableActions = [];

                        // Active State
                        if ($scope.activationStatus === 'READY' || $scope.activationStatus === 'IN_PROGRESS' || $scope.activationStatus === 'PAUSED' || $scope.activationStatus === 'PENDING_SCORING') {

                            // Edit activation
                            if ($scope.allowEdit && !$scope.activation.testEvent) {
                                usableActions.push(ACTIONS_MAP.edit);
                            }

                            // Deactivate actiavtion
                            if ($scope.allowDeactivate) {
                                usableActions.push(ACTIONS_MAP.deactivate);
                            }

                            // Print activation
                            if ($scope.activation.deliveryType === 'PAPER' && $scope.allowPrint) {
                                usableActions.push(ACTIONS_MAP.print);
                            }

                            // Score activation
                            if ($scope.activation.deliveryType === 'PAPER' && $scope.allowScore) {
                                usableActions.push(ACTIONS_MAP.score);
                            }
                        }

                        // Upcoming State
                        if ($scope.activationStatus === 'UPCOMING') {

                            // Deactivate actiavtion
                            if ($scope.allowDeactivate) {
                                usableActions.push(ACTIONS_MAP.deactivate);
                            }
                        }

                        // Inactive State
                        if ($scope.activationStatus === 'DEACTIVATED' || $scope.activationStatus === 'EXPIRED' || $scope.activationStatus === 'INCOMPLETE') {

                            // Reactivate activation
                            if ($scope.allowReactivate) {
                                usableActions.push(ACTIONS_MAP.reactivate);
                            }
                        }

                        $scope.usableActions = usableActions;
                    };

                    var deactivate = function (activation) {

                        var modalScope = $rootScope.$new();
                        modalScope.activation = activation;
                        modalScope.student = $scope.student;
                        modalScope.reason = 'Direct';

                        var completionModal = ModalService.open({
                            scope: modalScope,
                            template: '<ccc-modal-deactivate-activation modal="modal" student="student" activation="activation"></ccc-modal-deactivate-activation>'
                        });

                        completionModal.result.then(function () {
                            modalScope.activation.deactivate(modalScope.reason).then(function () {
                                refresh();
                                $scope.$emit('ccc-activation-card.deactivate', modalScope.activation);
                            });
                        });
                    };

                    var reactivate = function (activation) {

                        if (!activation.testEvent) {
                            reactivateNormalActivation(activation);
                        } else {
                            reactivateTestEventActivation(activation);
                        }
                    };

                    var edit = function () {
                        $scope.$emit('ccc-activation-card.edit', $scope.activation, $scope.student);
                    };

                    var score = function () {
                        $scope.$emit('ccc-activation-card.score', $scope.activation, $scope.student);
                    };

                    var print = function () {
                        $scope.$emit('ccc-activation-card.requestPrint', $scope.activation);
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.toggleLogs = function () {
                        $scope.logsVisible = !$scope.logsVisible;
                        $scope.resultsVisible = false;
                    };

                    $scope.toggleResults = function () {
                        $scope.resultsVisible = !$scope.resultsVisible;
                        $scope.logsVisible = false;
                    };

                    $scope.getPlacement = function (activation) {
                        $scope.$emit('ccc-activation-card.getPlacement', activation);
                    };

                    $scope.showTestResults = function () {
                        $scope.$emit('ccc-activation-card.showTestResults', $scope.activation, $scope.student);
                    };


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-activation-card.requestRefresh', refresh);
                    $scope.$on('PrintActivationService.print', refresh);


                    /*============ INITIALIZATION ============*/

                    if (!($scope.activation instanceof ActivationClass)) {
                        throw new Error('ccc-activation-card.ActivationClass_expected');
                    }

                    refresh();
                }
            ],

            template: [
                '<div class="wrapper {{activationStatusClass}}">',

                    '<div class="header {{activationStatusClass}}">',
                        '<h4 class="title">',
                            '{{::activation.assessmentTitle}}',
                            // '<span class="text-fade"></span>',
                        '</h4>',
                        '<div class="labels">',
                            '<span ng-if="activation.deliveryType === \'PAPER\'" class="label paper" translate="CCC_ACTIVATIONS.CARD.LABEL.PAPER"></span>',
                            '<span class="label {{activationStatusClass}}" translate="CCC_ACTIVATIONS.CARD.STATUS.{{activationStatus}}"></span>',
                        '</div>',
                        '<div ng-if="headerControls" class="actions">',

                            // Controls for a student user
                            '<span ng-if="isStudent">',

                                // Completed
                                '<button ccc-focusable ng-if="activationStatus === \'COMPLETE\' && allowPlacement" class="btn btn-sm btn-success" ng-click="showTestResults(activation)">',
                                    '<span translate="CCC_ACTIVATIONS.CARD.CONTROLS.SHOW_TEST_RESULTS"></span>',
                                    ' <i class="fa fa-chevron-right" aria-hidden="true"></i>',
                                '</button>',

                                // Ready status
                                '<button ccc-focusable class="btn btn-success btn-sm" ng-if="activationStatus === \'READY\'"><span translate="CCC_ACTIVATIONS.CARD.CONTROLS.START"></span> <span class="fa fa-chevron-right" role="presentation" aria-hidden="true"></span></button>',

                                // In-progress or Paused
                                '<button ccc-focusable class="btn btn-success btn-sm" ng-if="activationStatus === \'IN_PROGRESS\' || activationStatus === \'PAUSED\'"><span translate="CCC_ACTIVATIONS.CARD.CONTROLS.CONTINUE"></span> <span class="fa fa-chevron-right" role="presentation" aria-hidden="true"></span></button>',

                            '</span>',
                        '</div>',
                    '</div>',

                    // Test Event Details
                    '<div ng-if="activation.testEvent" class="event">',
                        '<span class="event-prefix">',
                            '<span class="sr-only">Event</span>',
                            '<span class="fa fa-calendar-check-o" role="presentation" aria-hidden="true"></span>',
                        '</span>',
                        '<div class="event-header">',
                            '<h4 class="event-title">{{::activation.testEvent.name}}</h4>',
                        '</div>',
                        '<div class="event-body">',
                            '<div class="event-dates">',
                                '<span class="sr-only">Date:</span>',
                                '<span class="sr-only">From:</span>',
                                '<span class="event-date start-date">{{::activationEventStart}} - </span>',
                                '<span class="sr-only">To:</span>',
                                '<span class="event-date end-date">{{::activationEventEnd}}</span>',
                            '</div>',
                            // '<div class="event-location">',
                            //     '<span class="event-label">Location:</span>',
                            //     'Remote',
                            // '</div>',
                            '<div class="event-proctor">',
                                '<span class="sr-only">Proctor:</span>',
                                '{{::activation.testEvent.proctorFirstName}} ',
                                '{{::activation.testEvent.proctorLastName}}',
                            '</div>',
                        '</div>',
                    '</div>',

                    '<div ng-if="activationDetails" class="details {{activationStatusClass}}">',

                        // Ready, In-Progress, Paused, Upcoming
                        '<span ng-if="activationStatus === \'ACTIVATED\' || activationStatus === \'READY\' || activationStatus === \'IN_PROGRESS\' || activationStatus === \'PAUSED\' || activationStatus === \'UPCOMING\' || activationStatus === \'PENDING_SCORING\'">',
                            '<div>',
                                '<span class="detail" translate="CCC_ACTIVATIONS.CARD.DETAILS.LAST_ACTIVITY"></span>',
                                // Ready status
                                '<span ng-if="activationStatus === \'READY\'">{{::activationActivatedOnFormatted}}</span> ',
                                // In-progress or Paused status
                                '<span ng-if="activationStatus === \'IN_PROGRESS\' || activationStatus === \'PAUSED\'">{{::activationLastActivity}}</span> ',
                                '<span class="divider">|</span> {{::activation.collegeName}} - {{::activation.locationLabel}}',
                            '</div>',
                            '<div>',
                                '<span class="detail" translate="CCC_ACTIVATIONS.CARD.DETAILS.TIME_ON_ASSESSMENT"></span>',
                                '{{activationTimeSpentOnAssessment}}',
                            '</div>',
                            '<div ng-class="{\'expiration-warning\': expiresToday}">',
                                '<span class="detail" ng-class="{\'expiration-warning\': expiresToday}" translate="CCC_ACTIVATIONS.CARD.DETAILS.EXPIRES"></span>',
                                '{{::activationEndDate}} <span class="divider">|</span> {{::activationEndDateFromNow}}',
                            '</div>',
                        '</span>',

                        // Completed
                        '<span ng-if="activationStatus === \'COMPLETE\'">',
                            '<div>',
                                '<span class="detail" translate="CCC_ACTIVATIONS.CARD.DETAILS.DETAIL"></span>',
                                '{{::activationStatusChangeOnFormatted}} <span class="divider">|</span> {{::activation.collegeName}} - {{::activation.locationLabel}}',
                            '</div>',
                            '<div>',
                                '<span class="detail" translate="CCC_ACTIVATIONS.CARD.DETAILS.TIME_ON_ASSESSMENT"></span>',
                                '{{activationTimeSpentOnAssessment}}',
                            '</div>',
                        '</span>',

                        // Deactivated or Incomplete
                        '<span ng-if="activationStatus === \'DEACTIVATED\' || activationStatus === \'INCOMPLETE\'">',
                            '<div>',
                                '<span class="detail" translate="CCC_ACTIVATIONS.CARD.DETAILS.INACTIVE"></span>',
                                '{{::activationStatusChangeOnFormatted}} <span class="divider">|</span> {{::activationLastActivity}}',
                            '</div>',
                            '<div>',
                                '<span class="detail" translate="CCC_ACTIVATIONS.CARD.DETAILS.TIME_ON_ASSESSMENT"></span>',
                                '{{activationTimeSpentOnAssessment}}',
                            '</div>',
                        '</span>',

                        // Expired
                        '<span ng-if="activationStatus === \'EXPIRED\'">',
                            '<div>',
                                '<span class="detail" translate="CCC_ACTIVATIONS.CARD.DETAILS.EXPIRED"></span>',
                                '{{::activationEndDate}}',
                            '</div>',
                            '<div>',
                                '<span class="detail" translate="CCC_ACTIVATIONS.CARD.DETAILS.TIME_ON_ASSESSMENT"></span>',
                                '{{activationTimeSpentOnAssessment}}',
                            '</div>',
                        '</span>',
                    '</div>',

                    // Lower action bar
                    '<div ng-if="footerControls" class="ccc-activation-card-actions-bottom">',

                        // Actions Dropup
                        '<div ccc-dropdown-focus ng-if="usableActions.length && !isStudent" class="activation-card-actions dropup">',
                            '<button class="btn btn-default btn-sm dropdown-toggle" type="button" id="{{activation.activationId}}" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">',
                                'Actions',
                                '<span class="caret"></span>',
                            '</button>',

                            '<ul class="dropdown-menu" aria-labelledby="{{activation.activationId}}">',
                                '<li ng-repeat="action in usableActions" ng-click="action.method()">',
                                    '<a href="#">',
                                        '<i class="{{action.icon}}" aria-hidden="true"></i>',
                                        '<span translate="{{action.name}}"></span>',
                                    '</a>',
                                '</li>',
                            '</ul>',
                        '</div>',

                        // Counselors & Students only
                        '<button ccc-focusable ng-if="activationStatus === \'COMPLETE\' && allowPlacement" class="btn btn-sm btn-primary" ng-click="toggleResults()"><span ng-hide="resultsVisible"><span translate="CCC_ACTIVATIONS.CARD.CONTROLS.SHOW_RESULTS"></span> <span class="fa fa-chevron-down" role="presentation" aria-hidden="true"></span></span> <span ng-hide="!resultsVisible"><span translate="CCC_ACTIVATIONS.CARD.CONTROLS.HIDE_RESULTS"></span> <span class="fa fa-chevron-up" role="presentation" aria-hidden="true"></span></span></button>',

                        // Proctors only
                        '<button ccc-focusable class="ccc-activation-card-logs-button btn btn-default btn-sm" ng-click="toggleLogs()" ng-if="allowLogs"><span ng-hide="logsVisible" translate="CCC_ACTIVATIONS.CARD.DETAILS.SHOW"></span><span ng-hide="!logsVisible" translate="CCC_ACTIVATIONS.CARD.DETAILS.HIDE"></span> <span translate="CCC_ACTIVATIONS.CARD.DETAILS.LOG"></span></button>',
                    '</div>',

                    // Activity Logs Module
                    '<ccc-activation-logs activation="activation" ng-if="logsVisible"></ccc-activation-logs>',

                    // Competency Results Module
                    '<div class="ccc-competency-results" ng-if="resultsVisible && allowPlacement">',
                        '<h5 class="title" translate="CCC_ACTIVATIONS.CARD.RESULTS.TITLE"></h5>',
                        '<ccc-competency-results assessment-session-id="activation.currentAssessmentSessionId" is-student="isStudent"></ccc-competency-results>',
                        '<div class="college-placements">',
                            '<button ng-click="getPlacement(activation)" class="btn btn-primary" ccc-focusable><span translate="CCC_ACTIVATIONS.CARD.RESULTS.TEST_RESULTS_BUTTON"></span> <span class="fa fa-chevron-right" role="presentation" aria-hidden="true"></span></button>',
                        '</div>',
                    '</div>',
                '</div>'
            ].join('')

        };
    });

})();
