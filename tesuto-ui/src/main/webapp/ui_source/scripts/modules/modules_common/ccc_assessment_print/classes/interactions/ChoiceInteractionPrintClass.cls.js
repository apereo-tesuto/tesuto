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

    angular.module('CCC.AssessmentPrint').factory('ChoiceInteractionPrintClass', [

        'MultipleChoiceOptionsPrintClass',

        function (MultipleChoiceOptionsPrintClass) {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/

            /*============ CLASS DECLARATION ============*/

            var ChoiceInteractionPrintClass = function (configs_in) {

                var defaults = {};

                // merge in the defaults onto the instance
                var that = this;
                $.extend(true, that, defaults, configs_in || {});


                /*=============== COERCE SOME INPUT VALUES =============*/

                /*=============== PRIVATE VARIABLES / METHODS ============*/

                /*=============== PUBLIC VARIABLES / METHODS ============*/

                that.getInteractionTemplates = function () {

                    var interactionElement = $('<div class="ccc-print-interaction-choice"></div>');
                    var multipleChoiceOptions = new MultipleChoiceOptionsPrintClass(that.choices, that.orientation);

                    if (that.prompt && $.trim(that.prompt)) {
                        var interactionPrompt = $('<div class="ccc-print-interaction-prompt"></div>');
                        interactionPrompt.html(that.prompt);
                        interactionElement.append(interactionPrompt);
                    }

                    interactionElement.append(multipleChoiceOptions.questionElement);

                    return {
                        interactionTemplate: interactionElement,
                        bubbleSheetTemplate: multipleChoiceOptions.answerElement
                    };
                };
            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/


            /*============ FACTORY INSTANCE PASSBACK ============*/

            return ChoiceInteractionPrintClass;
        }
    ]);

})();
