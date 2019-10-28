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

    angular.module('CCC.Components').directive('cccDatePicker', function () {


        /*============ PRIVATE STATIC VARIABLES / METHODS ============*/

        var datePickerUid = 0;
        var getDatePickerUid = function () {
            return datePickerUid++;
        };


        /*============ DIRECTIVE DEFINITION ============*/

        return {

            restrict: 'E',

            scope: {
                date: '=',
                minDate: '=?',
                minErrorText: '@?',     // because the minimum / max date may need some explanation that this component shouldn't know about... allow error text to be passed in
                maxDate: '=?',
                maxErrorText: '@?',
                isDisabled: '=',
                label: '@?',
                labelledBy: '@?',
                name: '@?'
            },

            controller: [

                '$scope',
                '$element',
                '$attrs',
                '$timeout',
                'Moment',
                'OverlayService',


                function ($scope, $element, $attrs, $timeout, Moment, OverlayService) {


                    /*============ PRIVATE VARIABLES AND METHODS =============*/

                    // transfer the root level id to the input for accessibility
                    var transferIdAttribute = function () {

                        if ($attrs['id']) {
                            var idValue = $attrs['id'];
                            $($element).removeAttr('id');
                            $element.find('input').attr('id', idValue);
                        }
                    };


                    /*============ MODEL =============*/

                    $scope.formName = $scope.name ? $scope.name : 'cccDatePickerForm' + getDatePickerUid();

                    $scope.min = $scope.min || false;
                    $scope.max = $scope.max || false;

                    $scope.minErrorText = $scope.minErrorText || 'CCC_COMP.CCC_DATE_PICKER.ERROR.MIN_DATE';
                    $scope.maxErrorText = $scope.maxErrorText || 'CCC_COMP.CCC_DATE_PICKER.ERROR.MAX_DATE';


                    /*============ MODEL DEPENDENT METHODS =============*/


                    /*============ BEHAVIOR =============*/

                    $scope.open = function () {

                        var overlayScope = $scope.$new();

                        var calendarOverlay = OverlayService.open({
                            width: 254,
                            height: 300,
                            position: 'top',
                            scope: overlayScope,
                            firstFocus: 'table',    // the table element needs focus for keyboard handling to work properly
                            target: $element.find('.input-group'),
                            positionTarget: $element.find('.input-group-btn button'),
                            template: '<ccc-overlay-calendar overlay="overlay" date="date"></ccc-overlay-calendar>'
                        });

                        // NOTE: the date picker will always return start of the selected day relative to the user's browser
                        calendarOverlay.result.then(function (newDate) {
                            // then sent to start of that day
                            $scope.date = Moment(newDate).startOf('day');
                            $scope[$scope.formName].date.$setDirty();
                        });
                    };

                    $scope.getCurrentDate = function () {
                        if ($scope[$scope.formName] && $scope[$scope.formName].date.$modelValue) {
                            return new Moment($scope[$scope.formName].date.$modelValue).format('ddd, MMM D YYYY');
                        }
                    };


                    /*============ LISTENERS =============*/

                    /*============ INITIALIZATION =============*/

                    // it's easier to reference a non dynamic form in template
                    $timeout(function () {
                        $scope.resolvedForm = $scope[$scope.formName];
                    },1);

                    transferIdAttribute();
                }
            ],

            template: [

                // we wrap this in an ng-form in case you are embedding this into a form and want the parent form to be dirty/invalid based on this input
                '<div class="input-group" ng-form="{{formName}}">',

                    '<input type="text" class="form-control" name="date" autocomplete="off" ',
                        'ng-model="date" ',
                        'ng-model-options="{allowInvalid: true}" ',
                        'ccc-input-date ',
                        'ccc-min-date="minDate" ',
                        'ccc-max-date="maxDate" ',
                        'ccc-validation-badge="resolvedForm" ',
                        'ccc-validation-badge-style="fullWidth" ',
                        'ng-disabled="isDisabled" ',
                        'placeholder="Enter a Date" ',
                        'ng-attr-aria-label="{{label}}" ',
                        'ng-attr-aria-labelledby="{{labelledBy}}" ',
                        'aria-describedby="date-picker-errors-{{formName}}" ',
                    '/>',

                    '<div class="ccc-date-picker-date-preview datePickerErrors{{formName}}">',
                        '<div class="noanim" ng-show="!resolvedForm.date.$invalid">{{getCurrentDate()}}</div>',
                        '<div id="date-picker-errors-{{formName}}" class="ccc-validation-messages noanim ccc-validation-messages-date-picker" ng-messages="resolvedForm.date.$error">',
                            '<p ng-message="cccInputDateInvalid" class="noanim"><span translate="CCC_COMP.CCC_DATE_PICKER.ERROR.INVALID"></span></p>',
                            '<p ng-message="cccMinDate" class="noanim"><span translate="{{minErrorText}}"></span></p>',
                            '<p ng-message="cccMaxDate" class="noanim"><span translate="{{maxErrorText}}"></span></p>',
                        '</div>',
                    '</div>',

                    '<span class="input-group-btn">',
                        '<button type="button" class="btn btn-default btn-icon-only" ng-click="open($event)" ng-disabled="isDisabled"><span class="sr-only">open calendar picker</span><i class="glyphicon glyphicon-calendar"></i></button>',
                    '</span>',

                '</div>'

            ].join('')

        };

    });

})();


