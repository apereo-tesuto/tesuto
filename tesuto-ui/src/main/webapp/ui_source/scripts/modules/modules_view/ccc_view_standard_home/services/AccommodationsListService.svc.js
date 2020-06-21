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
     * All API calls associated with Accommodations
     */

    angular.module('CCC.View.Home').service('AccommodationsListService', [

        '$q',
        'AccommodationsAPIService',

        function ($q, AccommodationsAPIService) {

            /*============ SERVICE DECLARATION ============*/

            var AccommodationsListService;


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            var accommodationsListPromise = false;

            var getAccommodationsListPromise = function () {
                return AccommodationsAPIService.getAccommodations({});
            };

            // Wrap original promise to copy the original resolve data
            var getCopiedAccommodationsListPromise = function () {
                var deferred = $q.defer();

                accommodationsListPromise.then(function (accommodations) {
                    deferred.resolve(accommodations);
                    return JSON.parse(JSON.stringify(accommodations));
                });

                return deferred.promise;
            };


            /*============ SERVICE DEFINITION ============*/

            AccommodationsListService = {

                getAccommodationsList: function () {
                    if (accommodationsListPromise) {
                        return getCopiedAccommodationsListPromise();
                    } else {
                        accommodationsListPromise = getAccommodationsListPromise();
                        return getCopiedAccommodationsListPromise();
                    }
                }

            };


            /*============ LISTENERS ============*/

            /*============ SERVICE PASSBACK ============*/

            return AccommodationsListService;

        }
    ]);

})();
