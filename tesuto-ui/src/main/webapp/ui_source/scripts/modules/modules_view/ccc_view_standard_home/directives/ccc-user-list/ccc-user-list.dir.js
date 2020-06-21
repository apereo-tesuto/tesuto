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

    angular.module('CCC.View.Home').directive('cccUserList', function () {

        return {

            restrict: 'E',

            scope: {
                users: '='
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/

                    /*============= MODEL =============*/

                    /*============= MODEL DEPENDENT METHODS =============*/


                    /*============= BEHAVIOR =============*/

                    $scope.selectUser = function ($event, user) {
                        var userElement = $($event.currentTarget);
                        $scope.$emit('ccc-user-list.selected', user, userElement);
                    };


                    /*============= LISTENERS =============*/

                }
            ],

            template: [

                '<ul class="user-list flex-list">',
                    '<li class="ccc-anim-fade-in flex-list-item" ng-repeat="user in users | orderBy:[\'lastName\',\'firstName\',\'middleName\'] track by user.userAccountId" data-user-id="{{user.cccId}}">',
                        '<ccc-system-user user="user" class="ccc-user clickable" tabindex="0" ng-click="selectUser($event, user)"></ccc-system-user>',
                    '</li>',
                '</ul>'

            ].join('')

        };

    });

})();
