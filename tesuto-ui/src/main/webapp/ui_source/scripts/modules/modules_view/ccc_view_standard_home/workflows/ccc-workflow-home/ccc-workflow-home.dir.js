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

    angular.module('CCC.View.Home').directive('cccWorkflowHome', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                '$state',
                'ViewManagerEntity',
                'LocationService',

                function ($scope, $state, ViewManagerEntity, LocationService) {

                    /*============ PRIVATE VARIABLES =============*/


                    /*============ MODEL ==============*/

                    // use a view manager to handler the workflow between screens
                    $scope.viewManager = new ViewManagerEntity({});
                    $scope.location = LocationService.getCurrentTestCenter() || null;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var addHomeView = function () {
                        var viewScope = $scope.$new();

                        $scope.viewManager.pushView({
                            id: 'home-view',
                            title: 'CCC_VIEW_HOME.WORKFLOW.HOME.DASHBOARD',
                            breadcrumb: 'CCC_VIEW_HOME.WORKFLOW.HOME.DASHBOARD',
                            scope: viewScope,
                            template: '<ccc-home></ccc-home>'
                        });
                    };


                    /*============ BEHAVIOR ==============*/

                    /*============ LISTENERS ==============*/

                    $scope.$on('ccc-proctor-location-summary.setProctorLocation', function () {
                        $state.go('proctorLocation');
                    });

                    $scope.$on('LocationService.currentTestCenterUpdated', function (event, location) {
                        $scope.location = location;
                    });

                    $scope.$on('ccc-activate-student.created', function () {
                        $scope.$broadcast('ccc-proctor-location-summary.requestRefresh');
                    });


                    /*============ INITIALIZATION ==============*/

                    addHomeView();

                }
            ],

            template: [
                '<ccc-view-manager view-manager="viewManager"></ccc-view-manager>'
            ].join('')

        };

    });

})();
