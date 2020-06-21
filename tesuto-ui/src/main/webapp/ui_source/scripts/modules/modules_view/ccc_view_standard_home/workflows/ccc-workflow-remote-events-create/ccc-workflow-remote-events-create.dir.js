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

    angular.module('CCC.View.Home').directive('cccWorkflowRemoteEventsCreate', function () {

        return {

            restrict: 'E',

            scope: {
                collegeId: '='
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                'ViewManagerEntity',
                'EventClass',
                'RemoteEventService',

                function ($scope, $element, $timeout, ViewManagerEntity, EventClass, RemoteEventService) {

                    /*============ MODEL ==============*/

                    // use a view manager to handler the workflow between screens
                    $scope.viewManager = new ViewManagerEntity({});


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var addRemoteEventStudentsSummaryView = function (remoteEvent, studentSummary) {

                        var viewScope = $scope.$new();

                        viewScope.remoteEvent = remoteEvent;
                        viewScope.studentSummary = studentSummary;

                        RemoteEventService.attachAllMetaData(remoteEvent, true);

                        // immediatly trigger an update on the summary view because students have changed
                        $scope.$broadcast('ccc-remote-events-create-summary.refresh', remoteEvent);

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

                    var addRemoteEventSummaryView = function (remoteEvent) {

                        var viewScope = $scope.$new();

                        viewScope.remoteEvent = remoteEvent;

                        RemoteEventService.attachAllMetaData(viewScope.remoteEvent);

                        viewScope.$on('ccc-remote-events-create-summary.done', function (e, remoteEvent) {
                            $scope.$emit('ccc-workflow-remote-events-create.done', remoteEvent);
                        });

                        viewScope.$on('ccc-remote-events-create-summary.editStudents', function (e, remoteEvent) {
                            addRemoteEventStudentsView(remoteEvent);
                        });

                        viewScope.$on('ccc-remote-events-create-summary.editDetails', function (e, remoteEvent) {
                            $scope.viewManager.popView();
                        });

                        $scope.viewManager.pushView({
                            id: 'remote-events-create-summary',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-REMOTE-EVENTS-CREATE.REMOTE_PROCTOR_SUMMARY',
                            breadcrumb: 'CCC_VIEW_HOME.CCC-WORKFLOW-REMOTE-EVENTS-CREATE.REMOTE_PROCTOR_SUMMARY',
                            scope: viewScope,
                            showBackButton: true,
                            template: '<ccc-remote-events-create-summary remote-event="remoteEvent"></ccc-remote-events-create-summary>'
                        });
                    };

                    var addEventDetailsView = function () {

                        var viewScope = $scope.$new();

                        viewScope.remoteEvent = new EventClass({collegeId: $scope.collegeId});

                        viewScope.$on('ccc-remote-events-details-create.cancel', function () {
                            $scope.$emit('ccc-workflow-remote-events-create.cancel');
                        });

                        viewScope.$on('ccc-remote-events-details-create.done', function (e, createdRemoteEvent) {
                            $scope.$emit('ccc-workflow-remote-events-create.done', createdRemoteEvent);
                        });

                        viewScope.$on('ccc-remote-events-details-create.saved', function (e, remoteEvent) {
                            // TODO: need to cache this remote event?
                            addRemoteEventSummaryView(remoteEvent);
                        });

                        $scope.viewManager.pushView({
                            id: 'remote-events-create',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-REMOTE-EVENTS-CREATE.EDIT_EVENT_DETAILS',
                            breadcrumb: 'CCC_VIEW_HOME.CCC-WORKFLOW-REMOTE-EVENTS-CREATE.EDIT_EVENT_DETAILS',
                            scope: viewScope,
                            showBackButton: true,
                            template: '<ccc-remote-events-details-create remote-event="remoteEvent"></ccc-remote-events-details-create>'
                        });
                    };

                    /*============ BEHAVIOR ==============*/

                    /*============ LISTENERS ==============*/

                    /*============ INITIALIZATION ==============*/

                    addEventDetailsView();
                }
            ],

            template: [
                '<ccc-view-manager view-manager="viewManager"></ccc-view-manager>'
            ].join('')

        };

    });

})();
