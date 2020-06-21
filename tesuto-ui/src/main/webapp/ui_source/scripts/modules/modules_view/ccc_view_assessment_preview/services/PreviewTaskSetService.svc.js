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

    angular.module('CCC.View.AssessmentPreview').service('PreviewTaskSetService', [

        '$q',
        '$http',
        'AssessmentSessionsPreviewAPIService',

        function ($q, $http, AssessmentSessionsPreviewAPIService) {

            /*============ PRIVATE VARIABLES AND METHODS ============*/

            var nextTaskSet = false;

            /*============ SERVICE DECLARATION ============*/

            var PreviewTaskSetService = {};


            /*============ PUBLIC METHODS ============*/

            PreviewTaskSetService.completeTaskSet = function (assessmentSessionId, taskSetId, responses, direction) {

                return AssessmentSessionsPreviewAPIService.completeTaskSet(assessmentSessionId, taskSetId, responses || {}).then(function (nextTaskSetFromServer) {

                    // the contract with the assessment player is to return false on getNextTaskSet when we are all done
                    if (!nextTaskSetFromServer) {

                        nextTaskSet = false;

                    } else {

                        nextTaskSet = {
                            taskSet: nextTaskSetFromServer,

                            // the following could be optional based on whether it is adaptive or linear
                            totalTaskSetCount: false,

                            totalTaskIndex: false,
                            totalTaskCount: false
                        };
                    }
                });
            };

            PreviewTaskSetService.scoreTaskSet = function (assessmentSessionId, taskSetId, responses) {

                return AssessmentSessionsPreviewAPIService.scoreResponse(assessmentSessionId, taskSetId, responses || {}).then(function (scoresFromServer) {
                    return scoresFromServer;
                });
            };

            PreviewTaskSetService.updateTaskSetResponses = function (assessmentSessionId, taskSetId, responses) {

                return AssessmentSessionsPreviewAPIService.updateTaskSetResponses(assessmentSessionId, taskSetId, responses);
            };

            PreviewTaskSetService.getNextTaskSet = function (assessmentSessionId, currentTaskSetId) {
                return $q.when(nextTaskSet);
            };

            PreviewTaskSetService.getPreviousTaskSet = function (assessmentSessionId, currentTaskSetId) {
                return $q.when(nextTaskSet);
            };

            PreviewTaskSetService.scoreTaskSet = function (assessmentSessionId, taskSetId, responses) {

                return AssessmentSessionsPreviewAPIService.scoreTaskSetResponse(assessmentSessionId, taskSetId, responses || {}).then(function (scoresFromServer) {
                    return scoresFromServer;
                });
            };

            PreviewTaskSetService.isScoreEnabled = function () {
                return true;
            };

            /*============ SERVICE PASS BACK ============*/


            return PreviewTaskSetService;
        }
    ]);

})();
