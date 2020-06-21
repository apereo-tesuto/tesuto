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

    angular.module('CCC.View.Home').directive('cccCollegeUsers', function () {

        return {

            restrict: 'E',

            scope: {
                college: '='
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
                    $scope.users = [];


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var refreshUserList = function () {
                        $scope.loading = true;
                        $scope.users = [];

                        UsersAPIService.getUsersByCollege($scope.college.cccId).then(function (users) {

                            users = preProcessUsers(users);

                            $scope.users = _.sortBy(users, function (user) {
                                return user.lastName;
                            });

                        }).finally(function () {
                            $scope.loading = false;
                        });
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.userClicked = function (user) {
                        $scope.$emit('ccc-college-users.userClicked', user);
                    };

                    $scope.getMappedGroupName = function (groupName) {
                        return RolesService.getRoleTitle(groupName);
                    };

                    $scope.addUser = function () {
                        $scope.$emit('ccc-college-users.addUser');
                    };


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-college-users.requestRefresh', refreshUserList);


                    /*============ INITIALIZATION ============*/

                    refreshUserList();
                }
            ],

            template: [

                '<ccc-content-loading-placeholder ng-hide="users.length > 0" no-results-info="users.length === 0 && !loading" hide-default-no-results-text="true">',
                    '<div><span translate="CCC_VIEW_HOME.CCC-DISTRICT-LIST.USERS.NO_RESULTS.MSG"></span> <button class="btn btn-sm btn-default" translate="CCC_VIEW_HOME.CCC-DISTRICT-LIST.USERS.NO_RESULTS.BUTTON" aria-describedby="college-{{::college.cccId}}" ng-click="addUser()"></button></div>',
                '</ccc-content-loading-placeholder>',

                '<ul class="list-unstyled user-list user-list-stacked">',
                    '<li ng-click="userClicked(user)" ng-repeat="user in users track by $index" class="user">',
                        '<div class="name">',
                            '<span ng-if="user.lastName" class="last">{{::user.lastName}},</span> {{::user.firstName}} <span ng-if="user.middleInitial">{{::user.middleInitial}}.</span>',
                            '<span class="text-fade"></span>',
                        '</div>',
                        '<div ng-if="user.securityGroupDtos.length" class="profile-data">',
                            '<span ng-repeat="group in user.securityGroupDtos track by $index" class="role">{{::getMappedGroupName(group.groupName)}}</span>',
                        '</div>',
                    '</li>',
                '</ul>'

            ].join('')
        };
    });

})();
