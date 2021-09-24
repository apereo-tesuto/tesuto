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

    angular.module('CCC.View.Home').directive('cccWorkflowStudentProfile', function () {

        return {

            restrict: 'E',

            scope: {
                location: '=?',
                student: '=?'
            },

            controller: [

                '$scope',
                'LocationService',
                'ViewManagerEntity',
                'NotificationService',

                function ($scope, LocationService, ViewManagerEntity, NotificationService) {

                    /*============ PRIVATE VARIABLES / METHODS ===========*/

                    /*============ MODEL ==============*/

                    $scope.viewManager = new ViewManagerEntity({});
                    $scope.location = LocationService.getCurrentTestCenter() || null;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var addScoreActivationView = function (activation, student) {

                        var viewScope = $scope.$new();

                        viewScope.activation = activation;
                        viewScope.student = student;
                        viewScope.parentViewManager = $scope.viewManager;

                        viewScope.$on('ccc-workflow-assessment-scoring.cancel', function () {
                            $scope.viewManager.popView();
                        });

                        viewScope.$on('ccc-workflow-assessment-scoring.statusChange', function () {
                            $scope.$broadcast('ccc-student-profile.requestRefresh');
                        });

                        viewScope.$on('ccc-workflow-assessment-scoring.complete', function () {
                            $scope.viewManager.popView();
                            $scope.$broadcast('ccc-student-profile.requestRefresh');
                        });

                        $scope.viewManager.pushView({
                            isNested: true,
                            id: 'proctor-score-activation',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-STUDENT-PROFILE.TITLE.SCORE_ASSESSMENT',
                            breadcrumb: 'CCC_VIEW_HOME.CCC-WORKFLOW-STUDENT-PROFILE.TITLE.SCORE_ASSESSMENT',
                            scope: viewScope,
                            template: '<ccc-workflow-assessment-scoring parent-view-manager="parentViewManager" activation="activation" student="student"></ccc-workflow-assessment-scoring>'
                        });
                    };

                    var addEditActivationView = function (activation, student) {

                        var viewScope = $scope.$new();

                        viewScope.activation = activation.clone();
                        viewScope.student = student;
                        viewScope.location = $scope.location;

                        viewScope.$on('ccc-activation-edit.cancel', function () {
                            $scope.viewManager.popView();
                        });

                        viewScope.$on('ccc-activation-edit.processing', function () {
                            $scope.viewManager.disableNavigation();
                        });

                        viewScope.$on('ccc-activation-edit.success', function () {

                            $scope.$broadcast('ccc-student-profile.requestRefresh');
                            $scope.$emit('ccc-workflow-student-profile.activationUpdated');
                            $scope.viewManager.goToView('proctor-student-profile');

                            NotificationService.open({
                                icon: 'fa fa-thumbs-up',
                                title: ' Activation successfully updated',
                                message: ''
                            },
                            {
                                delay: 5000,
                                type: "success",
                                allow_dismiss: true
                            });
                        });

                        $scope.viewManager.pushView({
                            id: 'proctor-edit-activation',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-STUDENT-PROFILE.TITLE.EDIT_ACTIVATION',
                            breadcrumb: 'CCC_VIEW_HOME.CCC-WORKFLOW-STUDENT-PROFILE.TITLE.EDIT_ACTIVATION',
                            scope: viewScope,
                            template: '<ccc-activation-edit activation="activation" student="student" location="location"></ccc-activation-edit>'
                        });
                    };

                    var addCreationSummaryView = function (resultsSummary, student) {

                        var viewScope = $scope.$new();

                        viewScope.summary = resultsSummary;
                        viewScope.student = student;

                        viewScope.$on('ccc-activate-student-summary.okay', function () {
                            $scope.viewManager.goToView('proctor-student-profile', true);
                        });

                        $scope.viewManager.enableNavigation();

                        viewScope.$on('ccc-activate-student-summary.confirm', function () {
                            $scope.viewManager.goToView('proctor-student-search', true);
                            $scope.$broadcast('ccc-student-lookup.request-clear');
                        });

                        $scope.viewManager.pushView({
                            id: 'proctor-create-activation-summary',
                            title: 'CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.BREADCRUMB.ACTIVATION_COMPLETE',
                            breadcrumb: 'CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.BREADCRUMB.ACTIVATION_COMPLETE',
                            scope: viewScope,
                            backButton: 'CCC_VIEW_HOME.WORKFLOW.STUDENT_ACTIVATION.BREADCRUMB.STUDENT_PROFILE',
                            backTarget: 'proctor-student-profile',
                            template: '<ccc-activate-student-summary student="student" summary="summary"></ccc-activate-student-summary>'
                        });
                    };

                    var addStudentTestResultsView = function (assessmentSessionId, assessmentTitle, completedDate, collegeName) {

                        var viewScope = $scope.$new();

                        viewScope.assessmentSessionId = assessmentSessionId;
                        viewScope.assessmentTitle = assessmentTitle;
                        viewScope.completedDate = completedDate;
                        viewScope.collegeName = collegeName;

                        $scope.viewManager.pushView({
                            id: 'proctor-student-test-results',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-STUDENT-PROFILE.TITLE.STUDENT_TEST_RESULTS',
                            breadcrumb: 'CCC_VIEW_HOME.CCC-WORKFLOW-STUDENT-PROFILE.TITLE.STUDENT_TEST_RESULTS',
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

                    var addActivateStudentView = function (student) {

                        var viewScope = $scope.$new();

                        viewScope.student = student;
                        viewScope.location = $scope.location;

                        viewScope.$on('ccc-activate-student.cancel', function () {
                            $scope.viewManager.popView();
                        });

                        viewScope.$on('ccc-activate-student.processing', function () {
                            $scope.viewManager.disableNavigation();
                        });

                        viewScope.$on('ccc-activate-student.created', function (event, resultsSummary) {
                            addCreationSummaryView(resultsSummary, student);
                        });

                        viewScope.$on('LocationService.currentTestCenterUpdated', function (event, location) {
                            viewScope.location = location;
                            $scope.$broadcast('ccc-activate-student.requestRefresh', location);
                        });

                        $scope.viewManager.pushView({
                            id: 'proctor-activate-student',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-STUDENT-PROFILE.TITLE.ACTIVATE_STUDENT',
                            breadcrumb: 'CCC_VIEW_HOME.CCC-WORKFLOW-STUDENT-PROFILE.TITLE.ACTIVATE_STUDENT',
                            scope: viewScope,
                            template: '<ccc-activate-student student="student" location="location"></ccc-activate-student>'
                        });
                    };

                    var addPlacementView = function (student, selectedAssessment, assessmentHistory) {

                        var viewScope = $scope.$new();

                        viewScope.student = student;

                        // TODO: Do we need this?
                        viewScope.selectedAssessment = selectedAssessment;
                        viewScope.assessmentHistory = assessmentHistory;

                        viewScope.$on('ccc-student-placement.done', function () {
                            $scope.viewManager.popView();
                        });

                        viewScope.$on('ccc-student-placement.assessmentComponentClicked', function (e, component) {
                            addStudentTestResultsView(component.assessmentSessionId, component.assessmentTitle, component.assessmentDate, false);
                        });

                        $scope.viewManager.pushView({
                            id: 'proctor-student-placement',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-STUDENT-PROFILE.TITLE.STUDENT_PLACEMENT',
                            breadcrumb: 'CCC_VIEW_HOME.CCC-WORKFLOW-STUDENT-PROFILE.TITLE.STUDENT_PLACEMENT',
                            scope: viewScope,
                            showBackButton: true,
                            template: '<ccc-student-placement student="student"></ccc-student-placement>'
                        });
                    };

                    var addStudentProfileView = function () {
                        var viewScope = $scope.$new();

                        viewScope.student = $scope.student;
                        viewScope.location = $scope.location;

                        viewScope.$on('ccc-student-profile.createNewActivation', function (event, student) {
                            addActivateStudentView(student);
                        });

                        viewScope.$on('ccc-student-profile.viewPlacements', function (event, student) {
                            addPlacementView(viewScope.student, false, viewScope.assessmentHistory);
                        });

                        viewScope.$on('ccc-student-profile.assessmentHistoryLoaded', function (event, assessmentHistory) {
                            viewScope.assessmentHistory = assessmentHistory;
                        });

                        viewScope.$on('ccc-activation-card.getPlacement', function (event, selectedAssessment) {
                            addStudentTestResultsView(selectedAssessment.currentAssessmentSessionId, selectedAssessment.assessmentTitle, selectedAssessment._completedDateTimeStamp, selectedAssessment.collegeName);
                        });

                        viewScope.$on('ccc-activation-card.edit', function (event, activation, student) {
                            addEditActivationView(activation, student);
                        });

                        viewScope.$on('ccc-student-profile.score', function (event, activation, student) {
                            addScoreActivationView(activation, student);
                        });

                        $scope.viewManager.pushView({
                            id: 'proctor-student-profile',
                            title: 'CCC_VIEW_HOME.WORKFLOW.PROCTOR_LOCATION.STUDENT_PROFILE',
                            breadcrumb: 'CCC_VIEW_HOME.WORKFLOW.PROCTOR_LOCATION.STUDENT_PROFILE',
                            scope: viewScope,
                            template: '<ccc-student-profile student="student" location="location"></ccc-proctor-location>'
                        });
                    };


                    /*============ BEHAVIOR ==============*/


                    /*============ LISTENERS ==============*/

                    $scope.$on('LocationService.currentTestCenterUpdated', function (event, location) {
                        $scope.location = location;
                        $scope.$emit('ccc-activate-student.requestRefresh', location);
                    });

                    $scope.$on('ccc-activate-student.created', function () {
                        $scope.$broadcast('ccc-student-profile.requestRefresh');
                    });

                    $scope.$on('ccc-activate-student-summary.deactivated', function () {
                        $scope.$broadcast('ccc-student-profile.requestRefresh');
                    });

                    $scope.$on('ccc-activation-card.deactivate', function () {
                        $scope.$broadcast('ccc-student-profile.requestRefresh');
                    });

                    $scope.$on('ccc-activation-card.reactivate', function () {
                        $scope.$broadcast('ccc-student-profile.requestRefresh');
                    });


                    /*============ INITIALIZATION ==============*/

                    addStudentProfileView();

                }
            ],

            template: [
                '<ccc-view-manager view-manager="viewManager"></ccc-view-manager>'
            ].join('')

        };

    });

})();
