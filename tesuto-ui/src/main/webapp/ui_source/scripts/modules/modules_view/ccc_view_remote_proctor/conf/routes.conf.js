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

    /*========== LAYOUT DEFINITIONS FOR DIFFERENT TEST ITEMS ============*/

    angular.module('CCC.View.RemoteProctor').config([

        '$stateProvider',
        '$urlRouterProvider',
        'SESSION_CONFIGS',

        function ($stateProvider, $urlRouterProvider, SESSION_CONFIGS) {

            /*============ CATCH ALLS AND FALL BACKS ============*/

            // our fallback is just to go to the proctor agreement view
            $urlRouterProvider.otherwise("/service/v1/remote-proctor/authorize?uuid");


            /*============ MAIN ROUTES ============*/

            $stateProvider.state('home', {
                url: '/service/v1/remote-proctor/authorize?uuid&acknowledge',
                pageTitle: 'Remote Proctor',
                template: '<ccc-route-remote-proctor></ccc-route-remote-proctor>',
                params: { uuid: SESSION_CONFIGS.uuid, acknowledge: '' }
            });
        }
    ]);

})();
