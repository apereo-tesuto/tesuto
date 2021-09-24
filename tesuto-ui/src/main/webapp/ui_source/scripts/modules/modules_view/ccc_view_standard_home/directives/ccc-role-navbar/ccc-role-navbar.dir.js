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

    angular.module('CCC.View.Home').directive('cccRoleNavbar', function () {

        return {

            restrict: 'E',

            scope: {
                actions: '='
            },

            controller: [

                '$scope',
                '$state',

                function ($scope, $state) {

                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-action-button-list.actionClicked', function (e, action) {
                        $state.go(action.state);
                    });
                }
            ],

            template: [

                '<div class="alert alert-warning" ng-if="actions.length === 0" translate="CCC_VIEW_HOME.ROLE_NAV.NO_ACTIONS"></div>',
                '<ccc-action-button-list actions="actions"></ccc-action-button-list>'

            ].join('')

        };

    });

})();
