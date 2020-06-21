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

    angular.module('CCC.AssessmentPrint').service('AssessmentItemTemplateService', [

        'InteractionTemplateService',

        function (InteractionTemplateService) {

            /*============ SERVICE DECLARATION ============*/

            var AssessmentItemTemplateService;


            /*============ PRIVATE VARIABLES AND METHODS ============*/

            var INTERACTION_WHITE_LIST = {
                'choiceInteraction': true
            };

            var getInteractionTemplates = function (interaction) {
                return InteractionTemplateService.getInteractionTemplates(interaction);
            };

            var getItemSessionNumberElement = function (itemSessionIndex) {
                return $([
                    '<span class="ccc-print-assessment-item-number">',
                        '<span  class="ccc-print-question-label-background">',
                            '<h2 class="ccc-print-question-label-text">' + itemSessionIndex + '</h2>',
                        '</span>',
                    '</span>'
                ].join(''));
            };

            var getItemSessionNotRenderableTemplate = function () {
                return $([
                    '<div class="ccc-print-assessment-item-invalid">',
                        'This item contains content that is incompatible with printed assessments',
                    '</div>'
                ].join(''));
            };

            var getItemSessionBubbleSheetNotRenderableTemplate = function () {
                return $([
                    '<div class="ccc-print-assessment-item-bubble-sheet-invalid">',
                        'No answer is required.',
                    '</div>'
                ].join(''));
            };

            var doDomPreProcess = function (domElement) {
                // we need a reliable way to know if object's have loaded, they don't have a complete event
                // so we add the objectLoaded flag in place of complete so others can test for that
                domElement.find('object').each(function (objectElem) {
                    $(this).load(function () {
                        this.objectLoaded = true;
                    });
                });
            };


            /*============ PUBLIC METHODS AND PROPERTIES ============*/

            AssessmentItemTemplateService = {

                getTemplatesForItemSession: function (itemSession) {

                    var okayToRender = true;
                    _.each(itemSession.assessmentItem.interactions, function (interaction) {
                        okayToRender = okayToRender && INTERACTION_WHITE_LIST[interaction.type];
                    });

                    var itemSessionBubbleSheetTemplate = $([
                        '<div class="ccc-print-assessment-item-bubble-sheet">',
                            '<div class="ccc-print-assessment-item-bubble-sheet-number"></div>',
                            '<div class="ccc-print-assessment-item-bubble-sheet-list"></div>',
                        '</div>'
                    ].join(''));

                    itemSessionBubbleSheetTemplate.find('.ccc-print-assessment-item-bubble-sheet-number').html(itemSession.itemSessionIndex + '.');

                    // here we need to go through and inject interaction directives and compile them all
                    var itemSessionContainer = $('<div class="ccc-print-assessment-item"></div>');
                    var itemSessionNumberElement = getItemSessionNumberElement(itemSession.itemSessionIndex);
                    var itemBodyElement = $('<div class="ccc-print-assessment-item-body"></div>');
                    itemSessionNumberElement.appendTo(itemSessionContainer);
                    itemBodyElement.appendTo(itemSessionContainer);

                    // first determine if there are any unrenderable interactions and if so insert special markup
                    if (!okayToRender) {

                        itemBodyElement.append(getItemSessionNotRenderableTemplate());
                        itemSessionBubbleSheetTemplate.find('.ccc-print-assessment-item-bubble-sheet-list').append(getItemSessionBubbleSheetNotRenderableTemplate());

                    // otherwise render as normal
                    } else {

                        var itemBody = $('<span></span>').html(itemSession.assessmentItem.itemBody);

                        // a chance to normalize some items
                        doDomPreProcess(itemBody);

                        itemBodyElement.append(itemBody);

                        itemBodyElement.find('interaction').each(function (index, interaction) {

                            if (itemSession.assessmentItem.interactions !== null && itemSession.assessmentItem.interactions !== undefined && itemSession.assessmentItem.interactions.length > 0) {

                                var interactionTemplates = getInteractionTemplates(itemSession.assessmentItem.interactions[index]);

                                itemSessionBubbleSheetTemplate.find('.ccc-print-assessment-item-bubble-sheet-list').append($(interactionTemplates.bubbleSheetTemplate));

                                var replacementInteraction = $('<span><span> </span><span class="ccc-print-interaction"></span><span> </span></span>');
                                replacementInteraction.find('.ccc-print-interaction').append(interactionTemplates.interactionTemplate);

                                $(interaction).replaceWith(replacementInteraction);
                            }
                        });

                        // if this item has it's own stimulus then inject a stimulus div
                        if ($.trim(itemSession.assessmentItem.stimulus)) {
                            var stimulusElement = $('<div class="ccc-assessment-item-stimulus"></div>').html(itemSession.assessmentItem.stimulus);
                            doDomPreProcess(stimulusElement);

                            itemSessionContainer.prepend(stimulusElement);
                        }
                    }

                    return {
                        itemSessionTemplate: itemSessionContainer,
                        itemSessionBubbleSheetTemplate: itemSessionBubbleSheetTemplate
                    };
                }
            };

            /*============ SERVICE PASS BACK ============*/


            return AssessmentItemTemplateService;
        }
    ]);

})();
