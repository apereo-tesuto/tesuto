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

    angular.module('CCC.Components').directive('cccGroupedItems', function () {

        return {

            restrict: 'E',

            scope: {
                groupedItemManager: "=",
                columns: "=?"
            },

            controller: [

                '$scope',

                function ($scope) {


                    /*============ MODEL ============*/

                    $scope.itemData = $scope.groupedItemManager.getGroupedItems();

                    $scope.columns = $scope.columns || 3;


                    /*============ BEHAVIOR ============*/

                    /*============ LISTENERS ============*/

                    $scope.groupedItemManager.addListener('groupedItemsUpdated', function (itemData) {
                        $scope.itemData = itemData;
                    });

                }
            ],

            template: [
                '<div class="ccc-grouped-items-group" ng-repeat="group in itemData track by $index" ng-class="{\'ccc-grouped-items-group-no-grouping\': !groupedItemManager.currentGrouping}">',
                    '<div class="row ccc-grouped-items-group-title" ng-if="groupedItemManager.currentGrouping">',
                        '<h2 class="col-xs-12" id="ccc-grouped-items-group-header-{{::$index}}" ng-bind-html="group.title"></h2>',
                    '</div>',
                    '<ul class="row ccc-grouped-items-group-row" aria-labelledby="ccc-grouped-items-group-header-{{::$index}}">',

                        '<li class="col-md-4 col-sm-6" ng-repeat="item in group.items" ng-if="columns == 3">',
                            '<ccc-grouped-item item="item" grouped-item-manager="groupedItemManager"></ccc-grouped-item>',
                        '</li>',

                        '<li class="col-md-6 col-sm-12" ng-repeat="item in group.items" ng-if="columns == 2">',
                            '<ccc-grouped-item item="item" grouped-item-manager="groupedItemManager"></ccc-grouped-item>',
                        '</li>',

                    '</ul>',
                '</div>'
            ].join('')

        };
    });

    angular.module('CCC.Components').directive('cccGroupedItem', function () {

        return {

            restrict: 'E',

            scope: {
                item: '=',
                groupedItemManager: '='
            },

            controller: [

                '$scope',
                '$element',
                '$compile',

                function ($scope, $element, $compile) {

                    /*============ MODEL ============*/

                    /*============ BEHAVIOR ============*/

                    /*============ LISTENERS ============*/

                    /*============ INITIALIZATION ============*/

                    var itemElem = $($scope.groupedItemManager.template);
                    itemElem.appendTo($element);

                    $compile(itemElem)($scope);
                }
            ]
        };
    });

})();
