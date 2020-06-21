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

    angular.module('CCC.View.Home').directive('cccActivationStatusAndDateFilter', function () {

        return {

            restrict: 'E',

            scope: {
                filterData: '=' // {status: 'started' or 'created', dateWindowType: 'today' or 'lastSeven' or 'custom', minDate: <minDate>, maxDate: <maxDate>}
            },

            controller: [

                '$scope',
                '$translate',
                'Moment',

                function ($scope, $translate, Moment) {

                    /*============= PRIVATE VARIABLES AND METHODS =============*/


                    /*============= MODEL =============*/

                    $scope.statusOptions = [
                        {
                            id: 'started',
                            label: 'CCC_VIEW_HOME.CCC-ACTIVATION-STATUS-AND-DATE-FILTER.STATUS_STARTED'
                        },
                        {
                            id: 'created',
                            label: 'CCC_VIEW_HOME.CCC-ACTIVATION-STATUS-AND-DATE-FILTER.STATUS_CREATED'
                        }
                    ];

                    $scope.dateOptions = [
                        {
                            id: 'today',
                            label: 'CCC_VIEW_HOME.CCC-ACTIVATION-STATUS-AND-DATE-FILTER.DATES_TODAY'
                        },
                        {
                            id: 'lastSeven',
                            label: 'CCC_VIEW_HOME.CCC-ACTIVATION-STATUS-AND-DATE-FILTER.DATES_LAST_SEVEN'
                        },
                        {
                            id: 'custom',
                            label: 'CCC_VIEW_HOME.CCC-ACTIVATION-STATUS-AND-DATE-FILTER.DATES_CUSTOM'
                        }
                    ];

                    $scope.hasDateInvalidError = false;
                    $scope.hasDateOverlapError = false;

                    $scope.datePickerDates = {
                        minDate: false,
                        maxDate: false
                    };

                    // TODO, come up with a better way to attach the form in the template to scope (due to it being within an ng-if)
                    $scope.forms = {};


                    /*============= MODEL DEPENDENT METHODS =============*/

                    var setDatesBasedOnDateWindowType = function () {

                        // these date filters must be in UTC but relative to the browsers current time
                        if ($scope.filterData.dateWindowType === 'today') {

                            $scope.filterData.minDate = Moment().startOf('day');
                            $scope.filterData.maxDate = Moment().endOf('day');

                        } else if ($scope.filterData.dateWindowType === 'lastSeven') {

                            $scope.filterData.minDate = Moment().subtract(7, 'days').startOf('day');
                            $scope.filterData.maxDate = Moment().endOf('day');

                        } else if ($scope.filterData.dateWindowType === 'custom') {

                            $scope.filterData.minDate = $scope.filterData.minDate || Moment().startOf('day');
                            $scope.filterData.maxDate = $scope.filterData.maxDate || Moment().endOf('day');

                            $scope.datePickerDates.minDate = Moment($scope.filterData.minDate);
                            $scope.datePickerDates.maxDate = Moment($scope.filterData.maxDate);

                        } else {

                            $scope.filterData.dateWindowType = 'today';
                            setDatesBasedOnDateWindowType();
                        }
                    };

                    var setSelectedStatus = function (status) {
                        $scope.selectedStatus = status;
                        $scope.filterData.status = status.id;
                    };

                    var setSelectedDate = function (dateOption) {

                        $scope.selectedDate = dateOption;
                        $scope.filterData.dateWindowType = dateOption.id;

                        setDatesBasedOnDateWindowType();
                    };

                    var setInitialStatus = function () {

                        $scope.filterData.status = $scope.filterData.status || 'started';
                        var selectedStatusObject = _.find($scope.statusOptions, function (statusOption) {
                            return statusOption.id === $scope.filterData.status;
                        });

                        setSelectedStatus(selectedStatusObject);
                    };

                    var setInitialDate = function () {

                        $scope.filterData.dateWindowType = $scope.filterData.dateWindowType || 'today';
                        var selectedDateObject = _.find($scope.dateOptions, function (dateOption) {
                            return dateOption.id === $scope.filterData.dateWindowType;
                        });

                        setSelectedDate(selectedDateObject);
                    };

                    var initialize = function () {
                        setInitialStatus();
                        setInitialDate();
                    };

                    var notifyFilterChanged = function () {
                        $scope.$emit('ccc-activation-status-and-date-filter.changed', $scope.filterData);
                    };
                    var debounced_notifyFilterChanged = _.debounce(notifyFilterChanged, 1);

                    var customDateFilterChanged = function () {

                        $scope.hasDateInvalidError = false;
                        $scope.hasDateOverlapError = false;

                        if (!$scope.datePickerDates.minDate || !$scope.datePickerDates.maxDate) {
                            $scope.hasDateInvalidError = true;
                            return;
                        }

                        // just create copies, we assume that they are coming in as UTC Dates
                        var minDate = Moment($scope.datePickerDates.minDate);
                        var maxDate = Moment($scope.datePickerDates.maxDate).endOf('day');

                        if (minDate.isValid() && maxDate.isValid()) {

                            if ($scope.datePickerDates.minDate.valueOf() > $scope.datePickerDates.maxDate.valueOf()) {
                                $scope.hasDateOverlapError = true;
                            }

                        } else {
                            $scope.hasDateInvalidError = true;
                        }
                    };


                    /*============= BEHAVIOR =============*/

                    $scope.setSelectedStatus = setSelectedStatus;

                    $scope.setSelectedDate = setSelectedDate;

                    $scope.applyCustomDates = function () {

                        $scope.filterData.minDate = Moment($scope.datePickerDates.minDate); // the datepicker date is already the start of this day
                        $scope.filterData.maxDate = Moment($scope.datePickerDates.maxDate).endOf('day'); // but we need to change the datepicker start of day to end of day

                        debounced_notifyFilterChanged();

                        $scope.forms['cccCustomDateForm'].$setPristine();
                    };

                    $scope.applyCustomDatesButtonClass = function () {
                        return {
                            'btn-primary': !($scope.hasDateOverlapError || $scope.hasDateInvalidError) && $scope.forms.cccCustomDateForm.$dirty,
                            'btn-default': $scope.hasDateOverlapError || $scope.hasDateInvalidError || !$scope.forms.cccCustomDateForm.$dirty
                        };
                    };


                    /*============= LISTENERS =============*/

                    $scope.$watch('filterData.status', debounced_notifyFilterChanged);
                    $scope.$watch('filterData.minDate', debounced_notifyFilterChanged);
                    $scope.$watch('filterData.maxDate', debounced_notifyFilterChanged);

                    $scope.$watch('datePickerDates.minDate', customDateFilterChanged);
                    $scope.$watch('datePickerDates.maxDate', customDateFilterChanged);


                    /*============ INITIALIZATION ============*/

                    initialize();
                }
            ],

            template: [

                '<div class="filter-dropdowns">',

                    '<div class="row row-thin">',

                        '<div class="col-md-6 col-thin">',

                            '<div ccc-dropdown-focus class="btn-group btn-full-width">',
                                '<button ng-disabled="loading" type="button" class="btn btn-primary btn-full-width btn-full-width-when-small dropdown-toggle" ng-class="{\'btn-primary\': listStyle === \'primary\'}" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">',
                                    '<span><span translate="{{selectedStatus.label}}"></span> <span class="caret" aria-hidden="true"></span></span>',
                                '</button>',
                                '<ul class="dropdown-menu">',
                                    '<li ng-repeat="status in statusOptions track by status.id" ng-class="{selected: status.id === selectedStatus.id}">',
                                        '<a href="#" ng-click="setSelectedStatus(status)"><span translate="{{status.label}}"></span></a>',
                                    '</li>',
                                '</ul>',
                            '</div>',

                        '</div>',

                        '<div class="col-md-6 col-thin">',

                            '<div ccc-dropdown-focus class="btn-group btn-full-width">',
                                '<button ng-disabled="loading" type="button" class="btn btn-primary btn-full-width btn-full-width-when-small dropdown-toggle" ng-class="{\'btn-primary\': listStyle === \'primary\'}" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">',
                                    '<span><span translate="{{selectedDate.label}}"></span> <span class="caret" aria-hidden="true"></span></span>',
                                '</button>',
                                '<ul class="dropdown-menu">',
                                    '<li ng-repeat="dateOption in dateOptions track by dateOption.id" ng-class="{selected: dateOption.id === selectedDate.id}">',
                                        '<a href="#" ng-click="setSelectedDate(dateOption)"><span translate="{{dateOption.label}}"></span></a>',
                                    '</li>',
                                '</ul>',

                            '</div>',

                        '</div>',

                    '</div>',

                '</div>',

                '<div class="filter-dates well well-thin" ng-if="selectedDate.id === \'custom\'" ccc-show-errors="forms.cccCustomDateForm.$dirty">',

                    '<ng-form name="forms.cccCustomDateForm">',

                        '<div class="filter-date-with-label">',
                            '<label id="ccc-activation-filter-start-date">From</label>',
                            '<ccc-date-picker class="start-date" date="datePickerDates.minDate" labelled-by="ccc-activation-filter-start-date"></ccc-date-picker>',
                        '</div>',

                        '<div class="filter-date-with-label">',
                            '<label id="ccc-activation-filter-end-date">To</label>',
                            '<ccc-date-picker date="datePickerDates.maxDate" labelled-by="ccc-activation-filter-end-date"></ccc-date-picker>',
                        '</div>',

                        '<div class="ccc-validation-messages ccc-validation-messages-standalone">',
                            '<p ng-if="forms.cccCustomDateForm.$dirty && hasDateInvalidError">',
                                '<i class="fa fa-exclamation-triangle color-warning"></i> ',
                                '<span translate="CCC_VIEW_HOME.CCC-ACTIVATION-STATUS-AND-DATE-FILTER.VALIDATION_INVALID"></span>',
                            '</p>',

                            '<p ng-if="forms.cccCustomDateForm.$dirty && hasDateOverlapError">',
                                '<i class="fa fa-exclamation-triangle color-warning"></i> ',
                                '<span translate="CCC_VIEW_HOME.CCC-ACTIVATION-STATUS-AND-DATE-FILTER.VALIDATION_OVERLAP"></span>',
                            '</p>',
                        '</div>',

                        '<button class="btn btn-full-width" ',
                            'ng-class="applyCustomDatesButtonClass()" ',
                            'ng-click="applyCustomDates()" ',
                            'ng-disabled="hasDateOverlapError || hasDateInvalidError || !forms.cccCustomDateForm.$dirty" ',
                            'translate="CCC_VIEW_HOME.CCC-ACTIVATION-STATUS-AND-DATE-FILTER.APPLY_CUSTOM_DATES">',
                        '</button>',

                    '<ng-form>',

                '</div>'

            ].join('')

        };

    });

})();
