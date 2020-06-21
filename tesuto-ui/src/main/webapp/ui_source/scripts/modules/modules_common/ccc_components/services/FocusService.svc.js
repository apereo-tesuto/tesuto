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
     * Re-usable focus related methods
     */

    angular.module('CCC.Components').service('FocusService', ['$rootScope', '$timeout', function ($rootScope, $timeout) {

        /*============ SERVICE DECLARATION ============*/

        var FocusService;


        /*============ PRIVATE METHODS AND VARIABLES ============*/

        var currentFocusHandler = false;

        var getFocusHandler = function (rootElement, firstElement) {
            return function (event) {

                if (!$.contains($(rootElement)[0], event.target)) {
                    event.stopPropagation();
                    $(firstElement).focus();
                }
            };
        };



        /*============ SERVICE DEFINITION ============*/

        FocusService = {

            setFocusTrap: function (rootElement, defaultFocusableElement) {

                // if there is already a focus trap, remove it
                if (currentFocusHandler) {
                    FocusService.clearFocusTrap();
                }
                currentFocusHandler = getFocusHandler(rootElement, defaultFocusableElement);
                document.addEventListener('focus', currentFocusHandler, true);

            },

            clearFocusTrap: function () {
                if (currentFocusHandler) {
                    document.removeEventListener('focus', currentFocusHandler, true);
                }
            }
        };

        /*============ LISTENERS ============*/

        /*============ SERVICE PASSBACK ============*/

        return FocusService;

    }]);

})();

