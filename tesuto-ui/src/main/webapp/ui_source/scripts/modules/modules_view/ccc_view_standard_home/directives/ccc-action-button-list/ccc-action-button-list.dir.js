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

    angular.module('CCC.View.Home').directive('cccActionButtonList', function () {

        return {

            restrict: 'E',

            scope: {
                actions: '='
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============ MODEL =============*/


                    /*============ BEHAVIOR =============*/

                    $scope.actionClicked = function (e, action) {

                        e.preventDefault();
                        $scope.$emit('ccc-action-button-list.actionClicked', action);
                    };
                }
            ],

            template: [

                '<ul>',
                    '<li ng-repeat="action in actions | orderBy: \'title\'" class="ccc-action-button-list-item clickable">',
                        '<a href="#" role="button" ng-click="actionClicked($event, action)">',
                            '<div class="visual">',
                                '<img ng-src="{{::action.icon}}" alt="{{ action.title | translate}} icon"/>',
                            '</div>',
                            '<h3 class="title" translate="{{::action.title}}"></h3>',
                        '</a>',
                    '</li>',
                '</ul>'

            ].join('')

        };

    });

})();





