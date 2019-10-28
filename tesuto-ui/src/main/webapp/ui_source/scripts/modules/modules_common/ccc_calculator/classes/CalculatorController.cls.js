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
     * Four Function Calculator Class
     * Just the guts to make unit testing simpler
     */

    angular.module('CCC.Calculator').factory('CalculatorController', [

        'ObservableEntity',

        function (ObservableEntity) {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/


            /*============ CLASS DECLARATION ============*/

            var CalculatorController = function (calculatorConfigs) {

                var defaults = {
                    PRECISION: 15, // determines how many significant digits to show
                    initialState: {
                        a: '0',
                        b: false,
                        operation: false
                    }
                };

                var that = this;
                ObservableEntity.call(that);
                $.extend(true, that, defaults, calculatorConfigs || {});


                /*=============== PRIVATE VARIABLES / METHODS ============*/

                var DECIMAL_MARK = ".";

                // set the initial calculator state
                var valueA = that.initialState ? that.initialState.a : '0';
                var valueB = that.initialState ? that.initialState.b : false;
                var operation = that.initialState ? that.initialState.operation : false;

                // method helps us quickly  get some details about the number
                var parseNumber = function (number) {

                    var num = parseFloat(number, 10).toPrecision(that.PRECISION) + '';

                    var details = {
                        isNegative: false,
                        hasDecimal: false,
                        digitCount: 0,
                        mantissa: false,
                        mantissaTrimmed: false,
                        exponentString: '',
                        exponentValue: false
                    };

                    if (num[0] === '-') {
                        details.isNegative = true;
                    }

                    details.mantissa = num.split('e')[0];
                    details.mantissaTrimmed = details.mantissa.replace(/\.?0+$/, '');

                    if (details.mantissaTrimmed.indexOf('.') !== -1) {
                        details.hasDecimal = true;
                    }

                    details.digitCount = details.mantissa.match(/\d+/g).join('').length;

                    var exponentPart = num.split('e')[1];
                    details.exponentString = exponentPart ? 'e' + exponentPart : '';

                    if (exponentPart) {
                        details.exponentValue = parseInt(exponentPart, 10);
                    }

                    return details;
                };

                // here we get a string with certain precision and cleanup and trailing 0's
                var processPrecision = function (result) {
                    try {

                        var numberDetails = parseNumber(result);
                        return numberDetails.mantissaTrimmed + numberDetails.exponentString;

                    } catch (e) {
                        return 'Error';
                    }
                };

                // at any time we can check the a,b values and operation to see what should be displayed
                var updateCurrentString = function () {

                    // this is simple, if B is not set, use A, otherwise B
                    if (!that.state.isValueBSet) {
                        that.state.currentStringValue = valueA;
                    } else {
                        that.state.currentStringValue = valueB;
                    }

                    // look for 'e' and replace it with 'E'
                    that.state.currentStringDisplayValue = that.state.currentStringValue.replace('e', '<span class="ccc-calculator-e">E</span>');
                };

                // at any time we should be able to say what type of state we are in
                var updateCalculatorState = function () {

                    that.state.isValueBSet = valueB !== false;
                    that.state.isOperationSet = operation !== false;

                    updateCurrentString();

                    that.fireEvent('stateChange', [{a: valueA, b: valueB, operation: operation}]);
                };

                var clear = function () {

                    valueA = '0';
                    valueB = false;
                    operation = false;
                    that.state.isResultShowing = false;

                    updateCalculatorState();
                };

                var evaluateExpression = function () {
                    try {

                        // simply use the current operation and return it
                        // this setup helps us avoid using eval which can be dangerous
                        switch (operation) {
                            case "+":
                                return parseFloat(valueA, 10) + parseFloat(valueB, 10);
                            case "-":
                                return parseFloat(valueA, 10) - parseFloat(valueB, 10);
                            case "x":
                            case "*":
                                return parseFloat(valueA, 10) * parseFloat(valueB, 10);
                            case "÷":
                                return parseFloat(valueA, 10) / parseFloat(valueB, 10);
                            default:
                                return parseFloat(valueA, 10);
                        }

                    } catch (e) {
                        return 'Error';
                    }
                };

                var getEvaluatedExpression = function () {

                    // treat this as simply clearing the operation and still returning valueA
                    if (!that.state.isValueBSet) {

                        valueB = '0';
                        operation = '+';

                    // if there is no operation somehow, then simply storeB and do a noop (this should never happen)
                    } else if (!that.state.isOperationSet) {

                        valueA = valueB;
                        valueB = '0';
                        operation = '+';
                    }

                    return processPrecision(evaluateExpression());
                };

                var setCurrentValue = function (newValue) {

                    if (that.state.isOperationSet) {
                        valueB = newValue + '';
                    } else if (!that.state.isValueBSet && !that.state.isOperationSet) {
                        valueA = newValue + '';
                    }

                    that.state.isResultShowing = false;

                    updateCalculatorState();
                };

                var negate = function (numberString) {

                    if (numberString[0] === '-') {
                        return numberString.substr(1);
                    } else {
                        return '-' + numberString;
                    }
                };

                var isCurrentNumberMaxedOut = function () {

                    var currentNumberDetails = parseNumber(that.state.currentStringValue);

                    // if the number leads with zero, it is not considered a significant digit, so don't count it
                    var leadsWithZero = currentNumberDetails.mantissa[0] === '0';
                    var decimalLength = currentNumberDetails.hasDecimal ? 1 : 0;
                    var negativeLength = currentNumberDetails.isNegative ? 1 : 0;
                    var totalDigits = that.state.currentStringValue.length - decimalLength - negativeLength;

                    return !leadsWithZero && totalDigits >= that.PRECISION || leadsWithZero && totalDigits >= that.PRECISION + 1;
                };


                /*=============== PUBLIC PROPERTIES =============*/

                that.state = {
                    currentStringValue: '0',
                    isValueBSet: false,
                    isOperationSet: false,
                    isResultShowing: true
                };


                /*=============== PUBLIC METHODS =============*/

                that.clear = clear;

                that.numberPushed = function (numberString) {

                    // ensure it is a string
                    numberString = numberString + '';

                    // If a result has been shown, make sure we
                    // clear the display before displaying any new numbers
                    if (that.state.isResultShowing && !that.state.isOperationSet) {
                        that.clear();
                    }

                    // Make sure we only add one decimal mark
                    if (numberString === DECIMAL_MARK && that.state.currentStringValue.indexOf(DECIMAL_MARK) > -1) {

                        // if we are on the first number or the second number then deny
                        if (!that.state.isOperationSet || (that.state.isOperationSet && that.state.isValueBSet)) {
                            return;
                        }
                    }

                    // If we are transitioning to the bValue then do so
                    if (that.state.isOperationSet && !that.state.isValueBSet) {

                        // this will force the screen to clear to 0 so valueB can start being built
                        valueB = '0';
                        updateCalculatorState();
                    }

                    if (isCurrentNumberMaxedOut()) {
                        return;
                    }

                    // Make sure that we remove the default 0 shown on the display
                    // when the user press the first number button
                    var newValue = (that.state.currentStringValue === "0" && numberString !== DECIMAL_MARK) ? numberString : that.state.currentStringValue + numberString;

                    setCurrentValue(newValue);
                };

                that.operationPushed = function (operation_in) {

                    // the user can hit another operation instead of equals which should cause an evaluation and the addition of a new operation
                    if (that.state.isOperationSet && that.state.isValueBSet) {
                        that.evaluate(operation_in);
                    }

                    operation = operation_in;

                    updateCalculatorState();
                };

                that.negate = function () {

                    // Disable the negate button when showing a result
                    if (that.state.isResultShowing) {
                        return;
                    }

                    if (that.state.isValueBSet && valueB !== '0') {
                        valueB = negate(valueB);

                    } else if (valueA !== '0') {
                        valueA = negate(valueA);
                    }

                    updateCalculatorState();
                };

                that.backspace = function () {

                    // Disable backspace if the calculator is shown a result
                    if (that.state.isResultShowing) {
                        return;
                    }

                    // Remove the last character, and make the display zero when
                    // last character is removed
                    if (that.state.currentStringValue.length > 1) {

                        var newValue = that.state.currentStringValue.substr(0, that.state.currentStringValue.length - 1);
                        setCurrentValue(newValue);

                    } else {
                        clear();
                    }
                };

                // will always return a string but that same string will also be on state
                that.evaluate = function (incomingOperation) {

                    var stateBeforeEvaluation = {
                        a: valueA,
                        b: valueB,
                        o: operation
                    };

                    var expressionString = getEvaluatedExpression();
                    valueA = expressionString;
                    valueB = false;
                    operation = false;

                    that.state.isResultShowing = true;

                    updateCalculatorState();

                    that.fireEvent('evaluated', [that.state.currentStringValue, incomingOperation || false, stateBeforeEvaluation]);

                    return that.state.currentStringValue;
                };


                /*=============== INITIALIZTION =============*/

                updateCalculatorState();

            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/


            /*============ FACTORY INSTANCE PASSBACK ============*/

            return CalculatorController;
        }
    ]);

})();
