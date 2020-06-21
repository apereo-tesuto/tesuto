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
     * Single Choice: http://www.imsglobal.org/question/qtiv2p1/examples/items/choice.xml
     * Multiple Choice: http://www.imsglobal.org/question/qtiv2p1/examples/items/choice_multiple.xml
     * QTI Specs: http://www.imsglobal.org/question/qtiv2p1/imsqti_infov2p1.html#element10278
     */

    angular.module('CCC.Assessment').directive('cccInteractionChoice', ['$sce', function ($sce) {

        return {

            restrict: 'E',

            scope: {
                interaction: "="
            },

            link: function (scope, element) {

                /*============ INITIALIZATION =============*/

                if (!scope.interaction.choices || !scope.interaction.choices.length) {
                    throw new Error('cccInteractionChoice:noChoices');
                }

                // we need to mark the html as safe here so things like MathML will render
                for (var i=0; i < scope.interaction.choices.length; i++) {
                    scope.interaction.choices[i].trustedContent = $sce.trustAsHtml(scope.interaction.choices[i].content);
                }
            },

            controller: [

                '$scope',
                '$sce',

                function ($scope, $sce) {

                    /*============ PRIVATE VARIABLES AND METHODS =============*/

                    var isMultipleChoice = $scope.interaction.maxChoices !== 1;


                    /*============ MODEL =============*/

                    $scope.type = isMultipleChoice ? 'multiple' : 'single';


                    /*============ MODEL DEPENDENT METHODS =============*/

                    /*============ BEHAVIOR =============*/

                    $scope.controlKeyUp = function (event) {

                        var UP = 38;
                        var DOWN = 40;

                        // when the user hits the up and down arrows
                        if ($scope.type === 'radio') {
                            if (event.keyCode === UP) {
                                UP = UP;
                            }
                            if (event.keyCode === DOWN) {
                                DOWN = DOWN;
                            }
                        }
                    };


                    /*============ LISTENERS =============*/

                }
            ],

            // http://www.w3.org/WAI/GL/wiki/Using_grouping_roles_to_identify_related_form_controls
            template: [
                '<div ng-form class="ccc-interaction-form" data-uiid="{{interaction.uiid}}">',

                    '<label ng-bind-html="interaction.prompt" class="ccc-interaction-prompt ccc-fg" id="interaction-{{interaction.uiid}}"></label>',

                    '<span ccc-lang>',
                        '<ul class="ccc-interaction-error-list ccc-fg" ng-if="interaction.validationErrors.length > 0">',
                            '<li tabindex="0" ng-repeat="error in interaction.validationErrors" class="invalid-error-message">{{error.msg}}</li>',
                        '</ul>',
                    '</span>',

                    '<ccc-interaction-choice-multiple interaction="interaction" ng-if="type === \'multiple\'"></ccc-interaction-choice-multiple>',
                    '<ccc-interaction-choice-single interaction="interaction" ng-if="type === \'single\'"></ccc-interaction-choice-single>',

                '</div>'
            ].join('')

        };

    }]);

})();
