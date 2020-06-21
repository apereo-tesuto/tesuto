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
     * All API calls associated with activations
     */

    angular.module('CCC.API.ActivationSearch').service('ActivationSearchAPIService', [

        '$rootScope',
        '$http',
        'ErrorHandlerService',
        'VersionableAPIClass',
        'ActivationClass',
        'CCCUtils',
        'CoerceActivationService',

        function ($rootScope, $http, ErrorHandlerService, VersionableAPIClass, ActivationClass, CCCUtils, CoerceActivationService) {

            /*============ SERVICE DECLARATION ============*/

            var ActivationSearchAPIService = new VersionableAPIClass({id: 'activationSearch', resource: 'activation-search'});


            /*============ PRIVATE METHODS AND VARIABLES ============*/


            /*============ SERVICE DEFINITION ============*/

            ActivationSearchAPIService.getActivationsForLocation = function (searchConfigs) {

                return $http.post(ActivationSearchAPIService.getBasePath() + '/locationsearch', searchConfigs).then(function (response) {

                    return response.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            ActivationSearchAPIService.getAllActivationsForStudent = function (cccId) {

                return $http.post(ActivationSearchAPIService.getBasePath() + '/quicksearch', {"userIds": cccId, "includeCanceled": true}).then(function (response) {

                    return CoerceActivationService.attachTestLocationInfoToActivations(response.data).then(function(activations){

                        return CCCUtils.coerce(ActivationClass, activations);
                    });

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            ActivationSearchAPIService.getActiveActivationsForStudent = function (cccId) {

                return $http.post(ActivationSearchAPIService.getBasePath() + '/quicksearch', {"userIds": cccId}).then(function (response) {

                    return CoerceActivationService.attachTestLocationInfoToActivations(response.data).then(function(activations) {

                        return CCCUtils.coerce(ActivationClass, activations);
                    });

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            /**
             * @param  {[array]} userIds can be 1 or many
             * @return {[string]} searchId
             */
            ActivationSearchAPIService.createActivationSearch = function (userIds) {

                var requestData = {"userIds": userIds};

                return $http.post(ActivationSearchAPIService.getBasePath(), requestData).then(function (response) {

                    var responseHeader = response.headers()['location'];
                    var parsedHeader = responseHeader.split('/');
                    var searchId = parsedHeader[parsedHeader.length - 1];

                    return searchId;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            /**
             * @param  {[string]} searchId
             * @return {[object]} returned activations
             */
            ActivationSearchAPIService.getActivationSearch = function (searchId) {

                return $http.get(ActivationSearchAPIService.getBasePath() + '/' + searchId, {searchId: searchId}).then(function (response) {

                    return response.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });

            };

            /**
             * [getUserLocationSummary description]
             * @return {[type]} [description]
             */
            ActivationSearchAPIService.getUserLocationSummary = function (searchParams) {

                return $http.post(ActivationSearchAPIService.getBasePath() + '/location-summary', searchParams || {}).then(function (response) {

                    return response.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };


            /*============ LISTENERS ============*/


            /*============ SERVICE PASSBACK ============*/

            return ActivationSearchAPIService;

        }
    ]);

})();

