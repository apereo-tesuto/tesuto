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

    angular.module('CCC.AsmtPlayer').service('AssessmentTaskLayoutService', [

        '$rootScope',
        'AsmtTaskUtilsService',
        'LAYOUTS',

        function ($rootScope, AsmtTaskUtilsService, LAYOUTS) {

            var getHasHorizontalChoice = function (interactionList) {
                for (var i=0; i < interactionList.length; i++) {
                    if (interactionList[i].type === 'choiceInteraction' && interactionList[i].orientation === 'horizontal') {
                        return true;
                    }
                }
                return false;
            };

            var getHasNoStimulus = function (assessmentTask) {
                return !assessmentTask.stimulus || !$.trim(assessmentTask.stimulus);
            };

            return {

                getLayoutForAssessmentTask: function (assessmentTask) {

                    var layout = LAYOUTS['double_column_stimulus_left'];

                    var interactionList = AsmtTaskUtilsService.getInteractionsList(assessmentTask);

                    var hasHorizontalChoice = getHasHorizontalChoice(interactionList);
                    var hasNoStimulus = getHasNoStimulus(assessmentTask);

                    if (hasHorizontalChoice) {
                        layout = LAYOUTS['single_column'];
                    }

                    if (hasNoStimulus) {
                        layout = LAYOUTS['single_column_no_stimulus'];
                    }

                    return layout;
                }

            };
        }
    ]);

})();
