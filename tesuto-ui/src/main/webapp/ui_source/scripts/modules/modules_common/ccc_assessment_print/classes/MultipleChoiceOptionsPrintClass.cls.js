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

    angular.module('CCC.AssessmentPrint').factory('MultipleChoiceOptionsPrintClass', [

        function () {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/

            /*============ CLASS DECLARATION ============*/

            var MultipleChoiceOptionsPrintClass = function (choices, orientation) {

                var that = this;

                /*=============== COERCE SOME INPUT VALUES =============*/

                orientation = orientation || 'vertical';


                /*=============== PRIVATE VARIABLES / METHODS ============*/

                var getRadioTextByIndex = function (index) {
                    return String.fromCharCode(97 + index).toUpperCase();
                };

                var getRadioElement = function (index) {
                    return $('<span class="ccc-multiple-choice-bubble ccc-fg"><span class="ccc-multiple-choice-bubble-inner">' + getRadioTextByIndex(index) + '</span></span>');
                };


                /*=============== PUBLIC VARIABLES / METHODS ============*/

                that.questionElement = $([
                    '<div class="ccc-multiple-choice-options-question">',
                        '<ul class="clearfix"></ul>',
                    '</div>'
                ].join('')).addClass('ccc-multiple-choice-options-question-orientation-' + orientation);

                that.answerElement = $([
                    '<div class="ccc-multiple-choice-options-answer">',
                        '<ul></ul>',
                    '</div>'
                ].join(''));


                /*============ INITIALIZATION ============*/

                _.each(choices, function (choice, choiceIndex) {

                    var questionChoiceElem = $('<li></li>');
                    that.questionElement.find('ul').append(questionChoiceElem);

                    var questionRadioElement = getRadioElement(choiceIndex);
                    questionRadioElement.append('<span class="ccc-multiple-choice-bubble-decoration">.</span>');
                    questionChoiceElem.append(questionRadioElement);

                    var questionChoiceContent = $('<span class="ccc-multiple-choice-option-value"></span>').html(choice.content);
                    questionChoiceElem.append(questionChoiceContent);

                    var answerChoiceElem = $('<li></li>');
                    that.answerElement.find('ul').append(answerChoiceElem);
                    answerChoiceElem.append(getRadioElement(choiceIndex));
                });
            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/


            /*============ FACTORY INSTANCE PASSBACK ============*/

            return MultipleChoiceOptionsPrintClass;
        }
    ]);

})();
