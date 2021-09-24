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

    angular.module('CCC.Components').directive('cccOverlayCalendar', function () {

        return {

            restrict: 'E',

            scope: {
                overlay: "=",
                date: '=',
                dateOptions: '='    // NOTE: these are not dynamic, they will be taken once when the controller wires up -> https://angular-ui.github.io/bootstrap/#/datepicker
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',
                'Moment',

                function ($scope, $element, $timeout, Moment) {

                    /*============ PRIVATE VARIABLES / METHODS ===========*/

                    var dateOptionDefaults = {

                        dateDisabled: function (data) {
                            return false;
                        },

                        maxDate: false,
                        minDate: false,
                        showWeeks: false
                    };


                    /*============ MODEL ============*/

                    // genderate a list of date options
                    $scope.finalDateOptions = $.extend(dateOptionDefaults, $scope.dateOptions);


                    /*============ BEHAVIOR ============*/


                    /*============ LISTENERS ============*/

                    $scope.$watch('date', function (newValue, oldValue) {
                        if (oldValue !== newValue) {
                            $scope.overlay.deferred.resolve(newValue);
                        }
                    });


                    /*============ INITIALIZATION ============*/

                    $timeout(function () {
                        $element.find('table').attr('tabindex','0').focus();
                    }, 1);
                }
            ],

            template: [
                '<div uib-datepicker ng-model="date" datepicker-options="finalDateOptions"></div>'
            ].join('')

        };

    });

})();
