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
     * Location management for test centers and colleges.
     */

    angular.module('CCC.View.Home').service('LocationService', [

        '$rootScope',
        '$q',
        'CurrentUserService',
        'localStorageService',
        'TestLocationsAPIService',

        function ($rootScope, $q, CurrentUserService, localStorageService, TestLocationsAPIService) {


            /*============ SERVICE DECLARATION ============*/

            var LocationService;


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            var getTestingLocationsPromise;

            var testCenterList = [];

            var currentTestCenter = localStorageService.get('LocationService-currentTestCenter') || null;

            var currentCollegeId = localStorageService.get('LocationService-currentCollegeId') || null;

            var processTestCenterList = function (testCenters) {

                // Alphabetically sort by test center name (ascending)
                testCenters.sort(function (a, b) {
                    if (a.name < b.name) {
                        return -1;
                    }
                    if (a.name > b.name) {
                        return 1;
                    }
                    return 0;
                });

                // Add each test center to our list
                _.each(testCenters, function (testCenter) {

                    testCenterList.push({
                        id: testCenter.id,
                        name: testCenter.name,
                        college: {
                            cccId: testCenter.college.cccId,
                            name: testCenter.college.name
                        }
                    });
                });

                // Check to make sure this proctor has access to a previously set location
                var thisProctorsTestCenterList = _.map(testCenterList, function (testCenter) {
                    return testCenter.id;
                });

                if (currentTestCenter && _.contains(thisProctorsTestCenterList, currentTestCenter.id) === false) {
                    currentTestCenter = null;
                }

                // If not previously set, the first test center becomes selected
                if (!currentTestCenter) {
                    currentTestCenter = testCenterList[0];
                    localStorageService.set('LocationService-currentTestCenter', currentTestCenter);
                    $rootScope.$broadcast('LocationService.currentTestCenterUpdated', currentTestCenter);
                }
            };

            var populateTestCenterList = function () {
                testCenterList = [];

                if (CurrentUserService.hasPermission('VIEW_TEST_LOCATIONS_BY_USER')) {

                    getTestingLocationsPromise = TestLocationsAPIService.getUserTestLocations().then(function (userTestLocations) {

                        processTestCenterList(userTestLocations);
                        return userTestLocations;
                    });

                } else {
                    getTestingLocationsPromise = $q.when([]);
                }
            };


            /*============ SERVICE DEFINITION ============*/

            LocationService = {

                getTestCenterList: function () {
                    return getTestingLocationsPromise;
                },

                getTestCenterById: function (testCenterId) {
                    return getTestingLocationsPromise.then(function (testingLocations) {

                        return _.find(testingLocations, function (testingLocation) {
                            return testingLocation.id === testCenterId;
                        });
                    });
                },

                getTestCenterMap: function () {
                    return LocationService.getTestCenterList().then(function (locationList) {
                        return _.reduce(locationList, function (memo, location) {
                            memo[location.id] = true;
                            return memo;
                        }, {});
                    });
                },

                getCurrentTestCenter: function () {
                    return currentTestCenter;
                },

                getCurrentTestCenterCollegeId: function () {
                    return currentTestCenter ? (currentTestCenter.college ? currentTestCenter.college.cccId : null) : null;
                },

                getCurrentCollegeId: function () {

                    if (!currentCollegeId) {
                        return LocationService.getCurrentTestCenterCollegeId() ? LocationService.getCurrentTestCenterCollegeId() : false;
                    } else {
                        return currentCollegeId;
                    }
                },

                setCurrentCollegeId: function (collegeId) {
                    currentCollegeId = collegeId;
                    localStorageService.set('LocationService-currentCollegeId', currentCollegeId);
                },

                setCurrentCollegeIdFromCurrentTestCenter: function () {
                    if (LocationService.getCurrentTestCenterCollegeId()) {
                        LocationService.setCurrentCollegeId(LocationService.getCurrentTestCenterCollegeId());
                    }
                },

                setCurrentTestCenter: function (testCenter) {
                    currentTestCenter.id = testCenter.id ? testCenter.id : null;
                    currentTestCenter.name = testCenter.name ? testCenter.name : '';
                    currentTestCenter.college.cccId = testCenter.college.cccId ? testCenter.college.cccId : null;
                    currentTestCenter.college.name = testCenter.college.name ? testCenter.college.name : '';
                    localStorageService.set('LocationService-currentTestCenter', currentTestCenter);
                    $rootScope.$broadcast('LocationService.currentTestCenterUpdated', currentTestCenter);
                },

                breakTestLocationsCache: function () {
                    getTestingLocationsPromise = false;
                    populateTestCenterList();
                }
            };


            /*============ LISTENERS ============*/

            /*============ INITIALIZATION ============*/

            populateTestCenterList();


            /*============ SERVICE PASSBACK ============*/

            return LocationService;

        }
    ]);

})();
