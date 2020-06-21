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

    angular.module('CCC.Components').service('RouteFreezeService', [

        '$rootScope',
        '$state',

        function ($rootScope, $state) {

            /*============ SERVICE DECLARATION ============*/

            var RouteFreezeService;


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            var removeListener = false;
            var lastStateCache = false;

            var onBlockHandler = $.noop;

            var onStateChangeStartListener = function (e, toState, toParam) {

                e.preventDefault();

                lastStateCache = {
                    toState: toState,
                    toParam: toParam
                };

                onBlockHandler(lastStateCache);
            };

            var stopBlocking = function (resumeToLastBlockedState) {

                if (removeListener) {
                    removeListener();
                }

                // go to the last blocked state
                if (resumeToLastBlockedState && lastStateCache) {
                    $state.go(lastStateCache.toState, lastStateCache.toParams);
                }

                lastStateCache = false;
            };

            var startBlocking = function (onBlockHandler_in) {

                onBlockHandler = onBlockHandler_in;

                stopBlocking();
                removeListener = $rootScope.$on('$stateChangeStart', onStateChangeStartListener);
            };


            /*============ SERVICE DEFINITION ============*/

            RouteFreezeService = {


                /**
                 * [startBlockingCloseWindow description]
                 * @type {function} onBlockHandler - a call back to be run when this service blocks the UI
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

            return RouteFreezeService;
        }
    ]);

})();

