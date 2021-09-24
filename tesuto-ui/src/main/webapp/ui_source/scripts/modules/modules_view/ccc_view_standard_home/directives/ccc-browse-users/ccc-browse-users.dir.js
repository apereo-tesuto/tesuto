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

    angular.module('CCC.View.Home').directive('cccBrowseUsers', function () {

        return {

            restrict: 'E',

            scope: {},

            controller: [

                '$scope',
                'UsersAPIService',

                function ($scope, UsersAPIService) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    $scope.users = [];
                    $scope.selectedFacets = {};

                    $scope.loading = true;
                    $scope.errorLoading = false;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var updateUsers = function (users_in) {
                        $scope.users = users_in;
                        $scope.$broadcast('ccc-user-faceted-search.updateData', $scope.users);
                    };

                    var getUsers = function () {

                        $scope.loading = true;
                        updateUsers([]);

                        UsersAPIService.userListSearch().then(function (userList) {

                            updateUsers(userList);

                        }, function () {

                            $scope.errorLoadingActivations = true;

                        })
                        .finally(function () {
                            $scope.loading = false;
                        });
                    };

                    var getUsersDebounced = _.debounce(function () {

                        getUsers();
                        $scope.$apply();
                    }, 100);


                    /*============ BEHAVIOR ==============*/

                    $scope.refreshUsers = function () {
                        getUsersDebounced();
                    };


                    /*============ LISTENERS ==============*/

                    // we cache any changes in facets so we can pass them back in when we redraw the faceted search
                    $scope.$on('ccc-user-faceted-search.selectedFacetsUpdated', function (e, selectedFacetMap) {
                        $scope.selectedFacets = selectedFacetMap;
                    });

                    // we can be asked to update our results
                    $scope.$on('ccc-user-search.requestRefresh', $scope.refreshUsers);

                    $scope.$on('ccc-user-faceted-search.addUser', function () {
                        $scope.$emit('ccc-user-search.addUser');
                    });


                    /*============ INITIALIZATION ==============*/

                    getUsersDebounced();

                }
            ],

            template: [

                '<div class="row margin-bottom-double">',
                    '<div class="col-sm-12">',
                        '<ccc-user-faceted-search loading="loading" users="users" selected-facets="selectedFacets"></ccc-user-faceted-search>',
                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();
