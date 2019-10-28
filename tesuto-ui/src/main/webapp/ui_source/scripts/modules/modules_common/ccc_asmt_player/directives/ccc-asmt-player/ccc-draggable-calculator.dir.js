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

    angular.module('CCC.AsmtPlayer').directive('cccDraggableCalculator', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$rootScope',
                '$element',
                '$compile',
                'CalculatorToolService',
                'ForegroundTrayService',

                function ($rootScope, $element, $compile, CalculatorToolService, ForegroundTrayService) {

                    /*=========== PRIVATE VARIABLES AND METHODS ============*/

                    /*=========== MODEL ===========*/

                    /*=========== BEHAVIOR ===========*/

                    /*=========== LISTENERS ===========*/


                    /*=========== INITIALIZATION ===========*/

                    // since the tray that holds the calculator originally can be closed
                    // we create a scope for the calculator that will not be killed when the tray closes and the calculator is dragged to the screen
                    var calculatorScope = $rootScope.$new();
                    var calculatorElement = $('<ccc-calculator on-remove-clicked="onRemoveClicked" controller-state="controllerState" data-drag="true" jqyoui-draggable="{onStart: \'onDragStart\', onStop:\'onDragStop\'}" data-jqyoui-options="{delay: 200, cancel: \'.display, button\', containment:\'body\', scroll: false}"></ccc-calculator>');

                    calculatorScope.controllerState = CalculatorToolService.state.controllerState;
                    calculatorScope.state = CalculatorToolService.state;

                    calculatorScope.onDragStart = function () {
                        ForegroundTrayService.itemDragStart();
                        CalculatorToolService.state.wasDragged = true;
                    };

                    calculatorScope.onDragStop = function (e) {

                        // set the remove callback to force the showing of a remove button
                        calculatorScope.onRemoveClicked = function () {
                            CalculatorToolService.close();
                        };

                        // the calculator is technically responsive, so take the width it was when it was rendered in the tray
                        calculatorElement.width(calculatorElement.width());

                        var offset = calculatorElement.offset();

                        // here we do a trick and move the calculatorElement out of the tray that will be cleared
                        // we update the offset to be relative to it's new parent
                        calculatorElement.appendTo($('.ccc-asmt-player-components'));
                        calculatorElement.css('left', offset.left);
                        calculatorElement.css('top', offset.top);
                        calculatorElement.css('position', 'absolute');
                        calculatorElement.draggable('option', 'containment', 'body');

                        // and after the drag is complete close the tray
                        ForegroundTrayService.itemDragEnd();
                        ForegroundTrayService.close();

                        $rootScope.$broadcast('ccc-draggable-calculator.dragged', offset);
                    };

                    // listen to calculator state changes to cache calculator state
                    // this allows us to persist calculator state between calculator renderings
                    calculatorScope.$on('ccc-calculator.stateChange', function (e, state) {
                        CalculatorToolService.state.controllerState = state;
                    });

                    calculatorElement.appendTo($element.find('.ccc-draggable-calculator-target'));

                    $compile(calculatorElement)(calculatorScope);
                }
            ],

            template: [
                '<div class="container-fluid">',
                    '<div class="row">',
                        '<div class="col-xs-12 ccc-draggable-calculator-target"></div>',
                    '</div>',
                '</div>'
            ].join('')
        };

    });

})();
