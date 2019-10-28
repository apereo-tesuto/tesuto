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

    angular.module('CCC.View.AssessmentPlayer').service('PlayerTaskSetService', [

        '$q',
        '$http',
        'AssessmentSessionsAPIService',
        'ActivationsAPIService',
        'TaskSetServiceClass',

        function ($q, $http, AssessmentSessionsAPIService, ActivationsAPIService, TaskSetServiceClass) {

            /*============ PRIVATE VARIABLES AND METHODS ============*/

            var nextTaskSet = false;

            var completeTaskSet = function (assessmentSessionId, taskSetId, responses, direction) {

                if (direction === 'next') {

                    return AssessmentSessionsAPIService.completeTaskSet(assessmentSessionId, taskSetId, responses || {}).then(function (nextTaskSetFromServer) {

                        // the contract with the assessment player is to return false on getNextTaskSet when we are all done
                        if (!nextTaskSetFromServer) {

                            nextTaskSet = false;

                        } else {

                            nextTaskSet = {
                                taskSet: nextTaskSetFromServer, // taskSetIndex should be in this object

                                // the following could be optional based on whether it is adaptive or linear
                                totalTaskSetCount: false,

                                totalTaskIndex: false,
                                totalTaskCount: false
                            };
                        }
                    });

                } else if (direction === 'previous') {

                    return AssessmentSessionsAPIService.completeTaskSetPrevious(assessmentSessionId, taskSetId, responses || {}).then(function (previousTaskSetFromServer) {

                        // the contract with the assessment player is to return false on getNextTaskSet when we are all done
                        if (!previousTaskSetFromServer) {

                            nextTaskSet = false;

                        } else {

                            nextTaskSet = {
                                taskSet: previousTaskSetFromServer, // taskSetIndex should be in this object

                                // the following could be optional based on whether it is adaptive or linear
                                totalTaskSetCount: false,

                                totalTaskIndex: false,
                                totalTaskCount: false
                            };
                        }
                    });

                } else {
                    throw new Error('PlayerTaskSetService.completionDirectionNotProvided');
                }
            };

            var pause = function (assessmentSessionId) {
                return ActivationsAPIService.pauseActivationByAssessmentSessionId(assessmentSessionId);
            };

            var updateTaskSetResponses = function (assessmentSessionId, taskSetId, responses) {
                return AssessmentSessionsAPIService.updateTaskSetResponses(assessmentSessionId, taskSetId, responses);
            };

            var getCurrentTaskSet = function (assessmentSessionId) {
                return AssessmentSessionsAPIService.get(assessmentSessionId);
            };

            var getNextTaskSet = function (assessmentSessionId, currentTaskSetId) {
                return $q.when(nextTaskSet);
            };

            var getPreviousTaskSet = function (assessmentSessionId, currentTaskSetId) {
                return $q.when(nextTaskSet);
            };

            var scoreTaskSet = function (assessmentSessionId, taskSetId, responses) {
                return false;
            };

            var isScoreEnabled = function () {
                return false;
            };


            /*============ SERVICE DECLARATION ============*/

            var PlayerTaskSetService = new TaskSetServiceClass({
                getCurrentTaskSet: getCurrentTaskSet,
                getNextTaskSet: getNextTaskSet,
                getPreviousTaskSet: getPreviousTaskSet,
                updateTaskSetResponses: updateTaskSetResponses,
                pause: pause,
                completeTaskSet: completeTaskSet,
                scoreTaskSet: scoreTaskSet,
                isScoreEnabled: isScoreEnabled
            });


            /*============ PUBLIC METHODS ============*/


            /*============ SERVICE PASS BACK ============*/

            return PlayerTaskSetService;
        }
    ]);

})();
