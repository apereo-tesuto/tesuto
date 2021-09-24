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

    angular.module('CCC.View.Home').directive('cccWorkflowBulkActivation', function () {

        return {

            restrict: 'E',

            scope: {
                'assessments': '=?',
                'initialDeliveryType': '@?',
                'onCompleteState': '@?'
            },

            controller: [

                '$scope',
                '$state',
                '$translate',
                'ViewManagerEntity',
                'NavigationFreezeService',
                'ModalService',

                function ($scope, $state, $translate, ViewManagerEntity, NavigationFreezeService, ModalService) {

                    /*============ PRIVATE VARIABLES =============*/

                    var studentsToActivateCache = [];

                    var windowBlockerTitle = 'You will lose all progress.';


                    /*============ MODEL ==============*/

                    // use a view manager to handler the workflow between screens
                    $scope.viewManager = new ViewManagerEntity({});


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var resetWorkflow = function () {
                        studentsToActivateCache = [];
                        $scope.$broadcast('ccc-bulk-activation-add-assessment.requestReset');
                    };

                    var fetchWindowBlockerTitle = function () {
                        $translate('CCC_VIEW_HOME.WORKFLOW.BULK_ACTIVATION.PROGRESS_WARNING.TITLE').then(function (translation) {
                            windowBlockerTitle = translation;
                        });
                    };

                    var showRouteBlockingModal = function () {

                        var routeBlockingModal = ModalService.openConfirmModal({
                            title: 'CCC_VIEW_HOME.WORKFLOW.BULK_ACTIVATION.PROGRESS_WARNING.TITLE',
                            message: 'CCC_VIEW_HOME.WORKFLOW.BULK_ACTIVATION.PROGRESS_WARNING.MESSAGE'
                        });

                        routeBlockingModal.result.then(function () {
                            NavigationFreezeService.stopBlocking(true);
                        });
                    };

                    var updateStudentCache = function (studentsToActivate) {

                        studentsToActivateCache = studentsToActivate;

                        if (studentsToActivateCache.length > 0) {

                            NavigationFreezeService.startBlocking(showRouteBlockingModal, windowBlockerTitle);

                        } else {
                            NavigationFreezeService.stopBlocking();
                        }
                    };

                    var addActivationSummaryView = function (assessments, students, location) {

                        var viewScope = $scope.$new();

                        viewScope.assessments = assessments;
                        viewScope.students = students;
                        viewScope.location = location;

                        $scope.viewManager.pushView({
                            id: 'bulk-activation-activation-summary-view',
                            title: 'CCC_VIEW_HOME.WORKFLOW.BULK_ACTIVATION.BREADCRUMB.ACTIVATION_RESULTS',
                            breadcrumb: 'CCC_VIEW_HOME.WORKFLOW.BULK_ACTIVATION.BREADCRUMB.ACTIVATION_RESULTS',
                            backTarget: 'bulk-activation-add-assessment-view',
                            scope: viewScope,
                            template: '<ccc-bulk-activation-summary assessments="assessments" students="students" location="location"></ccc-bulk-activation-summary>'
                        });

                        resetWorkflow();

                        viewScope.$on('ccc-bulk-activation-summary.done', function () {

                            if ($scope.onCompleteState) {
                                $state.go($scope.onCompleteState);
                            } else {
                                $state.go('home');
                            }
                        });
                    };

                    var addStudentView = function (assessments) {

                        var viewScope = $scope.$new();

                        viewScope.assessments = assessments;
                        viewScope.studentsToActivate = studentsToActivateCache;

                        $scope.viewManager.pushView({
                            id: 'bulk-activation-add-student-view',
                            title: 'CCC_VIEW_HOME.WORKFLOW.BULK_ACTIVATION.BREADCRUMB.BULK_ACTIVATION',
                            breadcrumb: 'CCC_VIEW_HOME.WORKFLOW.BULK_ACTIVATION.BREADCRUMB.BULK_ACTIVATION',
                            scope: viewScope,
                            template: '<ccc-bulk-activation-student-search assessments="assessments" students-to-activate="studentsToActivate" allow-edit="true"></ccc-bulk-activation-student-search>'
                        });

                        viewScope.$on('ccc-bulk-activation-student-search.studentsChanged', function (event, studentsToActivate) {
                            updateStudentCache(studentsToActivate);
                        });

                        viewScope.$on('ccc-bulk-activation-student-search.edit', function (event) {
                            $scope.viewManager.popView();
                        });

                        viewScope.$on('ccc-bulk-activation-student-search.created', function (e, assessments, students, location) {
                            NavigationFreezeService.stopBlocking();
                            addActivationSummaryView(assessments, students, location);
                        });

                        viewScope.$on('ccc-bulk-activation-student-search.cancel', function () {
                            if ($scope.onCompleteState) {
                                $state.go($scope.onCompleteState);
                            } else {
                                $state.go('home');
                            }
                        });
                    };

                    var addAssessmentView = function () {

                        var viewScope = $scope.$new();
                        var assessments;

                        $scope.viewManager.pushView({
                            id: 'bulk-activation-add-assessment-view',
                            title: 'CCC_VIEW_HOME.WORKFLOW.BULK_ACTIVATION.BREADCRUMB.BULK_ACTIVATION',
                            breadcrumb: 'CCC_VIEW_HOME.WORKFLOW.BULK_ACTIVATION.BREADCRUMB.BULK_ACTIVATION',
                            scope: viewScope,
                            template: '<ccc-bulk-activation-add-assessment initial-delivery-type="{{initialDeliveryType}}"></ccc-bulk-activation-add-assessment>'
                        });

                        viewScope.$on('ccc-bulk-activation-add-assessment.submit', function (event, assessments_in) {
                            assessments = assessments_in;
                            addStudentView(assessments);
                        });
                    };


                    /*============ BEHAVIOR ==============*/

                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-bulk-activation-student-search.created', function () {
                        $scope.$broadcast('ccc-bulk-activation-add-assessment.requestReset');
                    });


                    /*============ INITIALIZATION ==============*/

                    fetchWindowBlockerTitle();
                    addAssessmentView();
                }
            ],

            template: [
                '<ccc-view-manager view-manager="viewManager"></ccc-view-manager>'
            ].join('')

        };

    });

})();
