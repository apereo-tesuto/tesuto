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

    angular.module('CCC.Assessment').service('InteractionFactoryService', [

        '$rootScope',
        'InteractionChoiceFactory',
        'InteractionInlineChoiceFactory',
        'InteractionTextEntryFactory',
        'InteractionExtendedTextEntryFactory',
        'InteractionMatchFactory',

        function (
            $rootScope,
            InteractionChoiceFactory,
            InteractionInlineChoiceFactory,
            InteractionTextEntryFactory,
            InteractionExtendedTextEntryFactory,
            InteractionMatchFactory
        ) {

            /*============ SERVICE DECLARATION ============*/

            var InteractionFactoryService;


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            // each new interaction type must be mapped to a corresponding interaction type factory
            var interactionInstanceMap = {
                'choiceInteraction': InteractionChoiceFactory,
                'inlineChoiceInteraction': InteractionInlineChoiceFactory,
                'textEntryInteraction': InteractionTextEntryFactory,
                'extendedTextInteraction': InteractionExtendedTextEntryFactory,
                'matchInteraction': InteractionMatchFactory
            };

            // there may be some interaction settings that may need to be set based on information
            // from the itemSession that contains them
            // It seems very possible that in the future we may be passed information at the assessment level or taskSet level too
            var getInteractionSettings = function (itemSession) {
                return {
                    allowSkipping: (!!itemSession.allowSkipping), // force boolean
                    validateResponses: itemSession.validateResponses
                };
            };


            /*============ SERVICE DEFINITION ============*/

            InteractionFactoryService = {

                coerceInteraction: function (interactionData, interactionSettings) {

                    var finalInteractionData = $.extend(true, {}, interactionData, interactionSettings);

                    if (!interactionInstanceMap[finalInteractionData.type]) {
                        throw new Error('InteractionFactoryService:InteractionTypeNotFound "' + finalInteractionData.type + '"');
                    }

                    return new interactionInstanceMap[finalInteractionData.type](finalInteractionData);
                },

                getInteractionSettings: function (itemSession) {
                    return getInteractionSettings(itemSession);
                }
            };

            /*============ LISTENERS ============*/

            /*============ SERVICE PASSBACK ============*/

            return InteractionFactoryService;

        }
    ]);

})();

