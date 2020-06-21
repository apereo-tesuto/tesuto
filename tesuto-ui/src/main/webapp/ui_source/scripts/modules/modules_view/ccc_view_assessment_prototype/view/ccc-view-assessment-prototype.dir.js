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

    /**
     * MAIN VIEW DIRECTIVE FOR THE ASSESSMENT PLAYER VIEW
     */

    angular.module('CCC.View.AssessmentPrototype').directive('cccViewAssessmentPrototype', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                '$q',
                '$state',
                'SESSION_CONFIGS',
                'ModalService',
                'PrototypeTaskSetService',                  // this is this views service which contains all the logic for task retrieval and submission

                // Event Listners and Activity Generation
                'ActivityServiceClass',                     // class we need to instantiate that will handle gathering events and posting them as activities
                'AsmtPlayerEventTransformService',          // a service that stands between raw angular events and the ActivityServiceClass (allows us to tack on additional info for each event like assessmentSessionId)
                'ASMT_PLAYER_EVENT_FILTER_TRANSFORM_MAP',   // this generates the final transformed event into an activity ready for posting to the server
                'FakeData',                                 // we use the FakeData service to simulate post activity

                'WindowFocusService',                       // use this to tell if the user is trying to close the window

                function ($scope, $q, $state, SESSION_CONFIGS, ModalService, PrototypeTaskSetService, ActivityServiceClass, AsmtPlayerEventTransformService, ASMT_PLAYER_EVENT_FILTER_TRANSFORM_MAP, FakeData, WindowFocusService) {

                    /*=========== PRIVATE VARIABLES AND METHODS ============*/

                    // mocking google analytics channel
                    var ASMT_PLAYER_MOCKED_GA_FILTER_MAP = {
                        'ccc-asmt-player-toolbar-bottom.nextClicked': function (eventData) {
                            return {
                                eventCategory: 'navigation',
                                eventAction: 'nextClicked',
                                eventLabel: '',
                                eventValue: 1
                            };
                        }
                    };
                    // mocking google analytics channel
                    var mockedGAPostActivity = function (activityList) {
                        return (function (aList) {
                            var deferred = $q.defer();

                            if (Math.round(Math.random()*1) === 0) {
                                deferred.resolve();
                            } else {
                                deferred.reject();
                            }

                            return deferred.promise;
                        })(activityList);
                    };


                    /*=========== MODEL ===========*/

                    $scope.assessment = SESSION_CONFIGS['assessmentSession'];
                    $scope.user = SESSION_CONFIGS['user'];

                    $scope.taskSetServiceClassInstance = PrototypeTaskSetService;


                    /*=========== MODEL DEPENDENT METHODS ============*/

                    var forceActivityFlush = function () {
                        return $scope.activityService.flushActivityLog();
                    };

                    var startCompletionWorkflow = function () {

                        // ask the player to disable it's navigation
                        $scope.$broadcast('ccc-asmt-player-view.requestDisable');

                        // let's make sure we flush all activity now, we can't keep them forever so even if it fails
                        // let the user continue
                        forceActivityFlush().finally(function () {

                            WindowFocusService.stopBlockingCloseWindow();

                            $state.go('assessmentComplete', {
                            	assessmentSessionId: $scope.assessment.assessmentSessionId,
                                assessmentTitle: $scope.assessment.title
                            });
                        });
                    };

                    var startPauseWorkflow = function () {

                        var logoutURL = window.location.origin + '/ccc-assess/logout';
                        window.location = logoutURL;
                    };


                    /*=========== LISTENERS ===========*/

                    // when the player says no next task then we are all done, time to start completion workflow
                    $scope.$on('ccc-asmt-player.noNextTaskSet', startCompletionWorkflow);

                    // when the activity service has an update, update the activity monitor
                    $scope.$on('ActivityService.activityUpdated', function (e) {
                        $scope.$broadcast('ccc-activity-monitor.updateData', $scope.activityService.getCopyOfAllActivityForChannel('ccc'));
                    });

                    // when the player says we should be logging out
                    $scope.$on('ccc-asmt-player.requestPause', startPauseWorkflow);


                    /*=========== INITIALIZATION ===========*/

                    // start the activity service... attaching to scope to avoid the linter
                    $scope.activityService = new ActivityServiceClass({
                        channels: [
                            {
                                id: 'ccc',
                                required: true,
                                eventFilterMap: ASMT_PLAYER_EVENT_FILTER_TRANSFORM_MAP,
                                postActivity: function (activityList) {
                                    return FakeData.postActivity(activityList);
                                }
                            },
                            {// mocking a googla analytics channel to test multi channel support
                                id: 'ga',
                                eventFilterMap: ASMT_PLAYER_MOCKED_GA_FILTER_MAP,
                                postActivity: function (activityList) {
                                    return mockedGAPostActivity(activityList);
                                }
                            }
                        ],
                        eventTransform: AsmtPlayerEventTransformService.eventTransform,
                        keepAllActivities: true
                    });

                    //WindowFocusService.startBlockingCloseWindow('Are you sure?', forceActivityFlush);

                }
            ],

            template: [

                '<ccc-asmt-player-view task-set-service="taskSetServiceClassInstance" assessment="assessment" user="user" initial-task-set="assessment.currentTaskSet" allow-pause="true"></ccc-asmt-player-view>',
                '<ccc-activity-monitor></ccc-activity-monitor>'

            ].join('')

        };

    });

})();
