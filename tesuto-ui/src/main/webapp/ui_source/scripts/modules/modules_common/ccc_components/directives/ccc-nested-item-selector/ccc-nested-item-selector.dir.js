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

    /*============ ACTUAL NESTED ITEM SELECTOR DIRECTIVE ===========*/

    angular.module('CCC.Components').directive('cccNestedItemSelector', function () {

        var itemSelectorId = 0;
        var getItemSelectorId = function () {
            return itemSelectorId++;
        };

        return {

            restrict: 'E',

            scope: {

                items: '=',

                isDisabled: '=',

                disableCollapse: '=?',

                disableAutoExpandOnInit: '=?',

                disableChildAutoDeselect: '=?',
                disableChildAutoSelect: '=?',

                enableEnforceParentsSelected: '=?',
                enableEnforceParentsDeselected: '=?'
            },

            controller: [

                '$scope',
                '$element',
                'NestedItemClass',

                function ($scope, $element, NestedItemClass) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    // we formalize the contract of what a nested item can be with the NestedItemClass
                    // so we loop through the initial data and generate instances of this class recursively through top level items and their "children"
                    var addItemsToModel = function (itemsArray, itemsList) {

                        _.each(itemsList, function (item) {

                            var itemInstance = new NestedItemClass(item);
                            itemsArray.push(itemInstance);

                            // we need to convert the children, so we save the original objects and empty out the children array
                            var oldChildren = itemInstance.children;
                            itemInstance.children = [];

                            addItemsToModel(itemInstance.children, oldChildren);
                        });
                    };

                    var eachNestedItem = function (nestedItems, callBack) {
                        _.each(nestedItems, function (item) {
                            callBack(item);
                            if (item.children && item.children.length) {
                                eachNestedItem(item.children, callBack);
                            }
                        });
                    };

                    var selectAllChildren = function (nestedItem) {

                        _.each(nestedItem.children, function (nestedItemChild) {

                            var originalSelectedValue = nestedItemChild.selected;
                            nestedItemChild.selected = true;

                            if (!originalSelectedValue) {
                                nestedItemChild.onSelected(nestedItemChild);
                                selectAllChildren(nestedItemChild);
                            }
                        });
                    };

                    var deselectAllChildren = function (nestedItem) {

                        _.each(nestedItem.children, function (nestedItemChild) {

                            var originalSelectedValue = nestedItemChild.selected;
                            nestedItemChild.selected = false;

                            if (originalSelectedValue) {
                                nestedItemChild.onDeselected(nestedItemChild);
                                deselectAllChildren(nestedItemChild);
                            }
                        });
                    };


                    /*============ MODEL ============*/

                    $scope.itemSelectorId = getItemSelectorId();

                    $scope.disableCollapse = $scope.disableCollapse || false;

                    $scope.disableAutoExpandOnInit = $scope.disableAutoExpandOnInit || false;

                    $scope.disableParentAutoSelect = $scope.disableParentAutoSelect || false;
                    $scope.disableChildAutoDeselect = $scope.disableChildAutoDeselect || false;

                    $scope.enableEnforceParentsSelected = $scope.enableEnforceParentsSelected || false;
                    $scope.enableEnforceParentsDeselected = $scope.enableEnforceParentsDeselected || false;

                    $scope.itemsModel = [];


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var populateItemsModel = function () {

                        $scope.itemsModel = [];
                        addItemsToModel($scope.itemsModel, $scope.items);
                    };

                    // check it the item should be selected or deselected based on descendents selection values and a provided selectionMode
                    var setAndGetSelectedValue = function (item, selectionMode) {

                        var wasOriginallySelected = item.selected;

                        var hasDescendents = (item.children && item.children.length);

                        // this logic only applies to items that have children
                        if (hasDescendents) {

                            var foundMatchingDescendent = false;

                            _.each(item.children, function (childItem) {

                                var isChildSelected = setAndGetSelectedValue(childItem, selectionMode);
                                var childOrDescendentMatches = (selectionMode === 'select' && isChildSelected) || (selectionMode === 'deselect' && !isChildSelected);

                                foundMatchingDescendent = foundMatchingDescendent || childOrDescendentMatches;
                            });

                            if (selectionMode === 'select') {

                                item.selected = (wasOriginallySelected || foundMatchingDescendent);

                            } else if (selectionMode === 'deselect') {

                                item.selected = foundMatchingDescendent ? false : (!foundMatchingDescendent || wasOriginallySelected);
                            }

                            // if we manually changed this items selection value we need to fire off it's callback
                            if (wasOriginallySelected !== item.selected) {

                                if (wasOriginallySelected) {
                                    item.onDeselected(item);
                                } else {
                                    item.onSelected(item);
                                }
                            }
                        }

                        return item.selected;
                    };

                    var expandSelectedItems = function () {
                        eachNestedItem($scope.itemsModel, function (item) {
                            item.expanded = item.selected;
                        });
                    };

                    var ensureParentsOfSelectedChildrenAreSelected = function () {
                        _.each($scope.itemsModel, function (item) {
                            setAndGetSelectedValue(item, 'select');
                        });
                    };

                    var ensureParentsOfDeselectedChildrenAreDeselected = function () {
                        _.each($scope.itemsModel, function (item) {
                            setAndGetSelectedValue(item, 'deselect');
                        });
                    };

                    var enforceDataRules = function () {

                        if ($scope.enableEnforceParentsSelected) {
                            ensureParentsOfSelectedChildrenAreSelected();
                        }
                        if ($scope.enableEnforceParentsDeselected) {
                            ensureParentsOfDeselectedChildrenAreDeselected();
                        }
                    };

                    var runAutoSelectionRules = function (itemUpdated) {

                        if (!itemUpdated.selected && !$scope.disableChildAutoDeselect) {
                            deselectAllChildren(itemUpdated);
                        }
                        if (itemUpdated.selected && !$scope.disableChildAutoSelect) {
                            selectAllChildren(itemUpdated);
                        }
                    };

                    var initialize = function () {

                        populateItemsModel();
                        enforceDataRules();

                        if (!$scope.disableAutoExpandOnInit) {
                            expandSelectedItems();
                        }
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.nestedItemClass = function (item) {
                        return {
                            'ccc-nested-item-selected': item.selected
                        };
                    };


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-nested-item.updated', function (e, item) {

                        runAutoSelectionRules(item);
                        enforceDataRules();
                    });

                    $scope.$on('ccc-nested-item-selector.requestUpdate', function (e) {
                        initialize();
                    });


                    /*============ INITIALIZATION ============*/

                    initialize();
                }
            ],

            template: [

                '<ul ng-class="{\'ccc-nested-item-selector-collapse-disabled\':disableCollapse, \'ccc-nested-item-selector-disabled\': isDisabled}">',
                    '<li ng-repeat="item in itemsModel track by item.id" ccc-nested-item="item" disable-collapse="disableCollapse" level="0" class="ccc-nested-item-level-even" ng-class="nestedItemClass(item)" is-disabled="isDisabled" item-selector-id="itemSelectorId"></li>',
                '</ul>'

            ].join('')
        };
    });


    /*============ RECURSIVE LIST ITEM DIRECTIVE ===========*/

    angular.module('CCC.Components').directive('cccNestedItem', function () {

        return {

            restrict: 'A',

            scope: {
                cccNestedItem: '=',
                itemSelectorId: '=',
                level: '=',
                isDisabled: '=',
                disableCollapse: '='
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',

                function ($scope, $element, $timeout) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/

                    var isEvenLevel = ($scope.level + 1) % 2 === 0;


                    /*============ MODEL ==============*/

                    $scope.nextLevel = $scope.level + 1;

                    $scope.cccNestedItemId = 'ccc-nested-level-' + $scope.level + '-item-' + $scope.cccNestedItem.id;


                    /*============ MODEL DEPENDENT METHODS ============*/


                    /*============ BEHAVIOR ==============*/

                    $scope.nestedItemClass = function (item) {
                        return {
                            'ccc-nested-item-selected': item.selected,
                            'ccc-nested-item-level-even': isEvenLevel,
                            'ccc-nested-item-level-odd': !isEvenLevel
                        };
                    };

                    $scope.toggleClicked = function  (e, cccNestedItem) {

                        e.preventDefault();
                        e.stopPropagation();

                        cccNestedItem.expanded = !cccNestedItem.expanded;
                    };

                    $scope.checkBoxClicked = function () {

                        $timeout(function () {

                            if ($scope.cccNestedItem.selected) {

                                $scope.cccNestedItem.expanded = true;
                                $scope.cccNestedItem.onSelected($scope.cccNestedItem);

                            } else {
                                $scope.cccNestedItem.onDeselected($scope.cccNestedItem);
                            }

                            $scope.$emit('ccc-nested-item.updated', $scope.cccNestedItem);

                        }, 1);
                    };


                    /*============ LISTENERS ==============*/

                    /*============ INITIALIZATION ==============*/
                }
            ],

            template: [

                '<span class="ccc-nested-item-selector-content" ng-class="{disabled: cccNestedItem.disabled}">',

                    '<span class="ccc-nested-item-selector-content-controls">',

                        '<span class="nested-toggle">',
                            '<button class="btn btn-sm btn-default btn-icon-only" tabindex="0" role="button" ng-disabled="!cccNestedItem.children.length" ng-if="cccNestedItem.children.length" ng-click="toggleClicked($event, cccNestedItem)">',
                                '<i class="fa fa-caret-down noanim" aria-hidden="true" ng-if="cccNestedItem.expanded"></i><span class="sr-only" ng-if="!cccNestedItem.expanded">{{cccNestedItem.name}} toggle closed</span>',
                                '<i class="fa fa-caret-right noanim" aria-hidden="true" ng-if="!cccNestedItem.expanded"></i><span class="sr-only" ng-if="cccNestedItem.expanded">{{cccNestedItem.name}} toggle open</span>',
                            '</button>',
                            '&nbsp;',
                        '</span>',

                        '<input type="checkbox" id="{{::itemSelectorId}}-{{::cccNestedItemId}}" ng-model="cccNestedItem.selected" ng-disabled="cccNestedItem.disabled || isDisabled" ng-click="checkBoxClicked()" aria-labelledby="{{::itemSelectorId}}-{{::cccNestedItemId}}-label"/>',

                        '<span class="ccc-nested-item-print-checkbox"></span>',

                    '</span>',

                    '<label id="{{::itemSelectorId}}-{{::cccNestedItemId}}-label" for="{{::itemSelectorId}}-{{::cccNestedItemId}}" translate="{{cccNestedItem.name}}"></label>',

                '</span>',

                '<ul ng-if="(cccNestedItem.children.length && cccNestedItem.expanded) || disableCollapse">',
                    '<li ng-repeat="childItem in cccNestedItem.children track by childItem.id" ccc-nested-item="childItem" disable-collapse="disableCollapse" level="nextLevel" ng-class="nestedItemClass(childItem)" is-disabled="isDisabled" item-selector-id="itemSelectorId"></li>',
                '</ul>'

            ].join('')

        };

    });


    /*============ FACTORY CLASS USED WITHIN NESTED ITEM SELECTOR ===========*/

    angular.module('CCC.Components').factory('NestedItemClass', [

        function () {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/

            /*============ CLASS DECLARATION ============*/

            var NestedItemClass = function (configs_in) {

                var defaults = {

                    id: null,
                    name: '',
                    item: null,

                    expanded: false,
                    selected: false,    // is this item currently selected
                    disabled: false,    // is this item disabled from being toggled as far as selection goes

                    onSelected: $.noop,
                    onDeselected: $.noop,

                    children: []
                };

                // we simply tack on merged properties during initialization
                var that = this;
                $.extend(true, that, defaults, configs_in, {});


                /*============ PRIVATE VARIABLES AND METHODS ============*/

                /*============ PUBLIC METHODS ============*/

                /*============ INITIALIZATION ============*/
            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/

            /*============ CLASS PASSBACK ============*/

            return NestedItemClass;
        }
    ]);

})();
