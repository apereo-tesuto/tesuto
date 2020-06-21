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

    angular.module('CCC.View.Home').directive('cccUserEditById', function () {

        return {

            restrict: 'E',

            scope: {
                userAccountId: '='
            },

            controller: [

                '$scope',
                'UsersAPIService',

                function ($scope, UsersAPIService) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    $scope.user = false;
                    $scope.loading = true;


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var initialize = function () {

                        $scope.loading = true;

                        UsersAPIService.getUserById($scope.userAccountId).then(function (user) {
                            $scope.user = user;
                        }).finally(function () {
                            $scope.loading = false;
                        });
                    };


                    /*============ BEHAVIOR ==============*/

                    /*============ LISTENERS ==============*/


                    /*============ INITIALIZATION ==============*/

                    initialize();
                }
            ],

            template: [

                '<ccc-content-loading-placeholder ng-if="loading || !loading && !user" no-results="!user && !loading"></ccc-content-loading-placeholder>',

                '<ccc-user-edit user="user" ng-if="!loading && user"></ccc-user-edit>'

            ].join('')
        };

    });

})();
