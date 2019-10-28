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

    angular.module('CCC.View.Home').directive('cccManageUsersDashboard', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',

                function ($scope) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/

                    /*============= MODEL =============*/

                    /*============= MODEL DEPENDENT METHODS =============*/


                    /*============= BEHAVIOR =============*/

                    $scope.searchUsers = function () {
                        $scope.$emit('ccc-manage-users-dashboard.searchUsers');
                    };

                    $scope.createUser = function () {
                        $scope.$emit('ccc-manage-users-dashboard.createUser');
                    };


                    /*============= LISTENERS =============*/

                    /*============ INITIALIZATION ============*/

                }
            ],

            template: [

                '<div class="row">',

                    '<div class="col-md-12">',

                        '<div class="ccc-dashboard-action">',
                            '<div class="ccc-dashboard-action-button">',
                                '<button ccc-autofocus class="btn btn-primary btn-full-width" ng-click="searchUsers()"><i class="fa fa-search" aria-hidden="true"></i> Find a User</button>',
                            '</div>',
                            '<div class="ccc-dashboard-action-description">',
                                'Search for a particular user to view or modify their information.',
                            '</div>',
                        '</div>',

                        '<div class="ccc-dashboard-action">',
                            '<div class="ccc-dashboard-action-button">',
                                '<button class="btn btn-primary btn-full-width" ng-click="createUser()"><i class="fa fa-plus" aria-hidden="true"></i> Add a User</button>',
                            '</div>',
                            '<div class="ccc-dashboard-action-description">',
                                'Add a new user and configure their permissions and associated colleges.',
                            '</div>',
                        '</div>',

                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();
