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

    angular.module('CCC.View.Home').directive('cccCollegeTestLocations', function () {

        return {

            restrict: 'E',

            scope: {
                testLocations: '='
            },

            controller: [

                '$scope',
                'UsersAPIService',

                function ($scope, UsersAPIService) {

                    /*============ MODEL ============*/

                    $scope.loading = false;
                    $scope.testLocations = $scope.testLocations || [];
                    $scope.openTestLocationMap = {};

                    $scope.enabledTestLocations = [];
                    $scope.disabledTestLocations = [];

                    $scope.openTestLocationMap = {};


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var filterTestLocations = function () {

                        $scope.enabledTestLocations = _.filter($scope.testLocations, function (testLocation) {
                            $scope.openTestLocationMap[testLocation.id] = false;
                            return testLocation.enabled === true;
                        });

                        $scope.disabledTestLocations = _.filter($scope.testLocations, function (testLocation) {
                            $scope.openTestLocationMap[testLocation.id] = false;
                            return testLocation.enabled === false;
                        });
                    };

                    var editLocation = function (testLocation) {
                        $scope.$emit('ccc-college-test-locations.editLocation', testLocation);
                    };

                    var addUser = function () {
                        $scope.$emit('ccc-college-test-locations.addUser');
                    };


                    /*============ BEHAVIOR ============*/

                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-test-location-summary.edit', function (e, testLocation) {
                        editLocation(testLocation);
                    });

                    $scope.$on('ccc-test-location-summary.addUser', addUser);

                    $scope.$on('ccc-college-test-locations.update', filterTestLocations);

                    $scope.$on('ccc-test-location-summary.enabled', function () {
                        filterTestLocations();
                        $scope.$emit('ccc-college-test-locations.enabled');
                    });

                    $scope.$on('ccc-test-location-summary.userClicked', function (e, user) {
                        $scope.$emit('ccc-college-test-locations.userClicked', user);
                    });


                    /*============ INITIALIZATION ============*/

                    filterTestLocations();
                }
            ],

            template: [

                '<ccc-content-loading-placeholder ng-hide="testLocations.length > 0" no-results-info="testLocations.length === 0 && !loading" hide-default-no-results-text="true">',
                    '<div><span translate="CCC_VIEW_HOME.CCC-DISTRICT-LIST.TEST_LOCATIONS.NO_RESULTS.MSG"></span> ',
                    '<button class="btn btn-sm btn-default" ng-click="addLocationToCollege(college)" translate="CCC_VIEW_HOME.CCC-DISTRICT-LIST.TEST_LOCATIONS.NO_RESULTS.BUTTON" aria-describedby="college-{{::college.cccId}}"></button></div>',
                '</ccc-content-loading-placeholder>',

                '<ul class="list-unstyled test-location-list expand-collapse-list level-two">',
                    '<li ng-repeat="testLocation in enabledTestLocations track by testLocation.id" ng-class="{open: openTestLocationMap[testLocation.id]}">',
                        '<ccc-test-location-summary test-location="testLocation" is-expanded="openTestLocationMap[testLocation.id]"></ccc-test-location-summary>',
                    '</li>',
                '</ul>',

                '<h7 ng-if="disabledTestLocations.length">Disabled Locations</h7>',

                '<ul class="list-unstyled test-location-list expand-collapse-list level-two">',
                    '<li ng-repeat="testLocation in disabledTestLocations track by testLocation.id" ng-class="{open: openTestLocationMap[testLocation.id]}">',
                        '<ccc-test-location-summary test-location="testLocation" is-expanded="openTestLocationMap[testLocation.id]"></ccc-test-location-summary>',
                    '</li>',
                '</ul>'

            ].join('')
        };
    });

})();
