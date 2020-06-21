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
     * This service wraps the RouteFreezeService which allows you to block UI - router navigation
     * This service also wraps the WindowFocusService which allows you to try and block user from closing or navigating away
     */
    angular.module('CCC.Components').service('NavigationFreezeService', [

        '$rootScope',
        'RouteFreezeService',
        'WindowFocusService',

        function ($rootScope, RouteFreezeService, WindowFocusService) {

            /*============ SERVICE DECLARATION ============*/

            var NavigationFreezeService;


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            var stopBlocking = function (resumeToLastBlockedState) {
                RouteFreezeService.stopBlocking(resumeToLastBlockedState);
                WindowFocusService.stopBlockingCloseWindow();
            };

            var startBlocking = function (onBlockHandler_in, message) {
                RouteFreezeService.startBlocking(onBlockHandler_in);
                WindowFocusService.startBlockingCloseWindow(message);
            };


            /*============ SERVICE DEFINITION ============*/

            NavigationFreezeService = {


                /**
                 * [startBlockingCloseWindow description]
                 * @type {function} onBlockHandler - a call back to be run when this service blocks the UI
                 * @type {string} message - a string to show for window blocking
                 */
                startBlocking: startBlocking,

                /**
                 * [stopBlocking description]
                 * @type {boolean} resumeToLastBlockedState - you can stop blocking and then resume to the last blocked state, essentially resume
                 */
                stopBlocking: stopBlocking
            };


            /*============ LISTENERS ============*/


            /*============ INITIALIZATION ============*/


            /*============ SERVICE PASSBACK ============*/

            return NavigationFreezeService;
        }
    ]);

})();

