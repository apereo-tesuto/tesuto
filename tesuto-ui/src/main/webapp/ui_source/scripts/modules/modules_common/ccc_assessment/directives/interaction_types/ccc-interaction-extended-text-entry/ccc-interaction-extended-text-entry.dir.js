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
     * QTI Specs: http://www.imsglobal.org/question/qtiv2p1/imsqti_infov2p1.html#element10334
     */

    angular.module('CCC.Assessment').directive('cccInteractionExtendedTextEntry', function () {

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

                    $scope.itemUpdated = function () {
                        // CONTRACT: let everyone know we have changed
                        $scope.$emit('ccc-interaction.updated');
                    };

                    $scope.textEntryBlur = function () {
                        // CONTRACT: every interaction needs to notify when a user commits a change to the values
                        // There is a future user story where we will implement auto save where extened text interactions will be re-evaluated for events
                        // $scope.$emit('ccc-interaction.valueCommited', {
                        //     type:'extendedText',
                        //     size: (typeof $scope.interaction.values[0] === 'string') ? $scope.interaction.values[0].length : 0
                        // });
                    };

                    /*============ LISTENERS =============*/

                    $scope.$watch('interaction.values[0]', function (newValue, oldValue) {
                        // ignore the initial firing
                        if (newValue !== oldValue) {
                            $scope.itemUpdated();
                        }
                    });




                    /*============ INITIALIZATION =============*/

                }
            ],

            template: [
                '<span ng-form class="ccc-interaction-form" data-uuid="{{interaction.uiid}}">',

                    '<label ng-bind-html="interaction.prompt" class="ccc-interaction-prompt ccc-fg" id="interaction-{{interaction.uiid}}"></label>',

                    '<span ccc-lang id="extendedTextEntryInteractionErrors-{{interaction.uiid}}">',
                        '<ul class="ccc-interaction-error-list ccc-fg" ng-if="interaction.validationErrors.length > 0">',
                            '<li tabindex="0" ng-repeat="error in interaction.validationErrors" class="invalid-error-message"><span translate="{{error.msg}}"></span></li>',
                        '</ul>',
                    '</span>',

                    '<span class="ccc-interaction-text-entry-container">',
                        '<textarea ',
                            'autocomplete="off" class="text form-control ccc-bg ccc-fg" ',
                            'rows="{{interaction.expectedLines}}" ',
                            'aria-labelledby="interaction-{{interaction.uiid}}" ',
                            'aria-live="polite" ',
                            'aria-invalid="{{interaction.validationErrors.length ? true : false}}" ',
                            'ng-class="{\'ng-invalid\':interaction.validationErrors.length}" ',
                            'aria-describedby="extendedTextEntryInteractionErrors-{{interaction.uiid}}" ',
                            'name="group-one" ',
                            'ng-model="interaction.values[0]" ',
                            'ng-blur="textEntryBlur()"',
                        '></textarea>',
                    '</span>',

                '</span>'
            ].join('')

        };

    });

})();
