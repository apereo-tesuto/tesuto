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

    angular.module('CCC.Activations').service('CoerceActivationService', [

        '$rootScope',
        '$q',
        'TestLocationsAPIService',

        function ($rootScope, $q, TestLocationsAPIService) {


            /*============ SERVICE DECLARATION ============*/

            var CoerceActivationService;

            /*============ PRIVATE METHODS AND VARIABLES ============*/


            /*============ SERVICE DEFINITION ============*/

            CoerceActivationService = {

                attachTestLocationInfoToActivations: function(activations) {
                    var locationIds = [];

                    _.each(activations, function (activation) {
                        locationIds.push(activation.locationId);
                    });

                    return TestLocationsAPIService.getTestLocations(locationIds).then(function (testLocations) {

                        var locationIdToTestLocationMap = _.reduce(testLocations, function (memo, testLocation) {
                            memo[testLocation.id] = testLocation;
                            return memo;
                        }, {});

                        _.each(activations, function (activation) {

                            if (locationIdToTestLocationMap[activation.locationId]) {

                                activation.locationLabel = locationIdToTestLocationMap[activation.locationId].name;
                                activation.collegeName = locationIdToTestLocationMap[activation.locationId].collegeName;

                            // this scenario will only happen if the server chokes on trying to get locations
                            // the user should see a server warning from the testLocationAPIService if that happens
                            } else {

                                activation.locationLabel = 'unavailable';
                                activation.collegeName = 'unavailable';
                            }
                        });

                        return activations;
                    });
                }
            };
            /*============ LISTENERS ============*/

            /*============ SERVICE PASSBACK ============*/

            return CoerceActivationService;
        }
    ]);

})();
