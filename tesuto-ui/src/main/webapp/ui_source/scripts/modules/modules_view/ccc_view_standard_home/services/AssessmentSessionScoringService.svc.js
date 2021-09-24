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
     * SERVICE WITH HELPERS TO GENERATE SCORING DATA FOR AN ASSESSMENT SESSION
     */

    angular.module('CCC.View.Home').service('AssessmentSessionScoringService', [

        function () {

            /*============ SERVICE DECLARATION ============*/

            var AssessmentSessionScoringService;


            /*============ PRIVATE METHODS AND VARIABLES ============*/


            /*============ SERVICE DEFINITION ============*/

            AssessmentSessionScoringService = {

                // scan through an array of taskSets and generate a model appropriate for scoring
                getScoringModelFromTaskSets: function (taskSets) {

                    var scoringItems = [];

                    _.each(taskSets, function (taskSet) {
                        _.each(taskSet.tasks, function (task) {

                            var taskId = task.taskId;

                            _.each(task.itemSessions, function (itemSession) {

                                var itemSessionObject = {
                                    taskId: taskId,
                                    itemSessionId: itemSession.itemSessionId,
                                    itemSessionIndex: itemSession.itemSessionIndex,
                                    responses: []
                                };

                                scoringItems.push(itemSessionObject);

                                _.each(itemSession.assessmentItem.interactions, function (interaction) {

                                    itemSessionObject.responses.push({
                                        interaction: interaction,
                                        responseIdentifier: interaction.responseIdentifier,
                                        values: []
                                    });
                                });
                            });
                        });
                    });

                    // [
                    //     {
                    //         taskId: '',
                    //         itemSessionIndex: 1
                    //         itemSessionId: 'cb230a44-7186-459f-a947-5f8cb4e8ef23',
                    //         responses: [
                    //             {
                    //                 interaction: {},
                    //                 responseIdentifier: 'RESPONSE',
                    //                 values: ['choice_2']
                    //             }
                    //         ]
                    //     }
                    // ];

                    return scoringItems;
                },

                generateResponseModelFromScoringModel: function (scoringModel) {

                    var responseObject = {};

                    // here is a sample of what needs to be sent back
                    // {
                    //     "0eaa228a-5dc3-46d6-b30d-b17c52862b83": {
                    //         "duration": 9015,
                    //         "responses": [{
                    //             "itemSessionId": "cb230a44-7186-459f-a947-5f8cb4e8ef23",
                    //             "responseIdentifier": "RESPONSE",
                    //             "values": ["choice_2"]
                    //         }]
                    //     }
                    // }

                    _.each(scoringModel, function (itemSessionObject) {

                        if (!responseObject[itemSessionObject.taskId]) {
                            responseObject[itemSessionObject.taskId] = {
                                duration: 0,
                                responses: []
                            };
                        }

                        _.each(itemSessionObject.responses, function (response) {

                            responseObject[itemSessionObject.taskId].responses.push({
                                itemSessionId: itemSessionObject.itemSessionId,
                                responseIdentifier: response.responseIdentifier,
                                values: response.values.slice(0)
                            });
                        });
                    });

                    return responseObject;
                }
            };


            /*============ LISTENERS ============*/

            /*============ SERVICE PASSBACK ============*/

            return AssessmentSessionScoringService;

        }
    ]);

})();
