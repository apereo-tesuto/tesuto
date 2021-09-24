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

    angular.module('CCC.Components').directive('cccInputDate', [

        '$parse',
        'Moment',
        'DateService',
        '$timeout',

        function ($parse, Moment, DateService, $timeout) {

            return {

                restrict: 'A',

                // we expect this model should be a Moment date object, if it's not, it will end up one anyway :-)
                require: '?ngModel',

                link: function (scope, element, attr, ngModelCtrl) {

                    if (!ngModelCtrl) {
                        return;
                    }

                    // convert model value to viewValue
                    ngModelCtrl.$formatters.push(function (modelValue) {
                        if (modelValue) {

                            var dateMoment = new Moment(modelValue);
                            if (dateMoment.isValid()) {
                                return dateMoment.format('M/D/YYYY');
                            } else {
                                return '';
                            }

                        } else {
                            return '';
                        }
                    });

                    // convert viewValue to model value
                    ngModelCtrl.$parsers.push(function (viewValue) {

                        var parsedDateString = DateService.parseDateString(viewValue);
                        var newModelValue = new Moment(parsedDateString, 'MM/DD/YYYY');

                        // now verify that Moment thinks it is valid
                        if (newModelValue.isValid()) {

                            return newModelValue;

                        // if it's not valid by Moment's standards then it's null and we'll get an error
                        } else {
                            return null;
                        }
                    });

                    ngModelCtrl.$validators.cccInputDateInvalid = function (modelValue, viewValue) {

                        var parsedDateString = DateService.parseDateString(viewValue);
                        var newModelValue = new Moment(parsedDateString, 'MM/DD/YYYY');

                        // now verify that Moment thinks it is valid
                        return newModelValue.isValid();
                    };

                    var triggerUpdate = function () {
                        if (ngModelCtrl.$modelValue) {
                            ngModelCtrl.$setViewValue(new Moment(ngModelCtrl.$modelValue).format('M/D/YYYY'));
                            ngModelCtrl.$commitViewValue();
                            ngModelCtrl.$render();
                        }
                    };

                    $(element).blur(triggerUpdate);

                    ngModelCtrl.$validate();
                }
            };
        }
    ]);

})();
