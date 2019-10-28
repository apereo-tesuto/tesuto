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
     * All API calls associated with batch activations.
     * Use this for an array of activation objects.
     */

    angular.module('CCC.API.BatchActivations').service('BatchActivationsAPIService', [

        '$rootScope',
        '$http',
        'ErrorHandlerService',
        'VersionableAPIClass',

        function ($rootScope, $http, ErrorHandlerService, VersionableAPIClass) {

            /*============ SERVICE DECLARATION ============*/

            var BatchActivationsAPIService = new VersionableAPIClass({id:'activationBatch', resource: 'activation-batch'});


            /*============ PRIVATE METHODS AND VARIABLES ============*/


            /*============ SERVICE DEFINITION ============*/

            /**
             * create activations using batch-activation endpoint
             * @param  {[type]} activations an array of activation objects
             */
            BatchActivationsAPIService.createActivations = function (activations_in) {

                return $http.post(BatchActivationsAPIService.getBasePath(), activations_in).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);

                });
            };


            /*============ LISTENERS ============*/

            /*============ INITIALIZATION ============*/


            /*============ SERVICE PASSBACK ============*/

            return BatchActivationsAPIService;
        }
    ]);

})();
