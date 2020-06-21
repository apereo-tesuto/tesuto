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
     * QTI Specs: http://www.imsglobal.org/question/qtiv2p1/imsqti_infov2p1.html#element10278
     */

    angular.module('CCC.Assessment').directive('cccInteractionChoiceSingle', [function () {

        return {

            restrict: 'E',

            scope: {
                interaction: "="
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============ PRIVATE VARIABLES AND METHODS =============*/

                    /*============ MODEL =============*/

                    /*============ MODEL DEPENDENT METHODS =============*/


                    /*============ BEHAVIOR =============*/

                    $scope.getRadioTextByIndex = function (index) {
                        return String.fromCharCode(97 + index).toUpperCase();
                    };

                    // CONTRACT: every interaction should end up with a set of values
                    $scope.itemClicked = function (event, choice) {

                        var target = $(event.target);
                        target.closest('.ccc-interaction-choice-item-label').find('.ccc-interaction-choice-list-item').focus();

                        $scope.interaction.values = [];
                        $scope.interaction.values.push(choice.identifier);

                        // CONTRACT: let everyone know we have changed
                        $scope.$emit('ccc-interaction.updated');

                        // CONTRACT: every interaction needs to notify when a user commits a change to the values
                        $scope.$emit('ccc-interaction.valueCommited', {
                            type:'choice',
                            resp: $scope.interaction.values
                        });
                    };

                    $scope.isChoiceSelected = function (choice) {
                        return $scope.interaction.values.indexOf(choice.identifier) !== -1;
                    };


                    /*============ LISTENERS =============*/

                }
            ],

            // http://www.w3.org/WAI/GL/wiki/Using_grouping_roles_to_identify_related_form_controls
            template: [
                '<div class="ccc-interaction-choice-list clearfix" ng-class="{horizontal: interaction.orientation == \'horizontal\'}" aria-labelledby="interaction-{{interaction.uiid}}">',

                    '<div class="ccc-interaction-choice-item-wrapper" ng-repeat="choice in interaction.choices" role="radiogroup">',
                        '<label class="ccc-interaction-choice-item-label choice-item-input-radio">',

                            // putting the content first helps JAWS in windows read better
                            '<span ng-bind-html="choice.trustedContent" class="ccc-interaction-choice-item-content ccc-fg" id="interaction-{{interaction.uiid}}-choice-{{$index}}"></span>',

                            '<span class="ccc-interaction-choice-item-input-wrapper">',
                                '<input class="ccc-interaction-choice-item-input" type="radio" name="radio-group-{{interaction.uiid}}" ',
                                    'aria-checked="{{!!isChoiceSelected(choice)}}" ',
                                    'ng-checked="{{!!isChoiceSelected(choice)}}" ',
                                    'aria-labelledby="interaction-{{interaction.uiid}}-choice-letter-{{$index}} interaction-{{interaction.uiid}}-choice-{{$index}}" ',
                                    'aria-describedby="interaction-{{interaction.uiid}}" ',
                                    'ng-click="itemClicked($event, choice)" ',
                                '/>',
                                '<span class="ccc-interaction-choice-item-label-letter ccc-fg" id="interaction-{{interaction.uiid}}-choice-letter-{{$index}}">{{getRadioTextByIndex($index)}}<span class="sr-only">,</span></span>',
                            '</span>',

                        '</label>',
                    '</div>',

                    '<input class="hidden" autocomplete="off" name="group-one" ng-model="interaction.values" ng-model-options="{allowInvalid: true}" ng-value="choice" ccc-required-array="1" />',

                '</div>'
            ].join('')

        };

    }]);

})();
