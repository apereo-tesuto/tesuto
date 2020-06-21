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
describe('CalculatorController', function(){
  /* Notes:
    Each entry needs to act like a specific keystroke rather than enter all
    at once; e.g. 1 and 0 rather than 10
  */

    beforeEach(module('CCC.Calculator'));

    var calculatorController = '';

    beforeEach(inject(function($injector) {
      calculatorController = $injector.get('CalculatorController');
      calculatorController = new calculatorController();
    }));

    it('should have a defaults var', function() {
      expect(calculatorController.PRECISION).toEqual(15);
    });

    it('should accept single digit numbers', function() {
      calculatorController.numberPushed(1);
      expect(calculatorController.state.currentStringValue).toEqual('1');
    });

    it('should accept multiple digit numbers', function() {
      calculatorController.numberPushed(1);
      calculatorController.numberPushed(2);
      expect(calculatorController.state.currentStringValue).toEqual('12');
    });

    it('should negate the A value', function() {
      calculatorController.numberPushed(1);
      calculatorController.negate();
      expect(calculatorController.state.currentStringValue).toEqual('-1');
    });

    it('should accept an operator', function() {
      calculatorController.numberPushed(1);
      calculatorController.operationPushed('+')
      expect(calculatorController.state.isOperationSet).toEqual(true);
    });

    it('should accept a b value', function() {
      calculatorController.numberPushed(1);
      calculatorController.operationPushed('+');
      calculatorController.numberPushed(1);
      expect(calculatorController.state.isValueBSet).toEqual(true);
    });

    it('should negate the b value', function() {
      calculatorController.numberPushed(99);
      calculatorController.operationPushed('+');
      calculatorController.numberPushed(1);
      calculatorController.negate();
      calculatorController.evaluate();
      expect(calculatorController.state.currentStringValue).toEqual('98');
    });

    it('should remove a character when backspace is hit', function() {
      calculatorController.numberPushed(2);
      calculatorController.numberPushed(1);
      calculatorController.backspace();
      expect(calculatorController.state.currentStringValue).toEqual('2');
    });

    it('should clear all values', function() {
      calculatorController.numberPushed(2);
      calculatorController.operationPushed('+');
      calculatorController.numberPushed(1);
      calculatorController.clear();
      expect(calculatorController.state.currentStringValue).toEqual('0');
    });

    /**
      Operations
    **/
    /*
      Addition
    */
    it('should evaluate addition expression properly', function() {
      calculatorController.numberPushed(2);
      calculatorController.operationPushed('+');
      calculatorController.numberPushed(1);
      calculatorController.evaluate();
      expect(calculatorController.state.currentStringValue).toEqual('3');
    });

    it('should evaluate addition of two negatives properly', function() {
      calculatorController.numberPushed(2);
      calculatorController.negate();
      calculatorController.operationPushed('+');
      calculatorController.numberPushed(2);
      calculatorController.negate();
      calculatorController.evaluate();
      expect(calculatorController.state.currentStringValue).toEqual('-4');
    });

    it('should evaluate multiple addition expressions properly', function() {
      calculatorController.numberPushed(3);
      calculatorController.operationPushed('+');
      calculatorController.numberPushed(2);
      calculatorController.operationPushed('+');
      calculatorController.numberPushed(1);
      calculatorController.evaluate();
      expect(calculatorController.state.currentStringValue).toEqual('6');
    });

    it('should evaluate addition expression of a negative number properly', function() {
      calculatorController.numberPushed(2);
      calculatorController.negate();
      calculatorController.operationPushed('+');
      calculatorController.numberPushed(1);
      calculatorController.evaluate();
      expect(calculatorController.state.currentStringValue).toEqual('-1');
    });

    /*
      Subtraction
    */
    it('should evaluate subtraction properly', function() {
      calculatorController.numberPushed(2);
      calculatorController.operationPushed('-');
      calculatorController.numberPushed(1);
      calculatorController.evaluate();
      expect(calculatorController.state.currentStringValue).toEqual('1');
    });

    it('should handle subtracting two negatives properly', function() {
      calculatorController.numberPushed(3);
      calculatorController.negate();
      calculatorController.operationPushed('-');
      calculatorController.numberPushed(4);
      calculatorController.negate();
      calculatorController.evaluate();
      expect(calculatorController.state.currentStringValue).toEqual('1');
    });

    it('should evaluate multiple subtraction expressions properly', function() {
      calculatorController.numberPushed(2);
      calculatorController.operationPushed('-');
      calculatorController.numberPushed(1);
      calculatorController.operationPushed('-');
      calculatorController.numberPushed(1);
      calculatorController.evaluate();
      expect(calculatorController.state.currentStringValue).toEqual('0');
    });

    /*
      Multiplication
    */
    it('should evaluate multiplication properly', function() {
      calculatorController.numberPushed(2);
      calculatorController.operationPushed('*');
      calculatorController.numberPushed(1);
      calculatorController.evaluate();
      expect(calculatorController.state.currentStringValue).toEqual('2');
    });

    it('should evaluate multiplication of a negative number properly', function(){
      calculatorController.numberPushed(2);
      calculatorController.negate();
      calculatorController.operationPushed('*');
      calculatorController.numberPushed(1);
      calculatorController.evaluate();
      expect(calculatorController.state.currentStringValue).toEqual('-2');
    });

    it('should evaluate multiplication of two negative numbers properly', function(){
      calculatorController.numberPushed(4);
      calculatorController.negate();
      calculatorController.operationPushed('*');
      calculatorController.numberPushed(4);
      calculatorController.negate();
      calculatorController.evaluate();
      expect(calculatorController.state.currentStringValue).toEqual('16');
    });

    it('should evaluate multiple multiplication of series of numbers properly', function(){
      calculatorController.numberPushed(2);
      calculatorController.operationPushed('*');
      calculatorController.numberPushed(2);
      calculatorController.operationPushed('*');
      calculatorController.numberPushed(2);
      calculatorController.evaluate();
      expect(calculatorController.state.currentStringValue).toEqual('8');
    });

    it('should accept x as a multiplication operator', function() {
      calculatorController.numberPushed(3);
      calculatorController.operationPushed('x');
      calculatorController.numberPushed(3);
      calculatorController.evaluate();
      expect(calculatorController.state.currentStringValue).toEqual('9');
    });

    it('should show large numbers in exponential format', function() {
      calculatorController.numberPushed(9);
      calculatorController.numberPushed(9);
      calculatorController.numberPushed(9);
      calculatorController.numberPushed(9);
      calculatorController.numberPushed(9);
      calculatorController.numberPushed(9);
      calculatorController.numberPushed(9);
      calculatorController.numberPushed(9);
      calculatorController.operationPushed('*');
      calculatorController.numberPushed(8);
      calculatorController.numberPushed(8);
      calculatorController.numberPushed(8);
      calculatorController.numberPushed(8);
      calculatorController.numberPushed(8);
      calculatorController.numberPushed(8);
      calculatorController.numberPushed(8);
      calculatorController.numberPushed(8);
      calculatorController.evaluate();
      expect(calculatorController.state.currentStringValue).toEqual('8.88888871111111e+15');
    });

    /*
      Division
    */
    it('should evaluate division properly', function() {
      calculatorController.numberPushed(1);
      calculatorController.numberPushed(0);
      calculatorController.operationPushed('÷');
      calculatorController.numberPushed(5);
      calculatorController.evaluate();
      expect(calculatorController.state.currentStringValue).toEqual('2');
    });

    it('should evaluate division of a negative number properly', function(){
      calculatorController.numberPushed(4);
      calculatorController.negate();
      calculatorController.operationPushed('÷');
      calculatorController.numberPushed(2);
      calculatorController.evaluate();
      expect(calculatorController.state.currentStringValue).toEqual('-2');
    });

    it('should evaluate multiple division properly', function() {
      calculatorController.numberPushed(1);
      calculatorController.numberPushed(2);
      calculatorController.operationPushed('÷');
      calculatorController.numberPushed(3);
      calculatorController.operationPushed('÷');
      calculatorController.numberPushed(2);
      calculatorController.evaluate();
      expect(calculatorController.state.currentStringValue).toEqual('2');
    });

    it('should handle dividing by a negative properly', function() {
      calculatorController.numberPushed(9);
      calculatorController.operationPushed('÷');
      calculatorController.numberPushed(3);
      calculatorController.negate();
      calculatorController.evaluate();
      expect(calculatorController.state.currentStringValue).toEqual('-3');
    });

    /*
      Operation precedence
    */
    it('should order precedence by input order', function() {
      calculatorController.numberPushed(3);
      calculatorController.operationPushed('*');
      calculatorController.numberPushed(2);
      calculatorController.operationPushed('+');
      calculatorController.numberPushed(1);
      calculatorController.evaluate();
      expect(calculatorController.state.currentStringValue).toEqual('7');
    });

    /**
      Negative tests
    **/
    // There is no way this can be done via the UI at this point
    it('should ignore a bad operator', function() {
      calculatorController.numberPushed(2);
      calculatorController.operationPushed('^');
      calculatorController.numberPushed(2);
      expect(calculatorController.state.currentStringValue).toEqual('2');
    });

    it('should disable backspace after a result', function() {
      calculatorController.numberPushed(2);
      calculatorController.operationPushed('+');
      calculatorController.numberPushed(1);
      calculatorController.evaluate();
      calculatorController.backspace();
      expect(calculatorController.state.currentStringValue).toEqual('3');
    });

    it('should disable negate after a result', function() {
      calculatorController.numberPushed(2);
      calculatorController.operationPushed('-');
      calculatorController.numberPushed(3);
      calculatorController.evaluate();
      calculatorController.negate();
      expect(calculatorController.state.currentStringValue).toEqual('-1');
    });

    it('should not evaluate a series without an operator', function() {
      calculatorController.numberPushed(2);
      calculatorController.numberPushed(2);
      calculatorController.evaluate();
      expect(calculatorController.state.currentStringValue).toEqual('22');
    });

    /**
      Decimals
    **/
    it('should handle decimals properly', function() {
      calculatorController.numberPushed('.');
      calculatorController.numberPushed(2);
      expect(calculatorController.state.currentStringValue).toEqual('0.2');
    });

    it('should handle addition of decimals properly', function() {
      calculatorController.numberPushed('.');
      calculatorController.numberPushed(2);
      calculatorController.operationPushed('+');
      calculatorController.numberPushed('.');
      calculatorController.numberPushed(2);
      calculatorController.evaluate();
      expect(calculatorController.state.currentStringValue).toEqual('0.4');
    });

    it('should handle subtraction of decimals properly', function() {
      calculatorController.numberPushed(1);
      calculatorController.operationPushed('-');
      calculatorController.numberPushed('.');
      calculatorController.numberPushed(2);
      calculatorController.evaluate();
      expect(calculatorController.state.currentStringValue).toEqual('0.8');
    });

    it('should handle multiplication of decimals properly', function() {
      calculatorController.numberPushed('.');
      calculatorController.numberPushed(2);
      calculatorController.operationPushed('*');
      calculatorController.numberPushed('.');
      calculatorController.numberPushed(2);
      calculatorController.evaluate();
      expect(calculatorController.state.currentStringValue).toEqual('0.04');
    });

    it('should evaluate whole numbers to decimals properly', function() {
      calculatorController.numberPushed(1);
      calculatorController.operationPushed('÷');
      calculatorController.numberPushed(2);
      calculatorController.evaluate();
      expect(calculatorController.state.currentStringValue).toEqual('0.5');
    });

    it('should reject two decimal points', function() {
      calculatorController.numberPushed(1);
      calculatorController.numberPushed('.');
      calculatorController.numberPushed(1);
      calculatorController.numberPushed('.');
      calculatorController.numberPushed(1);
      expect(calculatorController.state.currentStringValue).toEqual('1.11');
    });

    it('should handle precision properly', function() {
      calculatorController.numberPushed('.');
      calculatorController.numberPushed('0');
      calculatorController.numberPushed('0');
      calculatorController.numberPushed('0');
      calculatorController.numberPushed('0');
      calculatorController.numberPushed('0');
      calculatorController.numberPushed('0');
      calculatorController.numberPushed('0');
      calculatorController.numberPushed('0');
      calculatorController.numberPushed('0');
      calculatorController.numberPushed('0');
      calculatorController.numberPushed('0');
      calculatorController.numberPushed('0');
      calculatorController.numberPushed('0');
      calculatorController.numberPushed('1');
      calculatorController.numberPushed('2');
      calculatorController.numberPushed('3');
      expect(calculatorController.state.currentStringValue).toEqual('0.00000000000001');
    });
});
