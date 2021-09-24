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
     * All API calls associated with Test Locations
     */

    angular.module('CCC.API.TestLocations').service('TestLocationsAPIService', [

        '$rootScope',
        '$http',
        'ErrorHandlerService',
        'VersionableAPIClass',
        '$q',

        function ($rootScope, $http, ErrorHandlerService, VersionableAPIClass, $q) {


            /*============ SERVICE DECLARATION ============*/

            var TestLocationsAPIService = new VersionableAPIClass({id: 'testLocations', resource: 'test-locations'});


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            var cachedTestLocationsMap = {}; // map testLocation id --> testLocation

            var updateCachedTestLocations = function (testLocations) {

                _.each(testLocations, function (testLocation) {
                    cachedTestLocationsMap[testLocation.id] = testLocation;
                });
            };

            var getLocationsNotCached = function (locationIds) {

                return _.filter(locationIds, function(id) {
                    if(cachedTestLocationsMap[id] === undefined) {
                        return id;
                    }
                });
            };

            var getLocationsFromCacheById = function (locationIds) {

                var filteredTestLocations = {};

                _.each(locationIds, function(id) {
                    if(cachedTestLocationsMap[id] !== undefined) {
                        filteredTestLocations[id] = cachedTestLocationsMap[id];
                    }
                });

                return filteredTestLocations;
            };

            var getTestLocationsFromServer = function (locationIds) {

                return $http.get(TestLocationsAPIService.getBasePath(), {params: {locationIds: locationIds}}).then(function (response) {

                    return response.data;

                }, function (err) {

                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            var ensureLocationsCachedById = function (locationIds) {

                var locationsIdsNotCached = getLocationsNotCached(locationIds);

                if (!_.isEmpty(locationsIdsNotCached)) {

                    return getTestLocationsFromServer(locationsIdsNotCached).then(function (testLocations) {
                        updateCachedTestLocations(testLocations);
                    });
                }
                else {

                    return $q.when([]);
                }
            };

            /*============ SERVICE DEFINITION ============*/

            TestLocationsAPIService.getTestLocations = function (locationIds) {

                var locationsIdsSet = _.uniq(locationIds);

                return ensureLocationsCachedById(locationsIdsSet).then(function () {
                    return getLocationsFromCacheById(locationsIdsSet);
                });
            };

            TestLocationsAPIService.createTestLocation = function (testLocationData) {

                return $http.post(TestLocationsAPIService.getBasePath(), testLocationData).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            TestLocationsAPIService.updateTestLocation = function (testLocationData) {

                return $http.put(TestLocationsAPIService.getBasePath() + '/' + testLocationData.id, testLocationData).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            TestLocationsAPIService.getUserTestLocations = function () {

                return $http.get(TestLocationsAPIService.getBasePath() + '/mine').then(function (results) {

                    // Massage new endpoint data into our existing format
                    _.map(results.data, function (testCenter) {
                        testCenter.college = {
                            cccId: testCenter.collegeId,
                            name: testCenter.collegeName
                        };
                    });

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };

            TestLocationsAPIService.setTestLocationEnabled = function (testLocationId, isEnabled) {

                return $http.put(TestLocationsAPIService.getBasePath() + '/' + testLocationId + '/enabled', {enabled: isEnabled}).then(function (results) {

                    return results.data;

                }, function (err) {
                    return ErrorHandlerService.reportServerError(err, []);
                });
            };


            /*============ LISTENERS ============*/


            /*============ SERVICE PASSBACK ============*/

            return TestLocationsAPIService;

        }
    ]);

})();
