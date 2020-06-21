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

    angular.module('CCC.Components').directive('cccMinDate', [

        '$parse',
        'Moment',

        function ($parse, Moment) {

            return {
                restrict: 'A',
                require: '?ngModel',

                link: function (scope, elm, attrs, ctrl) {
                    if (!ctrl) {
                        return;
                    }


                    var parsedMinDate;
                    var minDate;
                    ctrl.$validators.cccMinDate = function (viewValue, modelValue) {

                        parsedMinDate = $parse(attrs['cccMinDate'])(scope);

                        if (parsedMinDate === false || parsedMinDate === undefined) {

                            return true;

                        } else {

                            minDate = new Moment(parsedMinDate);

                            if (minDate.isValid() && viewValue !== false && $.trim(viewValue)) {

                                return ! new Moment(viewValue).isBefore(minDate);

                            } else {
                                return true;
                            }
                        }
                    };

                    scope.$watch(function () {
                        return ctrl.$viewValue;
                    }, function () {
                        ctrl.$validate();
                    });

                    scope.$watch(function () {
                        return $parse(attrs['cccMinDate'])(scope);
                    }, function () {
                        ctrl.$validate();
                    });
                }
            };
        }
    ]);

})();
