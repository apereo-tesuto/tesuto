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
     * Task Set Service Class
     * This is a class that represents the contract for the taskSetService that the cccAsmtPlayer and cccAsmtPlayerView
     * expects to get as an input. You should create a new instance of this class and provide it to those directives
     */

    angular.module('CCC.AsmtPlayer').factory('TaskSetServiceClass', [

        '$rootScope',

        function ($rootScope) {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/

            // should return a promise for the current task set
            var getCurrentTaskSet = function (assessmentSessionId) {
                throw new Error('TaskSetServiceClass.getCurrentTaskSet.notSet');
            };

            // should return a promise for the next task if there is one, and false otherwise
            var getNextTaskSet = function (assessmentSessionId, currentTaskSetId) {
                throw new Error('TaskSetServiceClass.getNextTask.notSet');
            };

            // should return a promise for the previous task if there is one, and false otherwise
            var getPreviousTaskSet = function (assessmentSessionId, currentTaskSetId) {
                throw new Error('TaskSetServiceClass.getPreviousTask.notSet');
            };

            // should return a promise that resolves success or error
            var pause = function (assessmentSessionId) {
                throw new Error('TaskSetServiceClass.pause.notSet');
            };

            // should return a promise that resolves success or error
            var updateTaskSetResponses = function (assessmentSessionId, taskSetId, responses) {
                throw new Error('TaskSetServiceClass.updateTaskSetResponses.notSet');
            };

            // should return a promise that resolves success or error
            var completeTaskSet = function (taskSetDataToSubmit) {
                throw new Error('TaskSetServiceClass.completeTaskSet.notSet');
            };

            // should return a promise that resolves success or error
            var scoreTaskSet = function (assessmentSessionId, taskSetId, responses) {
                throw new Error('TaskSetServiceClass.scoreTaskSet.notSet');
            };

            // should return a promise that resolves success or error
            var isScoreEnabled = function (taskSetDataToSubmit) {
                throw new Error('TaskSetServiceClass.isScoreEnabled.notSet');
            };


            /*============ CLASS DEFAULT CONFIGS ============*/

            // it is expected that all of these methods are overridden or else we will throw an error
            var defaults = {
                isScoreEnabled: isScoreEnabled,
                getCurrentTaskSet: getCurrentTaskSet,
                getNextTaskSet: getNextTaskSet,
                getPreviousTaskSet: getPreviousTaskSet,
                pause: pause,
                updateTaskSetResponses: updateTaskSetResponses,
                completeTaskSet: completeTaskSet,
                scoreTaskSet: scoreTaskSet
            };


            /*============ CLASS DECLARATION ============*/

            var TaskSetServiceClass = function (taskServiceConfigs) {

                var that = this;
                $.extend(true, that, defaults, taskServiceConfigs);


                /*=============== PUBLIC METHODS =============*/

                /*=============== MORE PUBLIC PROPERTIES =============*/

                /*=============== INITIALIZTION =============*/

            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/


            /*============ FACTORY INSTANCE PASSBACK ============*/

            return TaskSetServiceClass;
        }
    ]);

})();


