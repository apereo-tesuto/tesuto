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

    angular.module('CCC.View.Home').directive('cccWorkflowRemoteEvents', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                '$element',
                '$timeout',
                '$stateParams',
                'ViewManagerEntity',
                'NotificationService',
                'EventsAPIService',

                function ($scope, $element, $timeout, $stateParams, ViewManagerEntity, NotificationService, EventsAPIService) {

                    /*============ MODEL ==============*/

                    // use a view manager to handler the workflow between screens
                    $scope.viewManager = new ViewManagerEntity({});


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var addEditEventWorkflow = function (testEvent) {

                        var eventDetailsChanged = false;

                        var viewScope = $scope.$new();

                        viewScope.testEvent = testEvent.clone();

                        viewScope.$on('ccc-workflow-remote-events-edit.done', function () {

                            $scope.viewManager.popView();

                            // they could have changed the dates or the title etc..
                            if (eventDetailsChanged) {
                                $scope.$broadcast('ccc-remote-events.refresh');
                            }
                        });

                        viewScope.$on('ccc-workflow-remote-events-edit.remoteEventUpdated', function () {
                            eventDetailsChanged = true;
                        });

                        viewScope.$on('ccc-workflow-remote-events-edit.eventCancelled', function (e, remoteEvent) {

                            $scope.viewManager.popView();
                            $scope.$broadcast('ccc-remote-events.refresh');

                            EventsAPIService.cancellationEmail(remoteEvent.testEventId).then(function () {

                                NotificationService.open({
                                    icon: 'fa fa-check-circle-o',
                                    title: ' "'+ remoteEvent.name + '" ',
                                    message: 'has been cancelled.'
                                },
                                {
                                    delay: 5000,
                                    type: "info",
                                    allow_dismiss: true
                                });

                            }, function () {

                                NotificationService.open({
                                    icon: 'fa fa-exclamation-triangle',
                                    title: ' Cancellation email failed to send for "'+ remoteEvent.name + '".',
                                    message: ''
                                },
                                {
                                    delay: 0,
                                    type: "danger",
                                    allow_dismiss: true
                                });
                            });
                        });

                        $scope.viewManager.pushView({
                            id: 'remote-events-edit',
                            scope: viewScope,
                            showBackButton: true,
                            isNested: true,
                            template: '<ccc-workflow-remote-events-edit test-event="testEvent"></ccc-workflow-remote-events-edit>'
                        });
                    };

                    var addCreateEventWorkflow = function (collegeId) {

                        var viewScope = $scope.$new();

                        viewScope.collegeId = collegeId;

                        viewScope.$on('ccc-workflow-remote-events-create.cancel', function (e, remoteEvent) {
                            $scope.viewManager.popView();
                        });

                        viewScope.$on('ccc-workflow-remote-events-create.done', function (e, remoteEvent) {

                            $scope.viewManager.popView();
                            $scope.$broadcast('ccc-remote-events.refresh');

                            EventsAPIService.creationEmail(remoteEvent.testEventId).then(function () {

                                NotificationService.open({
                                    icon: 'fa fa-thumbs-up',
                                    title: ' "'+ remoteEvent.name + '" created and email was sent to remote proctor.',
                                    message: ''
                                },
                                {
                                    delay: 5000,
                                    type: "success",
                                    allow_dismiss: true
                                });

                            }, function () {

                                NotificationService.open({
                                    icon: 'fa fa-exclamation-triangle',
                                    title: ' Creation email failed to send for "'+ remoteEvent.name + '".',
                                    message: ''
                                },
                                {
                                    delay: 0,
                                    type: "danger",
                                    allow_dismiss: true
                                });
                            });
                        });

                        $scope.viewManager.pushView({
                            id: 'remote-events-new',
                            scope: viewScope,
                            showBackButton: true,
                            isNested: true,
                            template: '<ccc-workflow-remote-events-create college-id="collegeId"></ccc-workflow-remote-events-create>'
                        });
                    };

                    var addRemoteEventsView = function () {

                        var viewScope = $scope.$new();

                        // can be passed in to auto open the specified test event if it is active
                        viewScope.initialTestEventId = $stateParams.testEventId;

                        viewScope.$on('ccc-remote-event.addEvent', function (e, collegeId) {
                            addCreateEventWorkflow(collegeId);
                        });

                        viewScope.$on('ccc-remote-events.activeTestEventSelected', function (e, testEvent) {
                            addEditEventWorkflow(testEvent);
                        });

                        $scope.viewManager.pushView({
                            id: 'remote-events-home',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-REMOTE-EVENTS.REMOTE_EVENTS',
                            breadcrumb: 'CCC_VIEW_HOME.CCC-WORKFLOW-REMOTE-EVENTS.REMOTE_EVENTS',
                            scope: viewScope,
                            showBackButton: true,
                            template: '<ccc-remote-events initial-test-event-id="initialTestEventId"></ccc-remote-events>'
                        });
                    };


                    /*============ BEHAVIOR ==============*/

                    /*============ LISTENERS ==============*/

                    /*============ INITIALIZATION ==============*/

                    addRemoteEventsView();
                }
            ],

            template: [
                '<ccc-view-manager view-manager="viewManager"></ccc-view-manager>'
            ].join('')

        };

    });

})();
