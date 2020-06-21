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

    angular.module('CCC.AsmtPlayer').service('CalculatorToolService', [

        '$rootScope',
        '$timeout',
        'ForegroundTrayService',
        'ToolsPermissionService',

        function ($rootScope, $timeout, ForegroundTrayService, ToolsPermissionService) {

            var CalculatorToolService;

            /*============== PRIVATE VARIABLES ==============*/

            var open = function () {

                // very important to just ignore open requests when closing
                if (CalculatorToolService.state.closing) {
                    return;
                }

                // also leave if we don't have permission
                if (!ToolsPermissionService.getPermissions().allowCalculator) {
                    return;
                }

                CalculatorToolService.state.isOpen = true;

                ForegroundTrayService.open({
                    id: 'assessment-navigation',
                    title: 'Calculator',
                    template: '<ccc-draggable-calculator></ccc-draggable-calculator>',
                    width: 280,
                    onOpenComplete: function (trayElement) {
                        CalculatorToolService.state.currentCalculatorElement = trayElement.find('ccc-calculator');
                        trayElement.find('.display').focus();
                    },
                    onClose: function () {

                        // if it was dragged out then we allow the tray to close but flag the calculator to stay open
                        if (!CalculatorToolService.state.wasDragged) {
                            CalculatorToolService.state.isOpen = false;
                        }
                    }
                });

                $rootScope.$broadcast('CalculatorToolService.calculatorOpened');
            };

            var close = function () {

                // very important to just ignore multiple close requests
                if (CalculatorToolService.state.closing) {
                    return;
                }

                CalculatorToolService.state.closing = true;

                if (CalculatorToolService.state.wasDragged) {

                    // store a reference to current calculator because it is possible the user could relaunch and get a second one
                    var currentCalculatorElement = CalculatorToolService.state.currentCalculatorElement;
                    currentCalculatorElement.addClass('ccc-calculator-leaving');

                    $timeout(function () {

                        currentCalculatorElement.remove();
                        CalculatorToolService.state.closing = false;

                    }, 400);

                } else {

                    ForegroundTrayService.close(function () {
                        CalculatorToolService.state.closing = false;
                    });
                }

                if (CalculatorToolService.state.isOpen) {
                    $rootScope.$broadcast('CalculatorToolService.calculatorClosed');
                }

                CalculatorToolService.state.isOpen = false;
                CalculatorToolService.state.wasDragged = false;
                CalculatorToolService.state.currentCalculatorElement = false;
            };

            var toggle = function () {

                if (CalculatorToolService.state.isOpen) {
                    close();
                } else {
                    open();
                }
            };

            var repositionCalculator = function () {

                // the calculator was dragged to the screen we need to make sure it's on screen
                if (CalculatorToolService.state.currentCalculatorElement && CalculatorToolService.state.wasDragged) {
                    CalculatorToolService.state.currentCalculatorElement.position({
                        of: 'body',
                        my: "center  middle",
                        at: "center middle",
                        collision: "fit flip"
                    });
                }
            };


            /*============== SERVICE DEFINITION ==============*/

            CalculatorToolService = {

                // several of these flags are necessary to prevent ill logic during async closing and opening workflows
                state: {
                    isOpen: false,
                    wasDragged: false,
                    currentCalculatorElement: null,
                    controllerState: false
                },

                toggle: toggle,
                close: close,
                open: open
            };


            /*============= LISTENERS ==============*/

            $rootScope.$on('ccc-assess.resize', repositionCalculator);

            $rootScope.$watch(function () {
                return ToolsPermissionService.getPermissions().allowCalculator;
            }, function (allowCalculator) {
                if (!allowCalculator) {
                    CalculatorToolService.close();
                }
            });


            /*============= SERVICE PASSBACK ==============*/

            return CalculatorToolService;
        }
    ]);

})();
