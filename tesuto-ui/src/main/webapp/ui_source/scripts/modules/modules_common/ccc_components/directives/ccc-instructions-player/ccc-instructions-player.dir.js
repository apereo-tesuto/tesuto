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

    /*========== PRIVATE STATIC VARIABLES / METHODS ============*/

    var cccInstructionsPlayerId = 0;
    var getInstructionsPlayerId = function () {
        return cccInstructionsPlayerId++;
    };

    /*========== DIRECTIVE DEFINITION ============*/

    angular.module('CCC.Components').directive('cccInstructionsPlayer', function () {

        return {

            restrict: 'E',

            scope: {
                instructionsDisclaimer: '=?',
                instructions: '=',
                instructionsTitle: '=',
                instructionsSubTitle: '='
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                '$compile',

                function ($scope, $element, $timeout, $compile) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/


                    /*============ MODEL ============*/

                    $scope.instructionsDisclaimer = $scope.instructionsDisclaimer || null;

                    $scope.instructionsPlayerId = getInstructionsPlayerId();

                    $scope.instructionsTitleShowing = true;

                    $scope.instructions = $scope.instructions || [{
                        'id': 0,
                        'body': '',
                        'confirm': false,
                        'confirmText': '',
                        'checked': false
                    }];

                    $scope.currentIndex = 0;
                    $scope.allConfirmed = true;
                    $scope.endOfInstructions = false;

                    $scope.navigationDirection = 'forward';


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var checkCurrentIndex = function () {
                        if ($scope.currentIndex === $scope.instructions.length - 1) {
                            $scope.endOfInstructions = true;
                        }

                        $scope.checkPlayerState($scope.instructions);
                    };

                    var focusOnCurrentInstruction = function () {

                        var instruction = $scope.instructions[$scope.currentIndex];

                        var currentCardElement = $($element.find('.ccc-instructions-player-instructions .ccc-instructions-player-instruction-card-container')[$scope.currentIndex]);

                        if (instruction.confirm) {
                            currentCardElement.find('.ccc-instructions-player-checkbox').focus();
                        } else {

                            // first try focus on the next button
                            if (currentCardElement.find('button.next').length) {
                                currentCardElement.find('button.next').focus();
                            // if that fails try previous
                            } else if (currentCardElement.find('button.previous').length) {
                                currentCardElement.find('button.previous').focus();
                            } else {
                                currentCardElement.focus();
                            }
                        }
                    };

                    var loadTitleAndSubTitle = function () {

                        var disclaimerElement = $('<span></span>').html($scope.instructionsDisclaimer);
                        $element.find('.ccc-instructions-player-disclaimer-target').append(disclaimerElement);
                        $compile(disclaimerElement)($scope);

                        var titleElement = $('<span></span>').html($scope.instructionsTitle);
                        $element.find('.ccc-instructions-player-title-target').append(titleElement);
                        $compile(titleElement)($scope);

                        var subTitleElement = $('<span></span>').html($scope.instructionsSubTitle);
                        $element.find('.ccc-instructions-player-sub-title').append(subTitleElement);
                        $compile(subTitleElement)($scope);
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.checkPlayerState = function (instructions) {

                        _.each(instructions, function (instruction) {

                            if (instruction.confirm === true) {
                                $scope.allConfirmed = false;

                                if (instruction.checked === true) {
                                    $scope.allConfirmed = true;
                                }
                            }
                        });

                        if ($scope.allConfirmed === true && $scope.endOfInstructions === true && !$scope.instructionsTitleShowing) {
                            $scope.$emit('ccc-instructions-player.ready');
                        } else {
                            $scope.$emit('ccc-instructions-player.notReady');
                        }
                    };

                    $scope.instructionsTitleAcknowledged = function () {

                        $scope.instructionsTitleShowing = false;
                        $scope.checkPlayerState($scope.instructions);

                        $timeout(function () {
                            focusOnCurrentInstruction();
                        });
                    };

                    $scope.next = function (instruction) {
                        if (instruction.confirm === true && instruction.checked === false) {

                            $timeout(function () {
                                var currentCardElement = $($element.find('.ccc-instructions-player-instructions .ccc-instructions-player-instruction-card-container')[$scope.currentIndex]);
                                currentCardElement.find('button.next').focus();
                            });
                            return;
                        }

                        $scope.navigationDirection = 'forward';

                        $timeout(function () {

                            $scope.currentIndex++;
                            checkCurrentIndex();

                            $timeout(function () {

                                focusOnCurrentInstruction();
                            });
                        });
                    };

                    $scope.prev = function () {

                        $scope.navigationDirection = 'back';

                        $timeout(function () {

                            $scope.currentIndex--;
                            checkCurrentIndex();

                            $timeout(function () {
                                var currentCardElement = $($element.find('.ccc-instructions-player-instructions .ccc-instructions-player-instruction-card-container')[$scope.currentIndex]);
                                if (currentCardElement.find('button.previous').length) {
                                    currentCardElement.find('button.previous').focus();
                                } else {
                                    currentCardElement.find('button.next').focus();
                                }
                            });
                        });
                    };


                    /*============ LISTENERS ==============*/


                    /*============ INITIALIZATION ============*/

                    loadTitleAndSubTitle();

                    checkCurrentIndex();
                }
            ],

            template: [


                '<div class="ccc-instructions-player">',

                    '<div class="ccc-instructions-player-instruction-card-container ccc-instructions-player-title" tabindex="-1" ng-show="instructionsTitleShowing">',

                        '<div class="row">',
                            '<div class="col-md-10 col-md-offset-1">',

                                '<div class="ccc-instructions-player-instruction-card">',

                                    '<div id="ccc-instructions-player-id-{{instructionsPlayerId}}-title">',
                                        '<div class="ccc-instructions-player-disclaimer-target alert alert-info" ng-show="instructionsDisclaimer"></div>',
                                        '<h2 class="ccc-instructions-player-title-target"></h2>',
                                        '<p class="ccc-instructions-player-sub-title"></p>',
                                    '</div>',

                                    '<div class="ccc-instructions-player-title-controls">',
                                        '<button ccc-autofocus class="btn btn-lg btn-primary ccc-instructions-player-acknowledge-title" ng-click="instructionsTitleAcknowledged()" aria-describedby="ccc-instructions-player-id-{{instructionsPlayerId}}-title">Continue</button>',
                                    '</div>',

                                '</div>',

                            '</div>',
                        '</div>',

                    '</div>',

                    '<ul class="ccc-instructions-player-instructions" ng-if="!instructionsTitleShowing" ng-class="{\'ccc-instructions-player-direction-back\': navigationDirection == \'back\'}">',
                        '<li class="ccc-instructions-player-instruction-card-container" tabindex="-1" ng-show="$index === currentIndex" ng-repeat="instruction in instructions track by instruction.id">',

                            '<div class="row">',
                                '<div class="col-md-10 col-md-offset-1">',

                                    '<div class="ccc-instructions-player-instruction-card">',

                                        '<span ng-bind-html="instruction.body" id="ccc-instructions-player-id-{{instructionsPlayerId}}-{{instruction.id}}">{{instruction.body}}</span>',
                                        '<div class="important" ng-if="instruction.confirm === true">',
                                            '<label for="step-{{instructionsPlayerId}}-{{instruction.id}}">',
                                                '<input class="ccc-instructions-player-checkbox" id="step-{{instructionsPlayerId}}-{{instruction.id}}" ng-model="instruction.checked" type="checkbox" ng-click="checkPlayerState(instructions)" required="required" aria-describedby="ccc-instructions-player-id-{{instructionsPlayerId}}-{{instruction.id}}"> <span ng-bind-html="instruction.confirmText">{{instruction.confirmText}}</span>',
                                            '</label>',
                                        '</div>',

                                        '<div ng-if="instructions.length > 1" class="ccc-instructions-player-actions">',
                                            '<button ng-if="currentIndex > 0" ng-click="prev()" class="btn btn-default previous" aria-describedby="ccc-instructions-player-id-{{instructionsPlayerId}}-{{instruction.id}}">',
                                                '<span class="fa fa-chevron-left" role="presentation" aria-hidden="true"></span>',
                                                ' <span translate="CCC_COMP.INSTRUCTIONS_PLAYER.PREVIOUS"></span>',
                                            '</button>',
                                            '<button ng-if="currentIndex < instructions.length - 1" ng-click="next(instruction)" class="btn btn-primary next" ng-disabled="instruction.confirm && !instruction.checked" aria-describedby="ccc-instructions-player-id-{{instructionsPlayerId}}-{{instruction.id}}">',
                                                '<span translate="CCC_COMP.INSTRUCTIONS_PLAYER.NEXT"></span> ',
                                                '<span class="fa fa-chevron-right" role="presentation" aria-hidden="true"></span>',
                                            '</button>',
                                        '</div>',

                                    '</div>',

                                '</div>',
                            '</div>',

                        '</li>',
                    '</ul>',

                '</div>'

            ].join('')

        };

    });

})();
