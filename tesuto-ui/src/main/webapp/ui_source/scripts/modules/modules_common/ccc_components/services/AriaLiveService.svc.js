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
     * Service to force aria live messages
     */

    angular.module('CCC.Components').service('AriaLiveService', ['$rootScope', '$timeout', function ($rootScope, $timeout) {

        /*============ SERVICE DECLARATION ============*/

        var AriaLiveService;


        /*============ PRIVATE METHODS AND VARIABLES ============*/

        var ariaLiveContainer = $('<div class="ccc-aria-live-service-container" aria-live="assertive"></div>');
        ariaLiveContainer.addClass('sr-only');

        var timeoutRemove;
        var timeoutAdd;


        /*============ SERVICE DEFINITION ============*/

        AriaLiveService = {

            notify: function (message) {

                $timeout.cancel(timeoutRemove);
                $timeout.cancel(timeoutAdd);

                timeoutRemove = $timeout(function () {

                    ariaLiveContainer.html('');

                    timeoutAdd = $timeout(function () {
                        ariaLiveContainer.html('<span>' + message + '</span>');
                    }, 50);

                }, 10);
            }
        };

        /*============ LISTENERS ============*/


        /*============ INITIALIZATION ============*/

        ariaLiveContainer.appendTo($('body'));


        /*============ SERVICE PASSBACK ============*/

        return AriaLiveService;

    }]);

})();

