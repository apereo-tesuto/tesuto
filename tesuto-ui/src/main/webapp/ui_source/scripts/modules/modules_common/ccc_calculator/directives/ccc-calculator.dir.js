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
var FastClick = FastClick || {};

(function () {

    angular.module('CCC.Calculator').directive('cccCalculator', [function () {

        return {
            restrict: 'E',

            scope: {
                onRemoveClicked: '=?',  // optional callback function that will force remove button to be shown
                controllerState: '=?'   // you can start the calculator with an initial A,B operator state
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                'CalculatorController',
                'AriaLiveService',

                function ($scope, $element, $timeout, CalculatorController, AriaLiveService) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    var calculatorController = new CalculatorController({initialState: $scope.controllerState});

                    var attachBehavior = function () {

                        // Key codes and their associated calculator buttons
                        var calculatorKeys = {
                            48: "0", 49: "1", 50: "2", 51: "3", 52: "4", 53: "5", 54: "6",
                            55: "7", 56: "8", 57: "9", 96: "0", 97: "1", 98: "2", 99: "3",
                            100: "4", 101: "5", 102: "6", 103: "7", 104: "8", 105: "9",
                            106: "x", 107: "+", 109: "-", 110: ".", 111: "÷", 8: "backspace",
                            13: "=", 46: "c", 67: "c"
                        };

                        // some key down we need to stop
                        var keyDownCallback = function (e) {

                            // we don't want enter triggering a click
                            // we don't want backspace to make the browser go history back
                            if (e.keyCode === 13 || e.keyCode === 8) {
                                e.preventDefault();
                                e.stopPropagation();
                            }
                        };

                        // Callback for every key stroke
                        var keycallback = function (e) {

                            // Check if the key was one of our calculator keys
                            if (e.keyCode in calculatorKeys) {

                                // move focus to display if they are pressing actual buttons
                                if (e.keyCode !== 13) {
                                    $element.find('.display').focus();
                                }

                                // Get button-element associated with key
                                var ele = document.getElementById("calculator-button-" + calculatorKeys[e.keyCode]);
                                // Simulate button click on keystroke
                                $(ele).addClass("active");
                                setTimeout(function () {
                                    $(ele).removeClass("active");
                                }, 100);
                                // Fire click event
                                $(ele).click();
                            }
                        };

                        $element.on('keyup', keycallback);
                        $element.on('keydown', keyDownCallback);

                        $element.find('*').focus(function () {
                            $element.addClass('ccc-calculator-focused');
                        });

                        $element.find('*').blur(function () {
                            $element.removeClass('ccc-calculator-focused');
                        });

                        // lastly lets use fast click to avoid zooming on mobile.. users tend to click buttons fast on the calculator
                        _.each($element.find('button'), function (buttonElement) {
                            FastClick.attach(buttonElement);
                        });
                    };

                    var flashTimeout;
                    var flashCalculatorScreen = function (newNumber, currentOperation, stateBeforeEvaluation) {

                        $scope.flashCalculatorScreen = true;

                        $timeout.cancel(flashTimeout);

                        flashTimeout = $timeout(function () {
                            $scope.flashCalculatorScreen = false;
                        }, 100);

                        var currentOperationText = '';
                        if (currentOperation) {

                            if (currentOperation === '-') {
                                currentOperation = 'minus';
                            }
                            currentOperationText = ', ' + currentOperation;
                        }

                        AriaLiveService.notify('equals, ' + calculatorController.state.currentStringValue + currentOperationText);

                        $scope.$emit('ccc-calculator.evaluated', {value: calculatorController.state.currentStringValue, stateBeforeEvaluation: stateBeforeEvaluation});
                    };


                    /*============ MODEL ============*/

                    $scope.onRemoveClicked = $scope.onRemoveClicked || false;

                    $scope.state = calculatorController.state;


                    /*============ MODEL DEPENDENT METHODS ============*/


                    /*============ BEHAVIOR ============*/

                    $scope.clear = function () {
                        calculatorController.clear();
                    };

                    $scope.operator = function (button) {
                        if (button === '=') {
                            calculatorController.evaluate();
                        } else {
                            AriaLiveService.notify(button);
                            calculatorController.operationPushed(button);
                        }
                    };

                    $scope.number = function (button) {
                        calculatorController.numberPushed(button);
                    };

                    $scope.negate = function () {
                        calculatorController.negate();
                    };

                    $scope.backspace = function () {
                        calculatorController.backspace();
                    };

                    $scope.displayTextClass = function () {
                        return {
                            'display-text-long': calculatorController.state.currentStringValue.length > 15,
                            'display-flash': $scope.flashCalculatorScreen
                        };
                    };


                    /*============ MODEL ============*/

                    /*=============== LISTENERS ==============*/

                    calculatorController.addListener('evaluated', flashCalculatorScreen);

                    calculatorController.addListener('stateChange', function (state) {
                        $scope.$emit('ccc-calculator.stateChange', state);
                    });


                    /*=============== INITIALIZATION ==============*/

                    attachBehavior();

                    $scope.$emit('ccc-calculator.initialized');
                }
            ],

            template: [
                '<div class="calculator demo" tabindex="0">',

                    '<div class="row">',
                        '<div class="col-xs-12">',
                            '<div class="ccc-calculator-handle">',
                                '<div class="ccc-calculator-grip">&nbsp;</div>',
                                '<div class="ccc-calculator-grip">&nbsp;</div>',
                                '<div class="ccc-calculator-grip">&nbsp;</div>',
                                '<div class="ccc-calculator-grip">&nbsp;</div>',
                            '</div>',
                        '</div>',
                    '</div>',

                    '<span class="ccc-calculator-remove" role="button" ng-click="onRemoveClicked()" ng-if="onRemoveClicked">',
                        '<i class="fa fa-times-circle"></i>',
                        '<span class="sr-only">close calculator</span>',
                    '</span>',

                    '<div class="row">',
                        '<div class="col-xs-12">',
                            '<div class="display" ng-class="displayTextClass()" tabindex="0">',
                                '<div class="display-inner" aria-label="calculator results display" role="banner"><div class="display-text" aria-live="assertive" ng-bind-html="state.currentStringDisplayValue"></div></div>',
                            '</div>',
                        '</div>',
                    '</div>',

                    '<div class="row">',
                        '<div class="col-xs-3">',
                            '<button id="calculator-button-c" class="button button-red" ng-click="clear()" aria-label="clear display">c</button>',
                        '</div>',
                        '<div class="col-xs-3">',
                            '<button id="calculator-button-backspace" class="button button-backspace button-gray-light" ng-click="backspace()" aria-label="backspace">&#x21e4;</button>',
                        '</div>',
                        '<div class="col-xs-3">',
                            '<button id="calculator-button-negate" class="button button-gray-light" ng-click="negate()">&#xb1;</button>',
                        '</div>',
                        '<div class="col-xs-3">',
                            '<button id="calculator-button-÷" class="button button-gray-light" ng-click="operator(\'÷\')" aria-label="divided by">÷</button>',
                        '</div>',
                    '</div>',

                    '<div class="row">',
                        '<div class="col-xs-3">',
                            '<button id="calculator-button-7" class="button button-gray" ng-click="number(7)" >7</button>',
                        '</div>',
                        '<div class="col-xs-3">',
                            '<button id="calculator-button-8" class="button button-gray" ng-click="number(8)" >8</button>',
                        '</div>',
                        '<div class="col-xs-3">',
                            '<button id="calculator-button-9" class="button button-gray" ng-click="number(9)" >9</button>',
                        '</div>',
                        '<div class="col-xs-3">',
                            '<button id="calculator-button-x" class="button button-gray-light" ng-click="operator(\'x\')" aria-label="times">x</button>',
                        '</div>',
                    '</div>',

                    '<div class="row">',
                        '<div class="col-xs-3">',
                            '<button id="calculator-button-4" class="button button-gray" ng-click="number(4)" >4</button>',
                        '</div>',
                        '<div class="col-xs-3">',
                            '<button id="calculator-button-5" class="button button-gray" ng-click="number(5)" >5</button>',
                        '</div>',
                        '<div class="col-xs-3">',
                            '<button id="calculator-button-6" class="button button-gray" ng-click="number(6)" >6</button>',
                        '</div>',
                        '<div class="col-xs-3">',
                            '<button id="calculator-button--" class="button button-gray-light" ng-click="operator(\'-\')" aria-label="minus">-</button>',
                        '</div>',
                    '</div>',

                    '<div class="row">',
                        '<div class="col-xs-3">',
                            '<button id="calculator-button-1" class="button button-gray" ng-click="number(1)" >1</button>',
                        '</div>',
                        '<div class="col-xs-3">',
                            '<button id="calculator-button-2" class="button button-gray" ng-click="number(2)" >2</button>',
                        '</div>',
                        '<div class="col-xs-3">',
                            '<button id="calculator-button-3" class="button button-gray" ng-click="number(3)" >3</button>',
                        '</div>',
                        '<div class="col-xs-3">',
                            '<button id="calculator-button-+" class="button button-gray-light" ng-click="operator(\'+\')" aria-label="plus">+</button>',
                        '</div>',
                    '</div>',

                    '<div class="row">',
                        '<div class="col-xs-6">',
                            '<button id="calculator-button-0" class="button button-gray" ng-click="number(0)" >0</button>',
                        '</div>',
                        '<div class="col-xs-3">',
                            '<button id="calculator-button-." class="button button-gray" ng-click="number(\'.\')" aria-label="decimal point">.</button>',
                        '</div>',
                        '<div class="col-xs-3">',
                            '<button id="calculator-button-=" class="button button-blue" ng-click="operator(\'=\')" aria-label="equals">=</button>',
                        '</div>',
                    '</div>',

                '</div>'
            ].join('')
        };
    }]);

})();
