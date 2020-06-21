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
     * QTI Specs: http://www.imsglobal.org/question/qtiv2p1/imsqti_infov2p1.html#element10321
     * XML Sample: http://www.imsglobal.org/question/qtiv2p1/examples/items/inline_choice.xml
     */

    angular.module('CCC.Assessment').directive('cccInteractionInlineChoice', ['$sce', function ($sce) {

        return {

            restrict: 'E',

            scope: {
                interaction: "="
            },

            link: function (scope, element) {

                /*============ INITIALIZATION =============*/

                if (!scope.interaction.inlineChoices || !scope.interaction.inlineChoices.length) {
                    throw new Error('cccInteractionInlineChoice:noChoices');
                }

                // we need to mark the html as safe here so things like MathML will render
                for (var i=0; i < scope.interaction.inlineChoices.length; i++) {
                    scope.interaction.inlineChoices[i].trustedContent = $sce.trustAsHtml(scope.interaction.inlineChoices[i].content);
                }
            },

            controller: [

                '$scope',
                '$element',
                '$sce',
                '$timeout',
                'MathService',

                function ($scope, $element, $sce, $timeout, MathService) {

                    /*============ PRIVATE VARIABLES AND METHODS =============*/

                    var popoverTrigger = $($element.find('.popover-trigger-element'));

                    var NO_SELECTION_MADE_TEXT = 'Select an Answer';

                    var currentSelectionTarget = $element.find('.ccc-interaction-inline-choice-selection');


                    /*============ MODEL =============*/

                    $scope.showValidation = false;

                    // todo, I am thinking these values need to be standardized and pre-determined in the interaction factory they should come into the directive ready to go
                    $scope.ariaLabel = '';
                    $scope.ariaLabelledby = '';

                    if ($scope.interaction.ariaLabelledby) {
                        $scope.ariaLabelledby = $scope.interaction.ariaLabelledby;
                    }

                    if ($scope.interaction.ariaLabel) {
                        $scope.ariaLabel = $scope.interaction.ariaLabel;
                    }

                    $scope.interactionLabel = $scope.ariaLabel;

                    $scope.currentSelectionText = '';


                    /*============ MODEL DEPENDENT METHODS =============*/

                    var getInlineChoiceContentByIdentifier = function (inlineChoiceIdentifier) {

                        var inlineChoice = _.find($scope.interaction.inlineChoices, function (thisInlineChoice) {
                            return thisInlineChoice.identifier === inlineChoiceIdentifier;
                        });
                        return inlineChoice.trustedContent;
                    };

                    var getCurrentSelectionText = function () {
                        if ($scope.interaction.values.length === 0) {
                            return NO_SELECTION_MADE_TEXT;
                        } else {
                            return getInlineChoiceContentByIdentifier($scope.interaction.values[0]);
                        }
                    };

                    var interactionValueUpdated = function () {
                        $scope.currentSelectionText = getCurrentSelectionText();
                        $(currentSelectionTarget).css('opacity', 0);
                        MathService.processTarget(currentSelectionTarget, function () {
                            $(currentSelectionTarget).css('opacity', 1);
                        });
                    };


                    /*============ BEHAVIOR =============*/

                    // todo, kept this here possible for paper pencil?  this should probably be a utility method as well in some service
                    $scope.getRadioTextByIndex = function (index) {
                        return String.fromCharCode(97 + index).toUpperCase();
                    };

                    // CONTRACT: every interaction should end up with a set of values, this interaction updates it when the user clicks on the value
                    $scope.itemClicked = function (event, inlineChoice) {

                        event.preventDefault();

                        // inline choice is apparently not multiselect, so we will always have an array with only one value
                        $scope.interaction.values = [inlineChoice.identifier];

                        // CONTRACT: let everyone know we have changed
                        $scope.$emit('ccc-interaction.updated');

                        $scope.$emit('ccc-interaction.valueCommited', {
                            type:'inlineChoice',
                            resp: $scope.interaction.values
                        });

                        $timeout(function () {
                            $element.find('.ccc-interaction-inline-choice-button').focus();
                        }, 10);
                    };

                    $scope.isInlineChoiceSelected = function (inlineChoice) {
                        return $scope.interaction.values.indexOf(inlineChoice.identifier) !== -1;
                    };


                    /*============ LISTENERS =============*/

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

                    // watch for when the value of this interaction changes
                    $scope.$watch(function () {
                        return $scope.interaction.values.length ? $scope.interaction.values[0] : false;
                    }, interactionValueUpdated);


                    /*=========== INITIALIZATION ===========*/

                    // we add a class to the parent of this interaction so that we can adjust line height for better spacing
                    // because the actual input will have a bigger height causing inconsistencies in line height
                    $element.closest('ccc-interaction').parent().addClass('ccc-contains-text-entry-interaction');

                }
            ],

            template: [
                '<span ng-form class="ccc-interaction-form" name="{{interaction.id | uuid}}">',

                    '<label ng-if="interaction.prompt" ng-bind-html="interaction.prompt" class="ccc-interaction-prompt ccc-fg" for="interaction-{{::interaction.id}}"></label>',

                    '<span class="ccc-interaction-text-entry-container" aria-labelledby="interaction-{{interaction.id}}">',

                        '<input class="hidden" autocomplete="off" name="group-one" ng-model="interaction.values" ng-value="choice" ccc-required-array="1" />',

                        '<div ccc-dropdown-focus class="dropdown">',
                            '<button ',
                                'id="inline-choice-{{interaction.id}}" ',
                                'type="button" ',
                                'class="btn btn-default dropdown-toggle ccc-interaction-inline-choice-button" ',
                                'ng-disabled="disabled" ',
                                'ng-attr-aria-labelledby="{{ariaLabelledby}}" ',
                                'ng-attr-aria-label="{{ariaLabel}}" ',
                                'aria-describedby="inline-choice-interaction-errors-{{::interaction.uiid}" ',
                                'aria-invalid="{{interaction.validationErrors.length ? true : false}}" ',
                                'ng-class="{\'ng-invalid\':interaction.validationErrors.length}" ',
                                'data-toggle="dropdown" ',
                                'aria-haspopup="true" ',
                                'aria-expanded="false" ',
                            '>',
                                '<span class="sr-only">{{interactionLabel}},</span>',
                                '<span class="ccc-interaction-inline-choice-selection" ng-bind-html="currentSelectionText"></span> ',
                                '<span class="caret" aria-hidden="true"></span>',
                            '</button>',
                            '<ul class="dropdown-menu" aria-labelledby="inline-choice-{{::interaction.id}}">',
                                '<li class="dropdown-menu-item" role="option" ng-repeat="inlineChoice in interaction.inlineChoices track by inlineChoice.identifier" ccc-mathml-click="itemClicked($event, inlineChoice)">',
                                    '<a href="#" role="presentation"><span ng-bind-html="inlineChoice.trustedContent"></span></a>',
                                '</li>',
                            '</ul>',
                        '</div>',

                        '<span ccc-lang class="sr-only" id="inline-choice-interaction-errors-{{::interaction.uiid}}">',
                            '<ul class="ccc-interaction-error-list" ng-if="interaction.validationErrors.length > 0">',
                                '<li tabindex="0" ng-repeat="error in interaction.validationErrors" class="invalid-error-message" translate="{{error.msg}}"></li>',
                            '</ul>',
                        '</span>',

                        '<span class="ccc-interaction-validation-container" ng-hide="!interaction.validationErrors.length > 0">',

                            '<button class="ccc-button-wrapper" tabindex="0" popover-trigger="click show" popover-template="\'ccc-interaction-popover-errors-template.html\'">',
                                '<i class="fa fa-exclamation-triangle popover-trigger-element">',
                                    '<span class="sr-only" ccc-lang>show and hide error messages</span>',
                                '</i>',
                            '</button>',

                        '</span>',

                    '</span>',

                '</span>'
            ].join('')

        };

    }]);

})();
