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

    angular.module('CCC.View.Student').directive('cccWorkflowStudentDashboard', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                '$timeout',
                '$translate',
                'Moment',
                'CurrentStudentService',
                'ViewManagerEntity',
                'ModalService',

                function ($scope, $timeout, $translate, Moment, CurrentStudentService, ViewManagerEntity, ModalService) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/

                    // a flag to switch if an activation is changed so we can refresh as needed
                    var activationsChanged = false;

                    var launchUpcomingModal = function (activation) {

                        var fromTime = new Moment.utc(activation.startDate).fromNow();
                        var message = $translate.instant('CCC_STUDENT.WORKFLOW.STUDENT_DASHBOARD.UPCOMING_MODAL.MESSAGE', {fromTime:fromTime});

                        ModalService.openAlertModal('CCC_STUDENT.WORKFLOW.STUDENT_DASHBOARD.UPCOMING_MODAL.TITLE', message);
                    };


                    /*============ MODEL ==============*/

                    $scope.student = CurrentStudentService.getStudent();

                    // use a view manager to handler the workflow between screens
                    $scope.viewManager = new ViewManagerEntity({});


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var addStartAssessmentView = function (activation) {

                        var viewScope = $scope.$new();

                        viewScope.activation = activation;

                        viewScope.$on('ccc-student-start-activation-instructions.cancel', function () {
                            $scope.viewManager.goToView('student-dashboard', true);
                        });

                        viewScope.$on('ccc-student-start-activation-instructions.beforeStart', function () {
                            $scope.viewManager.disableNavigation();
                        });

                        viewScope.$on('ccc-student-start-activation-instructions.startFailed', function () {
                            $scope.viewManager.enableNavigation();
                        });

                        viewScope.$on('ccc-student-start-activation-instructions.startComplete', function () {
                            activationsChanged = true;
                            $scope.viewManager.enableNavigation();
                        });

                        $scope.viewManager.pushView({
                            id: 'student-start-activation-instructions',
                            breadcrumb: 'Activation Instructions',
                            scope: viewScope,
                            showBackButton: true,
                            backTarget: 'student-dashboard',
                            title: activation.assessmentTitle,
                            template: '<ccc-student-start-activation-instructions activation="activation"></ccc-student-start-activation-instructions>'
                        });
                    };

                    var addInstructionsView = function (activation) {

                        var viewScope = $scope.$new();

                        viewScope.activation = activation;

                        viewScope.$on('ccc-student-assessment-instructions.accepted', function (event) {
                            addStartAssessmentView(activation);
                        });

                        viewScope.$on('ccc-student-assessment-instructions.declined', function (event) {
                            $scope.viewManager.goToView('student-dashboard', true);
                        });

                        viewScope.$on('ccc-student-assessment-instructions.noAssessmentInstructions', function () {
                            addStartAssessmentView(activation);
                        });

                        $scope.viewManager.pushView({
                            id: 'student-assessment-instructions',
                            breadcrumb: 'CCC_STUDENT.WORKFLOW.STUDENT_DASHBOARD.BREADCRUMB.BACK',
                            scope: viewScope,
                            showBackButton: true,
                            backTarget: 'student-dashboard',
                            title: activation.assessmentTitle,
                            template: '<ccc-student-assessment-instructions activation="activation"></ccc-student-assessment-instructions>'
                        });

                    };

                    var addGeneralInstructionsView = function (activation) {

                        var viewScope = $scope.$new();

                        viewScope.activation = activation;

                        viewScope.$on('ccc-student-general-instructions.accepted', function (event) {
                            addInstructionsView(activation);
                        });

                        viewScope.$on('ccc-student-general-instructions.declined', function (event) {
                            $scope.viewManager.goToView('student-dashboard', true);
                        });

                        $scope.viewManager.pushView({
                            id: 'student-general-instructions',
                            title: activation.assessmentTitle,
                            breadcrumb: 'CCC_STUDENT.WORKFLOW.STUDENT_DASHBOARD.BREADCRUMB.BACK',
                            backTarget: 'student-dashboard',
                            scope: viewScope,
                            template: '<ccc-student-general-instructions student="student" activation="activation"></ccc-student-general-instructions>'
                        });
                    };

                    var addAuthorizeActivationView = function (activation) {

                        var viewScope = $scope.$new();

                        viewScope.activation = activation;

                        viewScope.$on('ccc-student-start-activation.cancel', function () {
                            $scope.viewManager.goToView('student-dashboard', true);
                        });

                        viewScope.$on('ccc-student-start-activation.authorized', function () {
                            addGeneralInstructionsView(activation);
                        });

                        viewScope.$on('ccc-student-start-activation.denied', function () {
                            $scope.viewManager.goToView('student-dashboard', true);
                        });

                        $scope.viewManager.pushView({
                            id: 'student-start-activation',
                            breadcrumb: 'CCC_STUDENT.WORKFLOW.STUDENT_DASHBOARD.BREADCRUMB.BACK',
                            scope: viewScope,
                            showBackButton: true,
                            title: activation.assessmentTitle,
                            template: '<ccc-student-authorize-activation activation="activation"></ccc-student-authorize-activation>'
                        });
                    };

                    var addStudentTestResultsView = function (collegeId, placementId) {

                        var viewScope = $scope.$new();

                        viewScope.collegeId = collegeId;
                        viewScope.placementId = placementId;

                        $scope.viewManager.pushView({
                            id: 'student-test-results',
                            title: 'CCC_STUDENT.WORKFLOW.STUDENT_DASHBOARD.TEST_RESULTS',
                            breadcrumb: 'CCC_STUDENT.WORKFLOW.STUDENT_DASHBOARD.TEST_RESULTS',
                            scope: viewScope,
                            template: [
                                '<ccc-student-test-results-view college-id="collegeId" placement-id="placementId"></ccc-student-test-results-view>'
                            ].join('')
                        });
                    };

                    var addStudentTestResultsViewFromActivation = function (assessmentSessionId, assessmentTitle, completedDate, collegeName) {

                        var viewScope = $scope.$new();

                        viewScope.assessmentSessionId = assessmentSessionId;
                        viewScope.assessmentTitle = assessmentTitle;
                        viewScope.completedDate = completedDate;
                        viewScope.collegeName = collegeName;

                        $scope.viewManager.pushView({
                            id: 'student-test-results',
                            title: 'CCC_STUDENT.WORKFLOW.STUDENT_DASHBOARD.TEST_RESULTS',
                                                        breadcrumb: 'CCC_STUDENT.WORKFLOW.STUDENT_DASHBOARD.TEST_RESULTS',
                            scope: viewScope,
                            template: [
                                '<ccc-student-test-results ',
                                    'assessment-session-id="assessmentSessionId" ',
                                    ' assessment-title="assessmentTitle" ',
                                    ' completed-date="completedDate" ',
                                    ' college-name="collegeName" ',
                                '></ccc-student-test-results>'
                            ].join('')
                        });
                    };

                    var addStudentPlacementView = function (activation, completedActivations, college) {

                        var viewScope = $scope.$new();

                        viewScope.selectedActivationId = activation ? activation.activationId : false;
                        viewScope.college = college;
                        viewScope.completedActivations = completedActivations;

                        viewScope.$on('ccc-student-placement-view.done', function () {
                            $scope.viewManager.popView();
                        });

                        viewScope.$on('ccc-student-placement-view.viewAssessment', function (e, collegeId, placementId) {
                            addStudentTestResultsView(collegeId, placementId);
                        });

                        $scope.viewManager.pushView({
                            id: 'student-placement',
                            title: 'CCC_STUDENT.WORKFLOW.STUDENT_DASHBOARD.STUDENT_PLACEMENT',
                            breadcrumb: 'CCC_STUDENT.WORKFLOW.STUDENT_DASHBOARD.STUDENT_PLACEMENT_BREADCRUMB',
                            scope: viewScope,
                            template: '<ccc-student-placement-view selected-activation-id="selectedActivationId" completed-activations="completedActivations" college="college"></ccc-student-placement-view>'
                        });
                    };

                    var addCollegeSelectionView = function (activation, completedActivations) {

                        var viewScope = $scope.$new();

                        viewScope.$on('ccc-student-college-selection-view.collegeSelected', function (e, college) {
                            addStudentPlacementView(activation, completedActivations, college.cccId);
                        });

                        $scope.viewManager.pushView({
                            id: 'student-college-selection',
                            title: 'CCC_STUDENT.WORKFLOW.STUDENT_DASHBOARD.COLLEGE_SELECTION',
                            breadcrumb: 'CCC_STUDENT.WORKFLOW.STUDENT_DASHBOARD.COLLEGE_SELECTION_BREADCRUMB',
                            scope: viewScope,
                            template: '<ccc-student-college-selection-view></ccc-student-college-selection-view>'
                        });
                    };

                    var addStudentDashboardView = function () {

                        var completedActivations = [];

                        var viewScope = $scope.$new();

                        viewScope.student = $scope.student;

                        viewScope.$on('ccc-student-activations.selected', function (event, activation, activationElement) {

                            // upcoming activations that are not in progress cannot be started
                            if (activation.isInFuture && activation.status === 'READY') {

                                launchUpcomingModal(activation);

                            } else {

                                if (activation.requirePasscode === false) {
                                    addGeneralInstructionsView(activation);
                                } else {
                                    addAuthorizeActivationView(activation);
                                }
                            }
                        });

                        viewScope.$on('ccc-student-activations.getPlacement', function (event, activation) {

                            var availablePlacementCollegeIds = CurrentStudentService.getAvailableCollegeForPlacementIds();

                            // if there are no colleges for placement, then move forward passing along false to the next view
                            if (availablePlacementCollegeIds.length === 0) {

                                addStudentPlacementView(activation, completedActivations, false);

                            // if student has access to only one location move directly on to placement
                            } else if (availablePlacementCollegeIds.length === 1) {

                                addStudentPlacementView(activation, completedActivations, availablePlacementCollegeIds[0]);

                            // if there is more than one college available for placement the student needs to select one
                            } else {
                                addCollegeSelectionView(activation, completedActivations);
                            }
                        });

                        viewScope.$on('ccc-student-activations.getPlacementsForCollege', function (event, college) {
                            addStudentPlacementView(false, completedActivations, college);
                        });

                        viewScope.$on('ccc-student-activations.activationsLoaded', function (event, activations) {
                            completedActivations = activations.completed;
                        });

                        viewScope.$on('ccc-student-activations.showTestResults', function (e, activation) {
                            addStudentTestResultsViewFromActivation(activation.currentAssessmentSessionId, activation.assessmentTitle, activation._completedDateTimeStamp, activation.collegeName);
                        });

                        $scope.viewManager.pushView({
                            id: 'student-dashboard',
                            title: 'CCC_STUDENT.WORKFLOW.STUDENT_DASHBOARD.MY_ASSESSMENTS',
                            breadcrumb: 'CCC_STUDENT.WORKFLOW.STUDENT_DASHBOARD.BREADCRUMB.BACK',
                            scope: viewScope,
                            template: '<ccc-student-activations student="student" disabled="loadingColleges"></ccc-student-activations>'
                        });
                    };


                    /*============ BEHAVIOR ==============*/


                    /*============ LISTENERS ==============*/

                    $scope.viewManager.addListener('viewFocused', function (viewId) {

                        if (viewId === 'student-dashboard' && activationsChanged) {
                            $scope.$broadcast('ccc-student-activations.requestRefresh');
                            activationsChanged = false;
                        }
                    });


                    /*============ INITIALIZATION ==============*/

                    addStudentDashboardView();

                }
            ],

            template: [
                '<ccc-view-manager view-manager="viewManager"></ccc-view-manager>'
            ].join('')

        };

    });

})();
