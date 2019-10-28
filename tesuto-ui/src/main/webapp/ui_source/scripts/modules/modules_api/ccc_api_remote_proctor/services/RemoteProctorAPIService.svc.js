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

    angular.module('CCC.API.RemoteProctor').service('RemoteProctorAPIService', [

        '$http',
        'ErrorHandlerService',
        'VersionableAPIClass',

        function ($http, ErrorHandlerService, VersionableAPIClass) {

            /*============ SERVICE DECLARATION ============*/

            var RemoteProctorAPIService = new VersionableAPIClass({id: 'remoteProctor', resource: 'remote-proctor'});


            /*============ PRIVATE METHODS AND VARIABLES ============*/


            /*============ SERVICE DEFINITION ============*/

            RemoteProctorAPIService.authorizeRemoteProctor = function (uuid) {

                return $http.get(RemoteProctorAPIService.getBasePath() + '/authorize', {params: {'uuid': uuid, 'acknowledge': true}}).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            RemoteProctorAPIService.get = function (testEventId) {

                return $http.get(RemoteProctorAPIService.getBasePath() + '/' + testEventId, {}).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            RemoteProctorAPIService.getActivations = function (testEventId) {

                return $http.get(RemoteProctorAPIService.getBasePath() + '/' + testEventId + '/activations', {}).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            RemoteProctorAPIService.getRemotePasscodeForTestEvent = function (testEventId) {

                return $http.get(RemoteProctorAPIService.getBasePath() + '/' + testEventId + '/passcode', {}).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };


            /*============ LISTENERS ============*/


            /*============ SERVICE PASSBACK ============*/

            return RemoteProctorAPIService;
        }
    ]);

})();

