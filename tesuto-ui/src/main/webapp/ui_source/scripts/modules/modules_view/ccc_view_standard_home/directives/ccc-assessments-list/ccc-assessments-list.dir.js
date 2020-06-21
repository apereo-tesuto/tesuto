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

    angular.module('CCC.View.Home').directive('cccAssessmentsList', function () {

        return {

            restrict: 'E',

            scope: {
                initialAssessmentIdentifiers: '=?',
                isRequired: '=?',
                initialDeliveryType: '@?', // ONLINE or PAPER
                testLocationId: '=?',
                noResultsMsg: '@?'
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                '$translate',
                'AssessmentsAPIService',
                'NotificationService',
                'LocationService',

                function ($scope, $element, $timeout, $translate, AssessmentsAPIService, NotificationService, LocationService) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/


                    /*============= MODEL =============*/

                    $scope.noResultsMsg = $scope.noResultsMsg || 'CCC_COMP.CONTENT_NO_RESULTS';

                    $scope.initialAssessmentIdentifiers = $scope.initialAssessmentIdentifiers || [];
                    $scope.isRequired = $scope.isRequired || false;

                    $scope.assessmentsLoadingComplete = false;
                    $scope.hasErrors = false;

                    $scope.onlineAssessmentRadios = [];
                    $scope.paperAssessmentRadios = [];
                    $scope.selectedAssessments = [];

                    $scope.visibleTab = $scope.initialDeliveryType || 'ONLINE';

                    // if nobody explicitly is trying to set the testLocationId, then use the current test center form the locationService
                    if ($scope.testLocationId === undefined) {
                        $scope.location = LocationService.getCurrentTestCenter() || null;
                    }


                    /*============= MODEL DEPENDENT METHODS =============*/

                    var isAssessmentSelected = function (assessment) {
                        return (
                            _.find($scope.initialAssessmentIdentifiers, function (assessmentIdentifier) {

                                return assessmentIdentifier.identifier === assessment.identifier && assessmentIdentifier.namespace === assessment.namespace;

                            }) !== undefined
                        );
                    };

                    var createAssessmentRadios = function (assessments) {

                        $scope.onlineAssessmentRadios = [];
                        $scope.paperAssessmentRadios = [];

                        _.each(assessments, function (assessment) {

                            if (assessment.online) {

                                $scope.onlineAssessmentRadios.push({
                                    deliveryType: "ONLINE",
                                    title: assessment.title,
                                    value: assessment.id,
                                    id: assessment.identifier,
                                    identifier: assessment.identifier,
                                    namespace: assessment.namespace,
                                    checked: isAssessmentSelected(assessment)
                                });

                            } else if (assessment.paper) {

                                $scope.paperAssessmentRadios.push({
                                    deliveryType: "PAPER",
                                    title: assessment.title,
                                    value: assessment.id,
                                    id: assessment.identifier,
                                    identifier: assessment.identifier,
                                    namespace: assessment.namespace,
                                    checked: isAssessmentSelected(assessment)
                                });
                            }
                        });

                        var hasCheckedPaperRadio = _.find($scope.paperAssessmentRadios, function (paperAssessmentRadio) {
                            return paperAssessmentRadio.checked === true;
                        }) !== undefined;

                        var hasCheckedOnlineRadio = _.find($scope.onlineAssessmentRadios, function (onlineAssessmentRadio) {
                            return onlineAssessmentRadio.checked === true;
                        }) !== undefined;

                        // if we have radios selected, use that delivery type
                        if (hasCheckedPaperRadio || hasCheckedOnlineRadio) {

                            $scope.setVisibleTab(hasCheckedPaperRadio ? 'PAPER' : 'ONLINE');

                        // else just set it to what it was to start with
                        } else {
                            $scope.setVisibleTab($scope.visibleTab);
                        }
                    };

                    var doGetSelectedAssessments = function () {
                        if ($scope.visibleTab === "ONLINE") {

                            return _.filter($scope.onlineAssessmentRadios, function (assessment) {
                                return assessment.checked;
                            });

                        } else {

                            return _.filter($scope.paperAssessmentRadios, function (assessment) {
                                return assessment.checked;
                            });
                        }
                    };

                    var getSelectedAssessments = function () {

                        $timeout(function () {

                            $scope.selectedAssessments = doGetSelectedAssessments();
                            $scope.$emit('ccc-assessments-list.selectedAssessmentsChanged', $scope.selectedAssessments, $scope.visibleTab);
                        });
                    };

                    var refreshAssessments = function (location) {

                        if (!location) {
                            $scope.assessmentsLoadingComplete = true;
                            $scope.onlineAssessmentRadios = [];
                            $scope.paperAssessmentRadios = [];
                            return;
                        }

                        $scope.assessmentsLoadingComplete = false;
                        $scope.onlineAssessmentRadios = [];
                        $scope.paperAssessmentRadios = [];

                        AssessmentsAPIService.getAssessmentsByTestLocationId(location.id).then(function (assessments) {

                            createAssessmentRadios(assessments);

                        }, function (err) {

                            createAssessmentRadios([]);

                        }).finally(function () {
                            $scope.assessmentsLoadingComplete = true;
                            getSelectedAssessments();
                        });
                    };


                    /*============= BEHAVIOR =============*/

                    $scope.setVisibleTab = function (tabName) {

                        $scope.visibleTab = tabName;

                        // Un-check and clear selected assessments when a user switches tabs
                        if ($scope.visibleTab === 'ONLINE') {

                            _.each($scope.paperAssessmentRadios, function (assessment) {
                                assessment.checked = false;
                            });

                        } else {

                            _.each($scope.onlineAssessmentRadios, function (assessment) {
                                assessment.checked = false;
                            });
                        }

                        $scope.selectedAssessments = doGetSelectedAssessments();
                        $scope.$emit('ccc-assessments-list.selectedAssessmentsChanged', $scope.selectedAssessments, $scope.visibleTab);
                    };


                    /*============= LISTENERS =============*/

                    $scope.$on('LocationService.currentTestCenterUpdated', function (event, location) {
                        refreshAssessments(location);
                    });

                    $scope.$on('ccc-activate-student.createActivation', function () {
                        $scope.$emit('ccc-assessments-list.selectedAssessments', $scope.selectedAssessments);
                    });

                    $scope.$on('ccc-activate-student.assessmentsError', function () {
                        $scope.hasErrors = true;

                        $scope.notifyMinError = NotificationService.open({
                            icon: 'fa fa-exclamation-triangle',
                            title: $translate.instant('CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.ERROR.ASSESSMENT_ACTIVATE'),
                            message: '',
                            uid: 'ccc-assessments-list.error'
                        },
                        {
                            delay: 0,
                            type: "warning",
                            allow_dismiss: true
                        });
                    });

                    $scope.$watch('selectedAssessments', function () {

                        if ($scope.hasErrors && $scope.selectedAssessments.length > 0) {

                            $scope.hasErrors = false;
                            $scope.notifyMinError.close();
                        }
                    });

                    $scope.$on('ccc-radio-box.change', getSelectedAssessments);

                    $scope.$on('ccc-bulk-activation-add-assessment.locationSelected', function (event, location) {
                        refreshAssessments(location);
                    });

                    $scope.$on('ccc-assessments-list.requestRefresh', function (event, location) {
                        refreshAssessments(location);
                    });

                    $scope.$watch('testLocationId', function (newTestLocationId, oldTestLocationId) {

                        if (newTestLocationId !== undefined && newTestLocationId !== oldTestLocationId) {

                            LocationService.getTestCenterById(newTestLocationId).then(function (testCenter) {
                                $scope.location = testCenter;
                                refreshAssessments($scope.location);
                            });
                        }
                    });


                    /*============ INITIALIZATION ============*/

                    if (!$scope.testLocationId) {
                        refreshAssessments($scope.location);
                    }
                }
            ],

            template: [

                '<ul class="nav nav-tabs" role="tablist">',
                    '<li role="presentation" ng-class="visibleTab === \'ONLINE\' ? \'active\' : \'\'"><a ng-click="setVisibleTab(\'ONLINE\')" href="" aria-controls="online-assessments" aria-selected="{{visibleTab === \'ONLINE\'}}" role="tab" id="online-assessments"><i class="fa fa-laptop" aria-hidden="true"></i> Online</a></li>',
                    '<li role="presentation" ng-class="visibleTab === \'PAPER\' ? \'active\' : \'\'"><a ng-click="setVisibleTab(\'PAPER\')" href="" aria-controls="paper-assessments" aria-selected="{{visibleTab === \'PAPER\'}}" role="tab" id="paper-assessments"><i class="fa fa-file-text-o" aria-hidden="true"></i> Paper</a></li>',
                '</ul>',

                '<div class="tab-content">',

                    '<div ng-if="visibleTab === \'ONLINE\'" role="tabpanel" class="tab-pane active" aria-labelledby="online-assessments">',

                        '<div id="online-assessment-list-description" class="sr-only" translate="CCC_VIEW_HOME.ASSESSMENT_LIST.DESCRIPTION.ONLINE"></div>',

                        '<ccc-content-loading-placeholder ng-hide="onlineAssessmentRadios.length > 0" no-results="assessmentsLoadingComplete && onlineAssessmentRadios.length == 0" hide-default-no-results-text="true">',
                            '<span translate="{{::noResultsMsg}}"></span>',
                        '</ccc-content-loading-placeholder>',

                        '<div>',
                            '<ccc-radio-box labelled-by="online-assessment-list-description" ng-class="{\'error\': hasErrors}" input-type="checkbox" ng-repeat="radio in onlineAssessmentRadios | orderBy: [\'title\'] track by radio.value" radio="radio" is-disabled="processing" data-notify="dismiss" described-by="assessmentErrors"></ccc-radio-box>',
                        '</div>',

                    '</div>',

                    '<div ng-if="visibleTab === \'PAPER\'" role="tabpanel" class="tab-pane active" aria-labelledby="paper-assessments">',

                        '<div id="paper-assessment-list-description" class="sr-only" translate="CCC_VIEW_HOME.ASSESSMENT_LIST.DESCRIPTION.PAPER"></div>',

                        '<ccc-content-loading-placeholder ng-hide="paperAssessmentRadios.length > 0" no-results="assessmentsLoadingComplete && paperAssessmentRadios.length == 0" hide-default-no-results-text="true">',
                            '<span translate="{{::noResultsMsg}}"></span>',
                        '</ccc-content-loading-placeholder>',

                        '<div>',
                            '<ccc-radio-box labelled-by="paper-assessment-list-description" ng-class="{\'error\': hasErrors}" input-type="checkbox" ng-repeat="radio in paperAssessmentRadios | orderBy: [\'title\'] track by radio.value" radio="radio" is-disabled="processing" data-notify="dismiss" described-by="assessmentErrors"></ccc-radio-box>',
                        '</div>',

                    '</div>',
                '</div>',

                '<div id="assessmentErrors" class="ccc-validation-messages ccc-validation-messages-standalone">',
                    '<p ng-if="!selectedAssessments.length && isRequired">',
                        '<i class="fa fa-exclamation-triangle color-warning"></i> ',
                        '<span translate="CCC_VIEW_HOME.CCC-ASSESSMENTS-LIST.VALIDATION_ASSESSMENTS"></span>',
                    '</p>',
                '</div>'

            ].join('')

        };

    });

})();
