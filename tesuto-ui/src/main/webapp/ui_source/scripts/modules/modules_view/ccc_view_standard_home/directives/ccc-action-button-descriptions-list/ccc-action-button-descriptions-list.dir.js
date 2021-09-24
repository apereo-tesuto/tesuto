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

    angular.module('CCC.View.Home').directive('cccActionButtonDescriptionsList', function () {

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
                        $scope.$emit('ccc-action-button-descriptions-list.actionClicked', action);
                    };
                }
            ],

            template: [

                '<ul>',
                    '<li ng-repeat="action in actions" class="ccc-action-button-descriptions-list-item">',
                        '<a href="#" class="clearfix" role="button" ng-click="actionClicked($event, action)">',

                            '<span class="ccc-action-button-list-item clickable">',
                                '<div class="visual">',
                                    '<img ng-src="{{::action.icon}}" alt="icon" ng-if="::action.icon"/>',
                                    '<i ng-if="::action.fontIcon" class="{{::action.fontIcon}}" aria-hidden="true"></i>',
                                '</div>',
                                '<h3 class="title" translate="{{::action.title}}"></h3>',
                            '</span>',

                            '<p class="ccc-action-button-list-item-description" translate="{{::action.description}}"></p>',

                        '</a>',
                    '</li>',
                '</ul>'

            ].join('')

        };

    });

})();





