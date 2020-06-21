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

    angular.module('CCC.View.Home').directive('cccProctorLocationSummary', function () {

        return {

            restrict: 'E',

            scope: {
                location: '=?'
            },

            controller: [

                '$scope',
                '$element',
                'Moment',
                'LocationService',
                'ActivationSearchAPIService',

                function ($scope, $element, Moment, LocationService, ActivationSearchAPIService) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/

                    /*============ MODEL ==============*/

                    $scope.locationsLoadingComplete = false;
                    $scope.testCenterList = [];
                    $scope.collegeList = [];

                    /*============ MODEL DEPENDENT METHODS ============*/

                    var generateCollegeList = function (testCenterList) {

                        // remove possible duplicates
                        $scope.collegeList = _.uniq($scope.collegeList, 'cccId');

                        _.each($scope.collegeList, function (college) {

                            _.each(testCenterList, function (testCenter) {

                                if (testCenter.college.cccId === college.cccId) {
                                    college.testCenters.push(testCenter);
                                }
                            });
                        });

                        // order colleges aplhabetically
                        $scope.collegeList = _.sortBy($scope.collegeList, function (college) {

                            // while we're in here, order testCenters alphabetically as well
                            college.testCenters = _.sortBy(college.testCenters, function (testCenter) {
                                return testCenter.name.toLowerCase();
                            });

                            return college.name;
                        });
                    };

                    var mergeStatsIntoTestCenterList = function (testCenterList, stats) {

                        _.each(testCenterList, function (location) {

                            // create list of colleges,
                            $scope.collegeList.push({
                                cccId: location.college.cccId,
                                name: location.college.name,
                                testCenters: []
                            });

                            var stat = stats[location.id + ""] || {};

                            location.READY = stat.READY ? stat.READY : 0;
                            location.IN_PROGRESS = stat.IN_PROGRESS ? stat.IN_PROGRESS : 0;
                            location.COMPLETE = stat.COMPLETE ? stat.COMPLETE : 0;
                            location.total = location.READY + location.IN_PROGRESS + location.COMPLETE;
                        });

                        generateCollegeList(testCenterList);
                    };

                    var refreshStats = function () {

                        $scope.testCenterList = [];
                        $scope.collegeList = [];

                        ActivationSearchAPIService.getUserLocationSummary({
                            minStatusUpdateDate: new Moment().startOf('day').valueOf()
                        }).then(function (stats) {

                            return LocationService.getTestCenterList().then(function (testCenters) {

                                $scope.testCenterList = testCenters;
                                mergeStatsIntoTestCenterList($scope.testCenterList, stats);

                            });

                        }, function (err) {

                            $scope.testCenterList = [];

                        }).finally(function () {

                            $scope.locationsLoadingComplete = true;

                        });
                    };


                    /*============= BEHAVIOR =============*/

                    $scope.setProctorLocation = function ($event, location) {
                        var locationElement = $($event.currentTarget);
                        LocationService.setCurrentTestCenter(location);
                        $scope.$emit('ccc-proctor-location-summary.setProctorLocation', location, locationElement);
                    };


                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-proctor-location-summary.requestRefresh', refreshStats);


                    /*============ INITIALIZATION ==============*/

                    refreshStats();
                }
            ],

            template: [

                '<div class="row">',
                    '<ccc-info class="col-md-6 col-sm-12" ng-hide="testCenterList.length > 0">',
                        '<strong translate="CCC_VIEW_HOME.INFO.NO_TEST_LOCATION_STRONG"></strong>',
                        '<span translate="CCC_VIEW_HOME.INFO.NO_TEST_LOCATION"></span>',
                    '</ccc-info>',
                '</div>',

                '<div class="row">',
                    '<div class="col-md-12">',
                        '<div ng-repeat="college in collegeList track by $index">',
                            '<h2 ccc-autofocus class="ccc-proctor-location-summary-header section-title" tabindex="-1">{{college.name}}</h2>',
                            '<div class="row">',
                                '<div class="col-md-6" ng-repeat="testCenter in college.testCenters track by testCenter.id" ng-if="testCenter.enabled">',
                                    '<ccc-location-card location="testCenter" ng-click="setProctorLocation($event, testCenter)"></ccc-location-card>',
                                '</div>',
                            '</div>',
                        '</div>',
                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();
