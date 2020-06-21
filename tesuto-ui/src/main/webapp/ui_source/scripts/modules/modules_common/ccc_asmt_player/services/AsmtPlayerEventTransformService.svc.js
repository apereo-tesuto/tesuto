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

    // This service simply houses an eventTransform method that tacks on standard data to each event that the channel is interested
    // It standardizes the format of the event but still gives flexibility in the args for more fine grained reporting

    angular.module('CCC.AsmtPlayer').service('AsmtPlayerEventTransformService', [

        '$rootScope',
        'AsmtService',
        'Moment',
        'SESSION_CONFIGS',

        function ($rootScope, AsmtService, Moment, SESSION_CONFIGS) {

            var AsmtPlayerEventTransformService;

            /*============== PRIVATE VARIABLES ==============*/

            var userId = SESSION_CONFIGS['user'].userAccountId;

            var eventTransform = function (eventKey, eventObj, eventArgs) {

                var currentTaskId = AsmtService.currentAssessmentTask ? AsmtService.currentAssessmentTask.taskId : false;
                var assessmentSessionId = AsmtService.assessment.assessmentSessionId;

                // for the asmtPlayer it may be important to keep track of the following
                return {
                    eventKey: eventKey,                                 // why not hold onto the eventKey
                    eventObj: eventObj,                                 // the first argument should always be an angular event
                    eventArgs: eventArgs,                               // pass along the rest of the arguments
                    timestamp: new Moment().utc().toISOString(),        // client time in utc
                    userId: userId,                                     // userid
                    assessmentSessionId: assessmentSessionId,           // assessmentSessionId
                    currentTaskId: currentTaskId                        // current task id
                };
            };


            /*============== SERVICE DEFINITION ==============*/

            AsmtPlayerEventTransformService = {
                eventTransform: eventTransform
            };


            /*============= LISTENERS ==============*/

            /*============= SERVICE PASSBACK ==============*/

            return AsmtPlayerEventTransformService;
        }
    ]);

})();
