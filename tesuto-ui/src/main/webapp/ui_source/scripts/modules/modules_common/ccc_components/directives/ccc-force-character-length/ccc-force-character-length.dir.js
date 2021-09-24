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

    angular.module('CCC.Components').directive('cccForceCharacterLength', [function () {

        return {
            restrict: 'A',
            require: '?ngModel',

            link: function (scope, elm, attrs, ctrl) {
                if (!ctrl) {
                    return;
                }

                scope.$watch(function () {

                    return ctrl.$viewValue;

                },function (newViewValue) {

                    if (newViewValue.length > attrs.cccForceCharacterLength) {
                        var truncatedViewValue = ctrl.$viewValue.substring(0, attrs.cccForceCharacterLength);
                        ctrl.$setViewValue(truncatedViewValue);
                        ctrl.$render();
                    }
                });
            }
        };
    }]);

})();
