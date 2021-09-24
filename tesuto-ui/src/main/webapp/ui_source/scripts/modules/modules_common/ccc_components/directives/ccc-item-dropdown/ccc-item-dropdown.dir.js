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

    angular.module('CCC.Components').directive('cccItemDropdown', function () {

        var dropdownId = 0;
        var getItemDropdownId = function () {
            dropdownId++;
            return dropdownId;
        };

        return {

            restrict: 'E',

            scope: {
                listStyle: '@?',
                initialItemId: '=?',
                initialItems: '=',
                loading: '=?',
                icon: '@?',
                getItemId: '=?',
                getItemName: '=?',
                isDisabled: '=?',
                isRequired: '=?',
                labelledBy: '@?',
                requiredErrorMsg: '@?',
                placeholder: '@?'
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',

                function ($scope, $element, $timeout) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    // provided id must no have hyphens..
                    var dropdownIdProvided = false;
                    if ($element.attr('id')) {
                        dropdownIdProvided = $element.attr('id');
                    }


                    /*============ MODEL ============*/

                    $scope.icon = $scope.icon || false;
                    $scope.loading = $scope.loading || false;

                    $scope.initialItemId = $scope.initialItemId === undefined ? false : $scope.initialItemId;

                    $scope.listStyle = $scope.listStyle || '';

                    $scope.getItemId = $scope.getItemId || function (item) {return item.id;};
                    $scope.getItemName = $scope.getItemName || function (item) {return item.name;};

                    $scope.items = $scope.initialItems || [];

                    $scope.currentItem = false;

                    $scope.placeholder = $scope.placeholder || false;

                    if (dropdownIdProvided) {
                        $scope.dropdownId = dropdownIdProvided;
                    } else {
                        $scope.dropdownId = 'cccItemDropdownForm' + getItemDropdownId();
                    }


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var getCurrentItem = function () {

                        if ($scope.currentItemId === false) {
                            return false;
                        } else {
                            return _.find($scope.items, function (item) {
                                return $scope.getItemId(item) === $scope.currentItemId;
                            });
                        }
                    };


                    /*============ BEHAVIOR ============*/

                    $scope.setCurrentItem = function (itemId, keepClean) {

                        $scope.currentItemId = itemId;
                        $scope.currentItem = getCurrentItem();

                        if ($scope.currentItem) {

                            if (!keepClean) {
                                $scope[$scope.dropdownId].$setDirty(true);
                            }

                            $scope.$emit('ccc-item-dropdown.itemSelected', $scope.dropdownId, $scope.currentItemId, $scope.currentItem);
                        }
                    };

                    $scope.getMessages = function () {

                        if ($scope[$scope.dropdownId]) {
                            return $scope[$scope.dropdownId].currentItem.$error;
                        } else {
                            return {};
                        }
                    };


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-item-dropdown.setItems', function (e, items, dropdownId_in) {
                        if (!dropdownId_in || dropdownId_in && dropdownId_in === $scope.dropdownId) {
                            $scope.items = items;
                            $scope.setCurrentItem($scope.currentItemId, true);
                        }
                    });

                    $scope.$on('ccc-item-dropdown.clearSelection', function (e, dropdownId_in) {
                        if (!dropdownId_in || dropdownId_in && dropdownId_in === $scope.dropdownId) {
                            $scope.setCurrentItem(false);
                        }
                    });

                    // setting the id from the outside will not mark the form dirty... you can do so externally by looking for the id of the form attached to the parent scope
                    $scope.$on('ccc-item-dropdown.setSelectedItemId', function (e, selectedItemId, dropdownId_in) {

                        if (!dropdownId_in || dropdownId_in && dropdownId_in === $scope.dropdownId) {
                            $scope.setCurrentItem(selectedItemId, true);
                        }
                    });


                    /*============ INITIALIZATION ============*/

                    // propagate the ccc-autofocus attribute down to the button
                    if ($element.attr('ccc-autofocus')) {
                        $element.removeAttr('ccc-autofocus');
                        $element.find('button').attr('ccc-autofocus','ccc-autofocus');
                    }

                    if ($scope.initialItemId !== false) {

                        // in case ccc-item-dropdown.setItems is called before the digest below finishes or someone changes the selected item go ahead and store it
                        $scope.currentItemId = $scope.initialItemId;

                        // give ng-form a digest cycle to add the form to $scope and then set the current item
                        $timeout(function () {
                            $scope.setCurrentItem($scope.currentItemId, true);
                        }, 1);
                    }
                }
            ],

            template: [

                '<ng-form name="{{::dropdownId}}">',

                    '<div ccc-dropdown-focus class="btn-group" ng-class="{\'ng-invalid\': isRequired && !currentItem}" name="currentItem" ccc-validation-badge="{{::dropdownId}}" ccc-validation-badge-style="fullWidth">',
                        '<button ng-disabled="loading || isDisabled" type="button" ccc-focusable class="btn dropdown-toggle" ng-class="{\'btn-primary\': listStyle === \'primary\', \'btn-default\': listStyle !== \'primary\'}" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" aria-describedby="{{::dropdownId}}Errors" ng-attr-aria-labelledby="{{::labelledBy}}">',
                            '<i ng-if="icon" class="fa {{::icon}} noanim" aria-hidden="true"></i>',
                            '<span ng-if="loading" class="noanim">Loading <span class="fa fa-spinner fa-spin" aria-hidden="true"></span></span>',
                            '<span ng-if="!loading && items.length === 0" translate="CCC_COMP.CCC-ITEM-DROPDOWN.NO_ITEMS" class="noanim"></span>',
                            '<span ng-if="!loading && items.length !== 0" class="noanim">',
                                '<span ng-if="currentItem" class="noanim">{{getItemName(currentItem)}}</span>',
                                '<span ng-if="!currentItem && placeholder" class="noanim" translate="{{::placeholder}}"></span>',
                                '<span ng-if="!currentItem && !placeholder" class="noanim" translate="CCC_COMP.CCC-ITEM-DROPDOWN.PLEASE_SELECT"></span>',
                            '</span>',
                            '<span ng-if="!loading" class="caret noanim" aria-hidden="true"></span>',
                        '</button>',
                        '<ul class="dropdown-menu">',
                            '<li ng-repeat="item in items track by getItemId(item)" class="dont-break-out" ng-class="{selected: getItemId(item) === currentItemId}">',
                                '<a href="#" ng-click="setCurrentItem(getItemId(item))">{{::getItemName(item)}}</a>',
                            '</li>',
                        '</ul>',
                        '<input type="hidden" ng-model="currentItem" ccc-validation-expression="!isRequired || currentItem" name="currentItem">',
                    '</div>',

                    '<div id="{{::dropdownId}}Errors" class="ccc-validation-messages noanim" ng-messages="getMessages()">',
                        '<p ng-message="cccValidationExpression" class="noanim">',
                            '<span translate="{{::requiredErrorMsg}}"></span>',
                        '</p>',
                    '</div>',

                '</ng-form>'

            ].join('')
        };
    });

})();
