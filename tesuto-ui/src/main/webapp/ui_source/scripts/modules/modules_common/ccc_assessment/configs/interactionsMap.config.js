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

    /*========== LAYOUT DEFINITIONS FOR DIFFERENT TEST ITEMS ============*/

    angular.module('CCC.Assessment').value('INTERACTIONS_DISPLAY_CONFIGS_MAP', {

        'choiceInteraction': function (interaction) {
            return {
                directive: 'ccc-interaction-choice',
                displayStyle: 'block'
            };
        },

        'inlineChoiceInteraction': function (interaction) {
            return {
                directive: 'ccc-interaction-inline-choice',
                displayStyle: 'inline-block'
            };
        },

        'textEntryInteraction': function (interaction) {
            return {
                directive: 'ccc-interaction-text-entry',
                displayStyle: 'inline-block'
            };
        },

        'extendedTextInteraction': function (interaction) {
            return {
                directive: 'ccc-interaction-extended-text-entry',
                displayStyle: 'block'
            };
        },

        'matchInteraction': function (interaction) {
            return {
                directive: 'ccc-interaction-match',
                displayStyle: 'block'
            };
        }

    });

})();
