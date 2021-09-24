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
     * Controller for Angular Idle
     */

    angular.module('CCC.Components').service('IdleService', [

        '$rootScope',
        '$location',
        '$timeout',
        'ModalService',
        'Keepalive',

        function ($rootScope, $location, $timeout, ModalService, Keepalive) {

            /*============ SERVICE DECLARATION ============*/

            var IdleService;


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            var newTimeOutModal = function () {
                var modalScope = $rootScope.$new();

                ModalService.open({
                    scope: modalScope,
                    template: '<ccc-modal-session-timeout modal="modal"></ccc-modal-session-timeout>'
                });
            };


            /*============ SERVICE DEFINITION ============*/

            IdleService = {

            };


            /*============ LISTENERS ============*/

            $rootScope.$on('IdleStart', function () {
                newTimeOutModal();
            });

            $rootScope.$on('IdleEnd', function () {
                $timeout(function () {
                    Keepalive.ping(); // heartbeat to sessionCheck endpoint
                });
            });

            $rootScope.$on('IdleTimeout', function () {
                $location.path("/logout").search({'sessionTimeout': 'true'});
            });


            /*============ SERVICE PASSBACK ============*/

            return IdleService;

        }
    ]);

})();
