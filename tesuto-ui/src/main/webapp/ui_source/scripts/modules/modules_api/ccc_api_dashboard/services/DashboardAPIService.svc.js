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

    angular.module('CCC.API.Dashboard').service('DashboardAPIService', [

        '$http',
        '$q',
        '$timeout',
        'ErrorHandlerService',
        'VersionableAPIClass',

        function ($http, $q, $timeout, ErrorHandlerService, VersionableAPIClass) {

            /*============ SERVICE DECLARATION ============*/

            var DashboardAPIService = new VersionableAPIClass({id: 'dashboard'});


            /*============ PRIVATE METHODS AND VARIABLES ============*/


            /*============ SERVICE DEFINITION ============*/

            DashboardAPIService.seedDataRequest = function () {

                var url = DashboardAPIService.getBasePath();

                return $http.post(url + "/qa", {}).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, ['404', '401'], ['logout']);
                });

            };


            /*============ LISTENERS ============*/


            /*============ SERVICE PASSBACK ============*/

            return DashboardAPIService;

        }
    ]);

})();

