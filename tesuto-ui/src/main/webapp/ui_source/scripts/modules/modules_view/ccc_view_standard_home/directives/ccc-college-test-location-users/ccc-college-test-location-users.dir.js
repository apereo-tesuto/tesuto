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

    angular.module('CCC.View.Home').directive('cccCollegeTestLocationUsers', function () {

        return {

            restrict: 'E',

            scope: {
                testLocation: '=',
                isDisabled: '='
            },

            controller: [

                '$scope',
                'UsersAPIService',
                'RolesService',

                function ($scope, UsersAPIService, RolesService) {


                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    var filterBlackListUsers = true; // this flag may operate off of a permission one day

                    // ensure that we don't show develepors and students
                    // so ultimately if we clean up roles and there are no roles left that user should not be shown
                    var preProcessUsers = function (users) {

                        if (filterBlackListUsers) {

                            RolesService.markUsersWithBlackListedRoles(users);

                            return _.filter(users, function (user) {
                                return !user._hasBlackListedRoles && user.enabled === true && user.roles.length;
                            });

                        } else {

                            return _.filter(users, function (user) {
                                return user.enabled === true && user.roles.length;
                            });
                        }
                    };


                    /*============ MODEL ============*/

                    $scope.loading = false;
                    $scope.testLocation = $scope.testLocation || {};
                    $scope.UserList = [];


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var refreshUserList = function () {
                        $scope.loading = true;
                        $scope.users = [];

                        UsersAPIService.getUsersByTestLocation($scope.testLocation.id).then(function (users) {

                            users = preProcessUsers(users);

                            $scope.users = _.sortBy(users, function (user) {
                                return user.lastName;
                            });

                        }).finally(function () {
                            $scope.loading = false;
                        });
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.addUser = function () {
                        $scope.$emit('ccc-college-test-location-users.addUser');
                    };

                    $scope.userClicked = function (user) {
                        $scope.$emit('ccc-college-test-location-users.userClicked', user);
                    };

                    $scope.getMappedGroupName = function (groupName) {
                        return RolesService.getRoleTitle(groupName);
                    };


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-college-test-location-users.requestRefresh', refreshUserList);


                    /*============ INITIALIZATION ============*/

                    refreshUserList();
                }
            ],

            template: [

                '<ccc-content-loading-placeholder ng-hide="users.length > 0" no-results-info="users.length === 0 && !loading" hide-default-no-results-text="true" class="ccc-content-loading-placeholder-small">',
                    '<div><span translate="CCC_VIEW_HOME.CCC-DISTRICT-LIST.USERS.NO_RESULTS_TEST_LOCATION"></span> <button ng-click="addUser()" class="btn btn-sm btn-default" ng-disabled="isDisabled" translate="CCC_VIEW_HOME.CCC-DISTRICT-LIST.USERS.NO_RESULTS.BUTTON" aria-describedby="college-{{::college.cccId}}"></button></div>',
                '</ccc-content-loading-placeholder>',

                '<ul class="list-unstyled user-list user-list-stacked">',
                    '<li ng-click="userClicked(user)" ng-repeat="user in users track by $index" class="user">',
                        '<div class="name">',
                            '<span ng-if="user.lastName" class="last">{{::user.lastName}},</span> {{::user.firstName}} <span ng-if="user.middleInitial">{{::user.middleInitial}}.</span>',
                            '<span class="text-fade"></span>',
                        '</div>',
                        '<div ng-if="user.roles.length" class="profile-data">',
                            '<span ng-repeat="role in user.roles track by $index" class="role">{{::getMappedGroupName(role.role)}}</span>',
                        '</div>',
                   ' </li>',
                '</ul>'

            ].join('')
        };
    });

})();
