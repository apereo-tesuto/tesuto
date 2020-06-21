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

    angular.module('CCC.Components').directive('cccClearableInput', function () {

        return {

            restrict: 'A',

            scope: {
                cccClearableInput: '='
            },

            controller: [

                '$scope',
                '$element',
                '$compile',

                function ($scope, $element, $compile) {

                    /*============ PRIVATE VARIABLES ============*/

                    /*============ MODEL ============*/

                    /*============ LISTENERS ============*/

                    /*============ BEHAVIOR ============*/

                    $scope.clearInput = function () {
                        $scope.cccClearableInput = '';
                    };


                    /*============ INITIALIZATION ============*/

                    var inputWrap = $('<span class="ccc-clearable-input-wrap"></span>');

                    $($element).wrap(inputWrap);

                    var clearButton = $('<a class="ccc-clearable-input-button" href="#" ng-click="clearInput()"><i class="fa fa-times-circle"></i><span class="sr-only">clear input</span></a>');
                    clearButton.appendTo($element.parent());

                    $compile(clearButton)($scope);
                }
            ]

        };

    });

})();






