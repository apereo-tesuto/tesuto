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

    angular.module('CCC.View.AssessmentPrototype').service('PrototypeTaskSetService', [

        '$q',
        '$http',
        'FakeData',
        'TaskSetServiceClass',

        function ($q, $http, FakeData, TaskSetServiceClass) {

            /*============ SERVICE DECLARATION =============*/

            var PrototypeTaskSetService;


            /*============ PRIVATE VARIABLES AND METHODS ============*/

            var currentTaskSet = false;
            // here we can grab the taskSet before it gets sent to the assessment player
            // this gives us the opportunity to apply changes dynamically for testing
            var taskSetInterceptor = function (taskSet) {

                _.each(taskSet.tasks, function (task) {
                    _.each(task.itemSessions, function (itemSession) {

                        if (PrototypeTaskSetService.useAllowSkippingOverride) {
                            itemSession.allowSkipping = PrototypeTaskSetService.allowSkippingOverrideValue;
                        }
                        if (PrototypeTaskSetService.useValidateResponsesOverride) {
                            itemSession.validateResponses = PrototypeTaskSetService.validateResponsesOverrideValue;
                        }
                    });
                });

                return taskSet;
            };


            /*============ PUBLIC METHODS ============*/

            var completeTaskSet = function (assessmentSessionId, taskSetId, responses, direction) {
                return FakeData.completeTaskSet(assessmentSessionId, taskSetId, responses || {});
            };

            var updateTaskSetResponses = function (assessmentSessionId, taskSetId, responses) {
                return FakeData.updateTaskSetResponses(assessmentSessionId, taskSetId, responses);
            };

            var getCurrentTaskSet = function (assessmentSessionId) {
                return $q.when(FakeData.getCurrentTaskSet(assessmentSessionId));
            };

            var getNextTaskSet = function (assessmentSessionId, currentTaskSetId) {

                return FakeData.getNextTaskSet(assessmentSessionId, currentTaskSetId).then(function (nextTaskSetFromServer) {

                    // the contract with the assessment player is to return false on getNextTaskSet when we are all done
                    if (!nextTaskSetFromServer) {

                        currentTaskSet = false;

                    } else {

                        currentTaskSet = {
                            taskSet: taskSetInterceptor(nextTaskSetFromServer),

                            // the following could be optional based on whether it is adaptive or linear
                            totalTaskSetIndex: false,
                            totalTaskSetCount: false,

                            totalTaskIndex: false,
                            totalTaskCount: false
                        };
                    }

                    return currentTaskSet;
                });

                // "responses": [{
                //     "itemSessionId": "JDKFD-SDFDD-BFFDE-EWVBD",
                //     "responseIdentifier": "RESPONSE",
                //     "values": ["ChoiceA", "ChoiceB"]
                // }]
            };

            var getPreviousTaskSet = function (assessmentSessionId, currentTaskSetId) {

                return FakeData.getPreviousTaskSet(assessmentSessionId, currentTaskSetId).then(function (taskSetFromServer) {

                    // the contract with the assessment player is to return false on getNextTaskSet when we are all done
                    if (!taskSetFromServer) {

                        currentTaskSet = false;

                    } else {

                        currentTaskSet = {
                            taskSet: taskSetInterceptor(taskSetFromServer),

                            // the following could be optional based on whether it is adaptive or linear
                            totalTaskSetIndex: false,
                            totalTaskSetCount: false,

                            totalTaskIndex: false,
                            totalTaskCount: false
                        };
                    }

                    return currentTaskSet;
                });
            };

            var scoreTaskSet = function (assessmentSessionId, taskSetId, responses) {
                return false;
            };

            var isScoreEnabled = function () {
                return false;
            };

            PrototypeTaskSetService = new TaskSetServiceClass({

                // these properties can be switched at any time
                // currently the will be toggled in the route logic so they are applied for all assessment tasks
                useAllowSkippingOverride: false,
                allowSkippingOverrideValue: false,

                useValidateResponsesOverride: false,
                validateResponsesOverrideValue: false,

                getCurrentTaskSet: getCurrentTaskSet,
                getNextTaskSet: getNextTaskSet,
                getPreviousTaskSet: getPreviousTaskSet,
                updateTaskSetResponses: updateTaskSetResponses,
                completeTaskSet: completeTaskSet,
                scoreTaskSet: scoreTaskSet,
                isScoreEnabled: isScoreEnabled
            });


            /*============ INITIALIZATION ============*/


            /*============ SERVICE PASS BACK ============*/

            return PrototypeTaskSetService;
        }
    ]);

})();
