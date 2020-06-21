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

    angular.module('CCC.Assessment').directive('cccInteraction', [

        '$compile',
        'INTERACTIONS_DISPLAY_CONFIGS_MAP',

        function ($compile, INTERACTIONS_DISPLAY_CONFIGS_MAP) {

            return {

                restrict: 'E',

                scope: {
                    interaction: "=",
                    index: "@",
                    ariaLabel: "@?"
                },

                link: function (scope, element) {

                    /*=========== INITIALIZATION ===========*/

                    var interactionDirective = INTERACTIONS_DISPLAY_CONFIGS_MAP[scope.interaction.type](scope.interaction).directive;
                    var interactionDisplayStyle = INTERACTIONS_DISPLAY_CONFIGS_MAP[scope.interaction.type](scope.interaction).displayStyle;
                    var interactionElem = $('<' + interactionDirective + '></' + interactionDirective + '>');

                    // pass along the interaction
                    interactionElem.attr('interaction', 'interaction');

                    // give it the proper root level display style so that the interaction can affect width as needed
                    if (interactionDisplayStyle === "block") {
                        $(element).css('display', 'block');
                    } else {
                        $(element).css('display', 'inline-block');
                    }

                    element.find('.ccc-interaction-directive').append(interactionElem);
                    $compile(interactionElem)(scope);
                },

                controller: [

                    '$scope',
                    '$element',

                    function ($scope, $element) {


                        /*============= MODEL  ============*/

                        /*============= MODEL DEPENDENT METHODS ============*/

                        /*============= LISTENERS ============*/

                    }
                ],

                template: [
                    '<span ng-class="{\'interaction-invalid\': interaction.validationErrors.length > 0}">',
                        '<span class="ccc-interaction-directive"></span>',
                    '</span>'
                ].join('')

            };

        }
    ]);

})();
