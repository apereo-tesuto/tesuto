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

    angular.module('CCC.Components').directive('cccDetailedSelect', function () {

        /*============= PRIVATE STATIC VARIABLES AND METHODS =============*/

        var selectId = 0;
        var getSelectId = function () {
            return selectId++;
        };

        var optionId = 0;
        var getOptionId = function () {
            return optionId++;
        };


        /*============= DIRECTIVE CONFIGURATION =============*/

        return {

            restrict: 'E',

            scope: {
                id: "@?",
                options: "=",       // an array of objects {id: <optional>, label: <string>, value: <whatever you like>, helpHeader: <optional>, help: <optional>}
                multiSelect: "=?",
                isRequired: "=?",
                isDisabled: "=?",
                describedBy: '@?',
                isInvalid: '=?'
            },

            controller: [

                '$scope',

                function ($scope) {

                    /*============ PRIVATE VARIABLES/METHODS ===========*/


                    /*============ MODEL ============*/

                    $scope.id = $scope.id || getSelectId();

                    $scope.multiSelect = $scope.multiSelect || false;
                    $scope.isRequired = $scope.isRequired || false;

                    $scope.selectedOptions = [];


                    /*============ MODEL DEPENDENT METHODS ============*/

                    var ensureUniqueItemIds = function () {
                        _.each($scope.options, function (option) {

                            // for repeated items we must have a unique id
                            if (option.id === undefined) {
                                option.id = getOptionId();
                            }

                            // for convencience assume the style is "YES" for optoins that don't specify their YES/NO type
                            if (option.type === undefined) {
                                option.type = 'YES';
                            }
                        });
                    };

                    var updateSelectedOptions = function () {
                        $scope.updateSelectedOptions = _.filter($scope.options, function (option) {
                            return option.selected === true;
                        });
                    };

                    var selectionsChanged = function () {
                        updateSelectedOptions();
                        $scope.$emit('ccc-detailed-select.changed', $scope.id, $scope.updateSelectedOptions, $scope.options);
                    };


                    /*============ BEHAVIOR ============*/


                    /*============ LISTENERS =============*/

                    $scope.$on('ccc-detailed-select-single.changed', selectionsChanged);
                    $scope.$on('ccc-detailed-select-multi.changed', selectionsChanged);


                    /*============ INITIALIZATION ============*/

                    ensureUniqueItemIds();

                    updateSelectedOptions();
                }
            ],

            template: [

                '<div class="form-yes-no" role="group">',
                    '<ccc-detailed-select-single select-id="id" options="options" ng-if="!multiSelect" is-disabled="isDisabled" described-by="{{::describedBy}}" is-invalid="isInvalid"></ccc-detailed-select-single>',
                    '<ccc-detailed-select-multi select-id="id" options="options" ng-if="multiSelect" is-disabled="isDisabled" described-by="{{::describedBy}}" is-invalid="isInvalid"></ccc-detailed-select-multi>',
                '</div>'

            ].join('')
        };

    });


    angular.module('CCC.Components').directive('cccDetailedSelectSingle', function () {

        return {

            restrict: 'E',

            scope: {
                selectId: "=",
                options: "=",
                isDisabled: "=?",
                describedBy: "@?",
                isInvalid: '=?'
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',

                function ($scope, $element, $timeout) {

                    /*============ PRIVATE VARIABLES/METHODS ===========*/

                    /*============ MODEL ============*/


                    /*============ BEHAVIOR ============*/

                    $scope.optionClicked = function (event, option) {

                        if ($scope.isDisabled) {
                            event.preventDefault();
                            event.stopPropagation();
                            return false;
                        }

                        _.each($scope.options, function (option) {
                            option.selected = false;
                        });

                        option.selected = true;

                        $scope.$emit('ccc-detailed-select-single.changed');
                    };


                    /*============ INITIALIZATION ============*/
                }
            ],

            template: [

                '<label class="form-yes-no-section" ng-repeat="option in options track by option.id" ng-class="{\'active\':option.selected, \'yes\': option.type === \'YES\', \'no\': option.type === \'NO\'}">',
                    '<span ng-if="option.type === \'YES\'" class="icon fa fa-check-square-o" role="presentation" aria-hidden="true"></span>',
                    '<span ng-if="option.type === \'NO\'" class="icon fa fa-times-rectangle-o" role="presentation" aria-hidden="true"></span>',
                    '<span class="radio">',
                        '<input type="radio" ',
                            'id="ccc-detailed-select-option-{{::selectId + \'-\' + option.id}}" ',
                            'name="ccc-detailed-select-{{::selectId}}" ',
                            'ng-disabled="isDisabled" ',
                            'aria-invalid="{{isInvalid === undefined ? false : isInvalid}}" ',
                            'ng-attr-aria-describedby="{{::describedBy ? describedBy : undefined}}" ',
                            'ng-checked="{{!!option.selected}}" ',
                            'value="{{::option.value}}" ',
                            'ng-click="optionClicked($event, option)">',
                        '<span translate="{{::option.label}}"></span>',
                    '</span>',
                    '<p class="help-block" ng-if="option.help || option.helpHeader">',
                        '<strong translate="{{::option.helpHeader}}" ng-if="option.helpHeader"></strong>',
                        '<span ng-if="option.help"><br ng-if="option.helpHeader && option.help"/><span translate="{{option.help}}"></span></span>',
                    '</p>',
                '</label>'

            ].join('')

        };

    });


    angular.module('CCC.Components').directive('cccDetailedSelectMulti', function () {

        return {

            restrict: 'E',

            scope: {
                selectId: "=",
                options: "=",
                isDisabled: "=?",
                describedBy: "@?",
                isInvalid: '=?'
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',

                function ($scope, $element, $timeout) {

                    /*============ PRIVATE VARIABLES/METHODS ===========*/

                    /*============ MODEL ============*/


                    /*============ BEHAVIOR ============*/

                    $scope.optionClicked = function (event, option) {

                        if ($scope.isDisabled) {
                            event.preventDefault();
                            event.stopPropagation();
                            return false;
                        }

                        option.selected = !option.selected;

                        $scope.$emit('ccc-detailed-select-multi.changed');
                    };


                    /*============ INITIALIZATION ============*/
                }
            ],

            template: [

                '<label class="form-yes-no-section" ng-repeat="option in options track by option.id" ng-class="{\'active\':option.selected, \'yes\': option.type === \'YES\', \'no\': option.type === \'NO\'}">',
                    '<span ng-if="option.type === \'YES\'" class="icon fa fa-check-square-o" role="presentation" aria-hidden="true"></span>',
                    '<span ng-if="option.type === \'NO\'" class="icon fa fa-times-rectangle-o" role="presentation" aria-hidden="true"></span>',
                    '<span class="checkbox">',
                        '<input type="checkbox" ',
                            'id="ccc-detailed-select-option-{{::selectId + \'-\' + option.id}}" ',
                            'name="ccc-detailed-select-{{::selectId}}" ',
                            'ng-disabled="isDisabled" ',
                            'aria-invalid="{{isInvalid === undefined ? false : isInvalid}}" ',
                            'ng-attr-aria-describedby="{{::describedBy ? describedBy : undefined}}" ',
                            'ng-checked="{{!!option.selected}}" ',
                            'value="{{::option.value}}" ',
                            'ng-click="optionClicked($event, option)">',
                        '<span translate="{{::option.label}}"></span>',
                    '</span>',
                    '<p class="help-block" ng-if="option.help || option.helpHeader">',
                        '<strong translate="{{::option.helpHeader}}" ng-if="option.helpHeader"></strong>',
                        '<span ng-if="option.help"><br ng-if="option.helpHeader && option.help"/><span translate="{{option.help}}"></span></span>',
                    '</p>',
                '</label>'

            ].join('')

        };

    });

})();
