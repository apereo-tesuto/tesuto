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
     * QTI Specs: http://www.imsglobal.org/question/qtiv2p1/imsqti_infov2p1.html#element10333
     */

    angular.module('CCC.Assessment').directive('cccInteractionTextEntry', function () {

        return {

            restrict: 'E',

            scope: {
                interaction: "="
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',

                function ($scope, $element, $timeout) {

                    /*============ PRIVATE VARIABLES AND METHODS =============*/

                    var popoverTrigger = $($element.find('.popover-trigger-element'));


                    /*============ MODEL =============*/

                    $scope.showValidation = false;

                    $scope.ariaLabel = '';
                    $scope.ariaLabelledby = '';

                    if ($scope.interaction.ariaLabelledBy) {
                        $scope.ariaLabelledby = $scope.interaction.ariaLabelledBy;
                    }

                    if ($scope.interaction.ariaLabel) {
                        $scope.ariaLabel = $scope.interaction.ariaLabel;
                    }

                    $scope.inputLabel = $scope.ariaLabel;
                    if ($scope.interaction.placeholderText) {
                        $scope.inputLabel = $scope.inputLabel + " , text placeholder, " + $scope.interaction.placeholderText;
                    }


                    /*============ MODEL DEPENDENT METHODS =============*/


                    /*============ BEHAVIOR =============*/

                    // CONTRACT: every interaction should end up with a set of values
                    $scope.itemUpdated = function () {

                        $scope.interaction.values = $scope.interaction.values || [];

                        // CONTRACT: let everyone know we have changed
                        $scope.$emit('ccc-interaction.updated');
                    };

                    $scope.textEntryBlur = function () {
                        // CONTRACT: every interaction needs to notify when a user commits a change to the values
                        $scope.$emit('ccc-interaction.valueCommited', {
                            type:'textEntry',
                            resp: $scope.interaction.values[0]
                        });
                    };


                    /*============ LISTENERS =============*/

                    $scope.$watch('interaction.values[0]', function (newValue, oldValue) {
                        // ignore initial watch firing
                        if (newValue !== oldValue) {
                            $scope.itemUpdated();
                        }
                    });

                    $scope.$watch('interaction.validationErrors.length', function (errorCount) {

                        if (errorCount > 0) {
                            $timeout(function () {
                                popoverTrigger.trigger('show');
                            }, 1);
                        } else {
                            $timeout(function () {
                                popoverTrigger.trigger('hide');
                            }, 1);
                        }
                    });


                    /*============ INITIALIZATION =============*/

                    // we add a class to the parent of this interaction so that we can adjust line height for better spacing
                    // because the actual input will have a bigger height causing inconsistencies in line height
                    $element.closest('ccc-interaction').parent().addClass('ccc-contains-text-entry-interaction');

                }
            ],

            template: [
                '<span ng-form class="ccc-interaction-form" data-uuid="{{interaction.uiid}}">',

                    '<label ng-if="interaction.prompt" ng-bind-html="interaction.prompt" class="ccc-interaction-prompt ccc-fg" for="interaction-{{interaction.uiid}}"></label>',

                    '<span class="ccc-interaction-text-entry-container" aria-labelledby="interaction-{{interaction.uiid}}">',

                        '<input autocomplete="off" ',
                                'id="interaction-{{interaction.uiid}}" ',
                                'class="text form-control ccc-bg ccc-fg" ',
                                'size="{{interaction.expectedLength ? interaction.expectedLength : \'\'}}" ',
                                'name="group-one" ',
                                'ng-attr-aria-label="{{inputLabel}}" ',
                                'ng-attr-aria-labelledby="{{ariaLabelledby}}" ',
                                'placeholder="{{interaction.placeholderText}}" ',
                                'aria-describedby="text-entry-interaction-errors-{{::interaction.uiid}}" ',
                                'ng-blur="textEntryBlur()" ',
                                'aria-invalid="{{interaction.validationErrors.length ? true : false}}" ',
                                'ng-class="{\'ng-invalid\': interaction.validationErrors.length}" ',
                                'ng-model="interaction.values[0]"/>',

                        '<span ccc-lang class="sr-only" id="text-entry-interaction-errors-{{::interaction.uiid}}">',
                            '<ul class="ccc-interaction-error-list" ng-if="interaction.validationErrors.length > 0">',
                                '<li tabindex="0" ng-repeat="error in interaction.validationErrors" class="invalid-error-message" translate="{{error.msg}}"></li>',
                            '</ul>',
                        '</span>',

                        '<span class="ccc-interaction-validation-container" ng-hide="!interaction.validationErrors.length > 0">',

                            '<button class="ccc-button-wrapper" tabindex="0" uib-popover-trigger="click show" uib-popover-template="\'ccc-interaction-popover-errors-template.html\'">',
                                '<i class="fa fa-exclamation-triangle popover-trigger-element">',
                                    '<span class="sr-only" ccc-lang>show and hide error messages</span>',
                                '</i>',
                            '</button>',

                        '</span>',

                    '</span>',

                '</span>'
            ].join('')

        };

    });

})();
