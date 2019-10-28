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

    angular.module('CCC.View.Home').directive('cccLocationsList', function () {

        return {

            restrict: 'E',

            scope: {
                listStyle: '@?'
            },

            controller: [

                '$scope',
                '$element',
                'LocationService',

                function ($scope, $element, LocationService) {

                    /*============ MODEL ============*/

                    $scope.locations = [];
                    $scope.selectedLocation = LocationService.getCurrentTestCenter() || null;
                    $scope.loading = true;

                    $scope.listStyle = $scope.listStyle || '';


                    /*============ MODEL DEPENDENT METHODS ============*/


                    /*============ BEHAVIOR ============*/

                    $scope.setCurrentTestCenter = function (location) {
                        // note we don't set the private selectedLocation here, the Location service will fire an event that internally we listen for below
                        LocationService.setCurrentTestCenter(location);
                    };


                    /*============ LISTENERS ============*/

                    // this helps keep all ccc-location-list dropdowns in sync... if one changes they all update
                    $scope.$on('LocationService.currentTestCenterUpdated', function (e, testCenter) {

                        $scope.selectedLocation = testCenter;

                        // now let anyone know above that the selected location of this dropdown has changed
                        $scope.$emit('ccc-locations-list.testCenterUpdated', testCenter);
                    });


                    /*============ INITIALIZATION ============*/

                    LocationService.getTestCenterList().then(function (testCenters) {

                        testCenters = _.chain(testCenters)
                            .sortBy(function (testCenter) {
                                return testCenter.name.toLowerCase();
                            })
                            .sortBy(function (testCenter) {
                                return testCenter.collegeName.toLowerCase();
                            })
                        .value();

                        $scope.locations = testCenters;

                    }).finally(function () {

                        $scope.loading = false;
                        $scope.$emit('ccc-locations-list.testCenterUpdated', LocationService.getCurrentTestCenter());
                    });

                    // propagate the ccc-autofocus attribute down to the button
                    if ($element.attr('ccc-autofocus')) {
                        $element.removeAttr('ccc-autofocus');
                        $element.find('button').attr('ccc-autofocus','ccc-autofocus');
                    }
                }
            ],

            template: [

                '<div ccc-dropdown-focus class="btn-group">',
                    '<button ng-disabled="loading" type="button" class="btn btn-default btn-full-width-when-small dropdown-toggle" ng-class="{\'btn-primary\': listStyle === \'primary\'}" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">',
                        '<i class="fa fa-university" aria-hidden="true"></i>',
                        '<span ng-if="loading">Loading <span class="fa fa-spinner fa-spin" aria-hidden="true"></span></span>',
                        '<span ng-if="!loading && locations.length === 0">No Locations Available</span>',
                        '<span ng-if="!loading && locations.length !== 0">{{selectedLocation.name}} <span class="caret" aria-hidden="true"></span></span>',
                    '</button>',
                    '<ul class="dropdown-menu">',
                        '<li ng-repeat="location in locations track by location.id" class="dont-break-out" ng-class="{selected: location.id === selectedLocation.id}" ng-if="location.enabled">',
                            '<a href="#" ng-click="setCurrentTestCenter(location)">{{location.collegeName}} - {{location.name}}</a>',
                        '</li>',
                    '</ul>',
                '</div>'

            ].join('')
        };
    });

})();
