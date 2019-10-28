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

    angular.module('CCC.View.Home').directive('cccWorkflowManageLocations', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                '$state',
                'NotificationService',
                'ViewManagerEntity',
                'LocationService',
                'TestLocationClass',

                function ($scope, $state, NotificationService, ViewManagerEntity, LocationService, TestLocationClass) {

                    /*============ PRIVATE VARIABLES =============*/


                    /*============ MODEL ==============*/

                    // use a view manager to handler the workflow between screens
                    $scope.viewManager = new ViewManagerEntity({});
                    $scope.location = LocationService.getCurrentTestCenter() || null;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var addTestLocationEditView = function (testLocation) {
                        var viewScope = $scope.$new();

                        viewScope.testLocation = new TestLocationClass(testLocation);

                        viewScope.$on('ccc-test-location-edit.cancel', function () {
                            $scope.viewManager.popView();
                        });

                        viewScope.$on('ccc-test-location-edit.complete', function () {
                            $scope.viewManager.popView();

                            // Force a reset on JSP passesd college locations
                            LocationService.breakTestLocationsCache();
                            $scope.$broadcast('ccc-manage-locations.requestRefresh');

                            NotificationService.open({
                                icon: 'fa fa-thumbs-up',
                                title: ' Test location updated.',
                                message: ''
                            },
                            {
                                delay: 5000,
                                type: "success",
                                allow_dismiss: true
                            });
                        });

                        $scope.viewManager.pushView({
                            id: 'test-location-edit-view',
                            title: 'Edit Test Location',
                            breadcrumb: 'CCC_VIEW_HOME.WORKFLOW.MANAGE_LOCATIONS.MANAGE_LOCATIONS',
                            scope: viewScope,
                            template: '<ccc-test-location-edit test-location="testLocation"></ccc-test-location-edit>'
                        });
                    };

                    var addUserToLocationView = function () {
                        var viewScope = $scope.$new();

                        viewScope.$on('ccc-workflow-manage-users.userUpdated', function () {
                            $scope.$broadcast('ccc-manage-locations.requestRefresh');
                        });

                        $scope.viewManager.pushView({
                            id: 'manage-locations-add-user-view',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-MANAGE-LOCATIONS.PAGE_TITLES.ADD_USER',
                            breadcrumb: 'CCC_VIEW_HOME.CCC-WORKFLOW-MANAGE-LOCATIONS.PAGE_TITLES.MANAGE_LOCATIONS',
                            isNested: true,
                            scope: viewScope,
                            template: '<ccc-workflow-manage-users></ccc-workflow-manage-users>'
                        });
                    };

                    var addTestLocationCreateView = function (college) {
                        var viewScope = $scope.$new();

                        viewScope.college = college;

                        viewScope.$on('ccc-test-location-create.cancel', function () {
                            $scope.viewManager.popView();
                        });

                        viewScope.$on('ccc-test-location-create.testLocationCreated', function () {

                            $scope.viewManager.goToView('manage-locations-view', true);

                            // Force a reset on JSP passesd college locations
                            LocationService.breakTestLocationsCache();
                            $scope.$broadcast('ccc-manage-locations.requestRefresh');

                            NotificationService.open({
                                icon: 'fa fa-thumbs-up',
                                title: ' Test location created.',
                                message: ''
                            },
                            {
                                delay: 5000,
                                type: "success",
                                allow_dismiss: true
                            });
                        });

                        $scope.viewManager.pushView({
                            id: 'test-location-create-view',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-MANAGE-LOCATIONS.PAGE_TITLES.ADD_TEST_LOCATION',
                            breadcrumb: 'CCC_VIEW_HOME.CCC-WORKFLOW-MANAGE-LOCATIONS.PAGE_TITLES.MANAGE_LOCATIONS',
                            scope: viewScope,
                            template: '<ccc-test-location-create college="college"></ccc-test-location-create>'
                        });
                    };

                    var addEditUserView = function (user) {
                        var viewScope = $scope.$new();
                        viewScope.user = user.clone();

                        viewScope.$on('ccc-user-edit.updated', function (e, user) {
                            $scope.$broadcast('ccc-college-users.requestRefresh');
                            $scope.$broadcast('ccc-college-test-location-users.requestRefresh');
                            $scope.viewManager.popView();
                        });

                        viewScope.$on('ccc-user-edit.cancel', function () {
                            $scope.viewManager.popView();
                        });

                        viewScope.$on('ccc-user-edit.userDeleted', function () {
                            $scope.$broadcast('ccc-college-users.requestRefresh');
                            $scope.$broadcast('ccc-college-test-location-users.requestRefresh');
                            $scope.viewManager.popView();

                            NotificationService.open({
                                icon: 'fa fa-thumbs-up',
                                title: ' Success.',
                                message: ' User was deleted.'
                            },
                            {
                                delay: 5000,
                                type: "success",
                                allow_dismiss: true
                            });
                        });

                        $scope.viewManager.pushView({
                            id: 'manage-locations-edit-user-view',
                            title: 'Edit User',
                            breadcrumb: 'Edit User',
                            scope: viewScope,
                            template: '<ccc-user-edit user="user"></ccc-user-edit>'
                        });
                    };

                    var addManageLocationsView = function () {
                        var viewScope = $scope.$new();

                        viewScope.$on('ccc-manage-locations.addLocationToCollege', function (e, college) {
                            addTestLocationCreateView(college);
                        });

                        viewScope.$on('ccc-manage-locations.editLocation', function (e, testLocation) {
                            addTestLocationEditView(testLocation);
                        });

                        viewScope.$on('ccc-manage-locations.userClicked', function (e, user) {
                            addEditUserView(user);
                        });

                        viewScope.$on('ccc-district-list.addUser', function () {
                            addUserToLocationView();
                        });

                        viewScope.$on('ccc-college-test-locations.enabled', function () {
                            NotificationService.open({
                                icon: 'fa fa-thumbs-up',
                                title: ' Test location updated.',
                                message: ''
                            },
                            {
                                delay: 5000,
                                type: "success",
                                allow_dismiss: true
                            });
                        });

                        $scope.viewManager.pushView({
                            id: 'manage-locations-view',
                            title: 'CCC_VIEW_HOME.CCC-WORKFLOW-MANAGE-LOCATIONS.PAGE_TITLES.MANAGE_LOCATIONS',
                            breadcrumb: 'CCC_VIEW_HOME.CCC-WORKFLOW-MANAGE-LOCATIONS.PAGE_TITLES.MANAGE_LOCATIONS',
                            scope: viewScope,
                            template: '<ccc-manage-locations></ccc-manage-locations>'
                        });
                    };


                    /*============ BEHAVIOR ==============*/

                    /*============ LISTENERS ==============*/

                    $scope.$on('LocationService.currentTestCenterUpdated', function (event, location) {
                        $scope.location = location;
                    });


                    /*============ INITIALIZATION ==============*/

                    addManageLocationsView();

                }
            ],

            template: [
                '<ccc-view-manager view-manager="viewManager"></ccc-view-manager>'
            ].join('')

        };

    });

})();
