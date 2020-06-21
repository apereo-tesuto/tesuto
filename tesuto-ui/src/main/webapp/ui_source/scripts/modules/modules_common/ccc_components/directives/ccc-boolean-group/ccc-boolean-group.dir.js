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

    // IMPORTANT: These directives create a nested hiearchy of dom elements, so lot's of event propagation needs to be stopped
    // This helps avoid one event on an item triggering events on a parent item

    angular.module('CCC.Components').directive('cccBooleanGroup', function () {

        return {

            restrict: 'E',

            scope: {
                booleanGroup: '=',
                isDisabled: '=?',
                parentCount: '=?',
            },

            controller: [

                '$scope',
                '$element',

                function ($scope, $element) {

                    /*============ PRIVATE VARIABLES AND METHODS =============*/

                    /*============ MODEL =============*/

                    /*============ MODEL DEPENDENT METHODS =============*/


                    /*============ BEHAVIOR =============*/

                    $scope.addItem = function (e) {
                        e.preventDefault();
                        e.stopPropagation();
                        $scope.booleanGroup._addItem(function (item) {
                            $scope.$emit('ccc-boolean-group.itemAdded', item);
                        });
                    };

                    $scope.addGroup = function (e) {
                        e.preventDefault();
                        e.stopPropagation();
                        $scope.booleanGroup._addGroup(function (groupItem) {
                            $scope.$emit('ccc-boolean-group.groupAdded', groupItem);
                        });
                    };


                    /*============ LISTENERS =============*/

                    $scope.$on('ccc-boolean-item.clicked', function (e, item) {
                        e.stopPropagation();

                        if (!$scope.isDisabled) {
                            $scope.$emit('ccc-boolean-group.itemClicked', item.data);
                        }
                    });

                    $scope.$on('ccc-boolean-item.deleted', function (e, item) {
                        e.stopPropagation();
                        $scope.booleanGroup.removeItem(item.booleanItemId);
                        $scope.$emit('ccc-boolean-group.itemDeleted', item.data);
                    });


                    /*============ INITIALIZATION =============*/
                }
            ],

            template: [

                '<ul ng-if="booleanGroup.booleanItems.length" class="ccc-boolean-group-ul">',
                    '<li ng-repeat="item in booleanGroup.booleanItems track by item.booleanItemId">',
                        '<ccc-boolean-item item="item" item-deleted-event="booleanGroup.itemDeletedEvent" item-index="parentCount ? (parentCount + $index) : $index" item-clicked-event="booleanGroup.itemClickedEvent" is-disabled="isDisabled"></ccc-boolean-item>',
                    '</li>',
                '</ul>',

                '<div class="ccc-boolean-group-buttons">',

                    '<div class="btn-group">',
                        '<button class="btn btn-success add-group ccc-boolean-item-button" ng-click="addItem($event)" ng-disabled="booleanGroup.loading || isDisabled">',
                            '<span class="icon fa fa-spin fa-spinner noanim" role="presentation" aria-hidden="true" ng-if="booleanGroup.itemLoading"></span> ',
                            '<span class="icon fa fa-plus noanim" role="presentation" aria-hidden="true" ng-if="!booleanGroup.itemLoading"></span> ',
                            '<span class="sr-only">Add A Single Item</span>',
                        '</button>',
                        '<button class="btn btn-success add-group-set ccc-boolean-item-button" ng-disabled="booleanGroup.loading || isDisabled" ng-click="addGroup($event)">',
                            '[ ',
                                '<span class="icon fa fa-spin fa-spinner noanim" role="presentation" aria-hidden="true" ng-if="booleanGroup.groupLoading"></span> ',
                                '<span class="icon fa fa-plus noanim" role="presentation" aria-hidden="true" ng-if="!booleanGroup.groupLoading"></span>',
                               '<span class="sr-only">Add A Group of Items</span>',
                            ' ]',
                        '</button>',
                    '</div>',

                '</div>'

            ].join('')

        };

    });


    /*============ SUPPORTING DIRECTIVE FOR CCC-BOOLEAN-GROUP ============*/

    angular.module('CCC.Components').directive('cccBooleanItem', function () {

        return {

            restrict: 'E',

            scope: {
                item: '=',
                isDisabled: '=',
                itemDeletedEvent: '=',
                itemClickedEvent: '=',
                itemIndex: '='
            },

            controller: [

                '$scope',
                '$element',
                '$compile',
                'BooleanGroupClass',
                'BooleanItemClass',

                function ($scope, $element, $compile, BooleanGroupClass, BooleanItemClass) {

                    /*============ PRIVATE VARIABLES AND METHODS =============*/

                    var isBooleanGroup = $scope.item instanceof BooleanGroupClass;
                    var isBooleanItem = $scope.item instanceof BooleanItemClass;

                    /*============ MODEL =============*/


                    /*============ MODEL DEPENDENT METHODS =============*/

                    var renderBooleanItem = function () {

                        var itemTemplate;

                        if (isBooleanGroup) {

                            itemTemplate = '<ccc-boolean-group boolean-group="item" parent-count="itemIndex" is-disabled="isDisabled"></ccc-boolean-group>';

                        } else if (isBooleanItem){

                            // expose data to the item directive
                            $scope.data = $scope.item.data;

                            // expose this item's index
                            $scope.itemIndex = $scope.itemIndex;

                            itemTemplate = $scope.item.directive;

                        } else {

                            throw new Error('ccc-boolean-item-renderer.invalidItem');
                        }

                        var itemElement = $(itemTemplate);

                        // if the element supports the is-disabled value
                        itemElement.attr('is-disabled',"isDisabled");

                        $element.find('.ccc-boolean-item-renderer-target').append(itemElement);
                        $compile(itemElement)($scope);
                    };


                    /*============ BEHAVIOR =============*/

                    $scope.changeOperator = function (newOperator) {

                        if ($scope.item.operator !== newOperator) {
                            $scope.item.changeOperator(newOperator);
                        }
                    };


                    /*============ LISTENERS =============*/

                    if ($scope.itemDeletedEvent) {
                        $scope.$on($scope.itemDeletedEvent, function (e) {
                            e.stopPropagation();
                            $scope.$emit('ccc-boolean-item.deleted', $scope.item);
                        });
                    }

                    if ($scope.itemClickedEvent && isBooleanItem) {
                        $scope.$on($scope.itemClickedEvent, function (e) {
                            e.stopPropagation();
                            $scope.$emit('ccc-boolean-item.clicked', $scope.item);
                        });
                    }


                    /*============ INITIALIZATION =============*/

                    renderBooleanItem();
                }
            ],

            template: [

                '<span class="ccc-boolean-item-renderer-target"></span>',
                '<span class="ccc-boolean-item-renderer-operator" ng-if="item.operator">',
                    '<div ccc-dropdown-focus class="btn-group" role="group" aria-label="Boolean Operator">',
                        '<button class="btn btn-primary dropdown-toggle ccc-boolean-item-button" ng-disabled="isDisabled" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">',
                            '{{item.operator}} ',
                            '<span class="caret"></span>',
                        '</button>',
                        '<ul class="dropdown-menu">',
                            '<li><a href="#" ng-click="changeOperator(\'and\')">And</a></li>',
                            '<li><a href="#" ng-click="changeOperator(\'or\')">Or</a></li>',
                        '</ul>',
                    '</div>',
                '</span>',

            ].join('')
        };

    });

})();
