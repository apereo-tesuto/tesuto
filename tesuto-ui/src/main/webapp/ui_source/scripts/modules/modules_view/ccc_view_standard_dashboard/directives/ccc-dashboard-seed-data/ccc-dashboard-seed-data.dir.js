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

    angular.module('CCC.View.Dashboard').directive('cccDashboardSeedData', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                'DashboardAPIService',

                function ($scope, DashboardAPIService) {


                    /*============ PRIVATE VARIABLES AND METHODS =============*/


                    /*============ MODEL =============*/

                    $scope.searching = false;
                    $scope.cccId = '';


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var doSeedData = function () {

                    	DashboardAPIService.seedDataRequest().then(function () {


                        }, function (err) {

                            $scope.$emit('ccc-seed-data.error', err);

                        }).finally(function () {


                        });
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.doSeedData = doSeedData;

                }
                
            ],

            template: [
                '<form novalidate autocomplete="off">',
                    '<button class="ccc-seed-data-button btn btn-default" ng-click="doSeedData()">Seed Data</button>',
                '</form>',
            ].join('')

        };

    });

})();




