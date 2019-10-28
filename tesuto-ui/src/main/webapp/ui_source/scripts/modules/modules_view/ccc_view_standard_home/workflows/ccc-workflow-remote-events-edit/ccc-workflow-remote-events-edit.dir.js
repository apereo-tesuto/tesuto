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

    angular.module('CCC.View.Home').directive('cccWorkflowRemoteEventsEdit', function () {

        return {

            restrict: 'E',

            scope: {
                testEvent: '='
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                'ViewManagerEntity',
                'NotificationService',
                'RemoteEventService',

                function ($scope, $element, $timeout, ViewManagerEntity, NotificationService, RemoteEventService) {

                    /*============ MODEL ==============*/

                    // use a view manager to handler the workflow between screens
                    $scope.viewManager = new ViewManagerEntity({});


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var addRemoteEventStudentsSummaryView = function (remoteEvent, studentSummary) {

                        var viewScope = $scope.$new();

                        viewScope.remoteEvent = remoteEvent;
                        viewScope.studentSummary = studentSummary;

                        // immediatly trigger an update on the summary view because students have changed
                        $scope.$broadcast('ccc-remote-events-edit-summary.refresh', false, false);

                        viewScope.$on('ccc-remote-events-students-summary.done', function (e, remoteEvent) {
                            $scope.viewManager.goToView('remote-events-create-summary', true);
                        });

                        $scope.viewManager.pushView({
                            id: 'remote-events-create-students-summary',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-REMOTE-EVENTS-CREATE.STUDENT_UPDATES',
                            breadcrumb: 'CCC_VIEW_HOME.CCC-WORKFLOW-REMOTE-EVENTS-CREATE.STUDENT_UPDATES',
                            scope: viewScope,
                            backTarget: 'remote-events-create-summary',
                            template: '<ccc-remote-events-students-summary remote-event="remoteEvent" student-summary="studentSummary"></ccc-remote-events-students-summary>'
                        });
                    };


                    var addRemoteEventStudentsView = function (remoteEvent) {

                        var viewScope = $scope.$new();

                        viewScope.remoteEvent = remoteEvent;

                        viewScope.$on('ccc-remote-events-students.studentsChanged', function (e, remoteEvent, studentSummary) {
                            addRemoteEventStudentsSummaryView(remoteEvent, studentSummary);
                        });

                        viewScope.$on('ccc-remote-events-students.cancel', function (e) {
                            $scope.viewManager.popView();
                        });

                        $scope.viewManager.pushView({
                            id: 'remote-events-create-students',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-REMOTE-EVENTS-CREATE.EDIT_STUDENTS',
                            breadcrumb: 'CCC_VIEW_HOME.CCC-WORKFLOW-REMOTE-EVENTS-CREATE.STUDENT_UPDATES_DONE',
                            scope: viewScope,
                            showBackButton: true,
                            template: '<ccc-remote-events-students remote-event="remoteEvent"></ccc-remote-events-students>'
                        });
                    };

                    var addEventDetailsView = function () {

                        var viewScope = $scope.$new();

                        viewScope.remoteEvent = $scope.testEvent.clone();

                        viewScope.$on('ccc-remote-events-details-edit.cancel', function () {
                            $scope.viewManager.popView();
                        });

                        viewScope.$on('ccc-remote-events-details-edit.saved', function (e, remoteEvent) {

                            // update the private testEvent
                            $scope.testEvent = remoteEvent;
                            $scope.$emit('ccc-workflow-remote-events-edit.remoteEventUpdated');

                            // here we use the existing scope from the addRemoteEventSummary view to update the remote event
                            $scope.$broadcast('ccc-remote-events-edit-summary.refresh', remoteEvent, true);

                            $scope.viewManager.popView();

                            NotificationService.open({
                                icon: 'fa fa-check-circle-o',
                                title: ' "'+ remoteEvent.name + '" ',
                                message: 'has been updated.'
                            },
                            {
                                delay: 5000,
                                type: "success",
                                allow_dismiss: true
                            });
                        });

                        $scope.viewManager.pushView({
                            id: 'remote-events-create',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-REMOTE-EVENTS-CREATE.EDIT_EVENT_DETAILS',
                            breadcrumb: 'CCC_VIEW_HOME.CCC-WORKFLOW-REMOTE-EVENTS-CREATE.EDIT_EVENT_DETAILS',
                            scope: viewScope,
                            showBackButton: true,
                            template: '<ccc-remote-events-details-edit remote-event="remoteEvent"></ccc-remote-events-details-edit>'
                        });
                    };

                    var addRemoteEventSummaryView = function () {

                        var viewScope = $scope.$new();

                        viewScope.remoteEvent = $scope.testEvent;

                        viewScope.$on('ccc-remote-events-edit-summary.done', function (e, remoteEvent) {
                            $scope.$emit('ccc-workflow-remote-events-edit.done', remoteEvent);
                        });

                        viewScope.$on('ccc-remote-events-edit-summary.editStudents', function (e, remoteEvent) {
                            addRemoteEventStudentsView(remoteEvent);
                        });

                        viewScope.$on('ccc-remote-events-edit-summary.editDetails', function (e, remoteEvent) {
                            addEventDetailsView();
                        });

                        viewScope.$on('ccc-remote-events-edit-summary.eventCancelled', function (e, remoteEvent) {
                            $scope.$emit('ccc-workflow-remote-events-edit.eventCancelled', remoteEvent);
                        });

                        $scope.viewManager.pushView({
                            id: 'remote-events-create-summary',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-REMOTE-EVENTS-CREATE.REMOTE_PROCTOR_SUMMARY',
                            breadcrumb: 'CCC_VIEW_HOME.CCC-WORKFLOW-REMOTE-EVENTS-CREATE.REMOTE_PROCTOR_SUMMARY',
                            scope: viewScope,
                            showBackButton: true,
                            template: '<ccc-remote-events-edit-summary remote-event="remoteEvent"></ccc-remote-events-edit-summary>'
                        });
                    };


                    /*============ BEHAVIOR ==============*/

                    /*============ LISTENERS ==============*/

                    /*============ INITIALIZATION ==============*/

                    RemoteEventService.attachAllMetaData($scope.testEvent);

                    addRemoteEventSummaryView($scope.testEvent);
                }
            ],

            template: [
                '<ccc-view-manager view-manager="viewManager"></ccc-view-manager>'
            ].join('')

        };

    });

})();
