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

    angular.module('CCC.View.Home').directive('cccManageLocations', function () {

        return {

            restrict: 'E',

            scope: {
                location: '=?'
            },

            controller: [

                '$scope',
                'DistrictsAPIService',

                function ($scope, DistrictsAPIService) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/

                    /*============ MODEL ==============*/

                    $scope.districts = [];
                    $scope.loading = true;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var sortDistrictData = function (districts) {

                        return _.sortBy(districts, function (district) {

                            // while we are in here, lets save another loop and sort the district colleges
                            district.colleges = _.sortBy(district.colleges, function (college) {

                                // and may as well sort the test locations as well.
                                college.testLocations = _.sortBy(college.testLocations, function (testLocation) {
                                    return testLocation.name.toLowerCase();
                                });

                                return college.name.toLowerCase();
                            });

                            return district.name.toLowerCase();
                        });
                    };

                    var initialize = function () {
                        $scope.districts = [];

                        DistrictsAPIService.get().then(function (districts) {

                            $scope.districts = sortDistrictData(districts);

                        }).finally(function () {
                            $scope.loading = false;
                        });
                    };


                    /*============= BEHAVIOR =============*/

                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-district-list.addLocationToCollege', function (e, college) {
                        $scope.$emit('ccc-manage-locations.addLocationToCollege', college);
                    });

                    $scope.$on('ccc-manage-locations.requestRefresh', initialize);

                    $scope.$on('ccc-district-list.editLocation', function (e, testLocation) {
                        $scope.$emit('ccc-manage-locations.editLocation', testLocation);
                    });

                    $scope.$on('ccc-district-list.userClicked', function (e, user) {
                        $scope.$emit('ccc-manage-locations.userClicked', user);
                    });


                    /*============ INITIALIZATION ==============*/

                    initialize();
                }
            ],

            template: [

                '<ccc-content-loading-placeholder ng-if="districts.length === 0" no-results-info="districts.length === 0 && !loading" hide-default-no-results-text="true">',
                    '<div translate="CCC_VIEW_HOME.CCC-MANAGE-LOCATIONS.NO_DISTRICTS"></div>',
                '</ccc-content-loading-placeholder>',

                '<ccc-district-list districts="districts" ng-if="!loading && districts.length"></ccc-district-list>'

            ].join('')

        };

    });

})();
