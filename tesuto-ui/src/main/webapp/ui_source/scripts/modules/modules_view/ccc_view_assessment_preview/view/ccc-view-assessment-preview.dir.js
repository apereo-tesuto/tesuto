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
     * MAIN VIEW DIRECTIVE FOR THE ASSESSMENT PREVIEW VIEW
     */

    angular.module('CCC.View.AssessmentPreview').directive('cccViewAssessmentPreview', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                '$q',
                'ModalService',
                'SESSION_CONFIGS',
                'PreviewTaskSetService',                // this is this views service which contains all the logic for task retrieval and submission
                'TaskSetServiceClass',                  // this is the class we will create and pass along to the asmt player
                'AsmtPlayerEventTransformService',      // a service that stands between raw angular events and the ActivityServiceClass (allows us to tack on additional info for each event like assessmentSessionId)
                'ActivityServiceClass',                 // class we need to instantiate that will handle gathering events and posting them as activities
                'ASMT_PLAYER_EVENT_FILTER_TRANSFORM_MAP',     // filter and transform map for assessment player events

                function ($scope, $q, ModalService, SESSION_CONFIGS, PreviewTaskSetService, TaskSetServiceClass, AsmtPlayerEventTransformService, ActivityServiceClass, ASMT_PLAYER_EVENT_FILTER_TRANSFORM_MAP) {

                    /*=========== PRIVATE VARIABLES AND METHODS ============*/

                    var showCompletionModal = function () {

                        var completionModal = ModalService.open({
                            allowClose: false,
                            size: 'md',
                            backdrop: 'static',
                            template: '<ccc-asmt-preview-complete-modal modal="modal"></cccc-asmt-preview-complete-modal>'
                        });

                        completionModal.result.then(function () {
                            window.close();
                        });
                    };

                    var showCloseWindoModal = function () {
                        ModalService.openAlertModal('CCC_VIEW_ASSESSMENT_PREVIEW.CLOSE_WINDOW_MODAL.TITLE', 'CCC_VIEW_ASSESSMENT_PREVIEW.CLOSE_WINDOW_MODAL.BODY', 'info');
                    };

                    var startCompletionWorkflow = function () {

                        // ask the player to disable it's navigation
                        $scope.$broadcast('ccc-asmt-player-view.requestDisable');

                        if (window.opener) {
                            showCompletionModal();
                        } else {
                            showCloseWindoModal();
                        }
                    };

                    var startGetScoreWorkflow = function () {

                        // ask the player to disable it's navigation
                        $scope.$broadcast('ccc-asmt-player-view.requestDisable');

                        var completionModal = ModalService.open({
                            allowClose: false,
                            size: 'md',
                            backdrop: 'static',
                            template: '<ccc-asmt-preview-complete-modal modal="modal"></cccc-asmt-preview-complete-modal>'
                        });

                        completionModal.result.then(function () {
                            window.close();
                        });
                    };


                    /*=========== MODEL ===========*/

                    $scope.assessment = SESSION_CONFIGS['assessmentSession'];

                    $scope.user = SESSION_CONFIGS['user'];

                    $scope.taskSetServiceClassInstance = new TaskSetServiceClass({
                        getNextTaskSet: PreviewTaskSetService.getNextTaskSet,
                        getPreviousTaskSet: PreviewTaskSetService.getPreviousTaskSet,
                        updateTaskSetResponses: PreviewTaskSetService.updateTaskSetResponses,
                        completeTaskSet: PreviewTaskSetService.completeTaskSet,
                        scoreTaskSet: PreviewTaskSetService.scoreTaskSet,
                        isScoreEnabled: PreviewTaskSetService.isScoreEnabled
                    });


                    /*=========== LISTENERS ===========*/

                    // when the player says no next task then we are all done, time to start completion workflow
                    $scope.$on('ccc-asmt-player.noNextTaskSet', startCompletionWorkflow);
                    $scope.$on('ccc-asmt-player.onGetScore', startGetScoreWorkflow);

                    /*=========== INITIALIZATION ===========*/

                    // start the activity service... attaching to scope for the reason that the linter will yell at us
                    $scope.PreviewPlayerActivityService = new ActivityServiceClass({
                        channels: [
                            {
                                id: 'ccc',
                                required: true,
                                eventFilterMap: ASMT_PLAYER_EVENT_FILTER_TRANSFORM_MAP,
                                postActivity: function (activityList) {
                                    return $q.when(true);
                                }
                            }
                        ],
                        eventTransform: AsmtPlayerEventTransformService.eventTransform,
                        setKeepAllActivities: true
                    });

                }
            ],

            template: [

                '<ccc-asmt-player-view task-set-service="taskSetServiceClassInstance" assessment="assessment" user="user" initial-task-set="assessment.currentTaskSet"></ccc-asmt-player-view>'

            ].join('')

        };

    });

})();
