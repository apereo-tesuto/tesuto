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

    angular.module('CCC.Components').directive('cccInputInvalid', [

        '$parse',

        function ($parse) {

            return {
                restrict: 'A',
                require: '?ngModel',

                link: function (scope, elm, attr, ctrl) {

                    if (!ctrl) {
                        return;
                    }

                    var fn = $parse(attr['cccInputInvalid'], /* interceptorFn */ null, /* expensiveChecks */ true);

                    scope.$watch(function () {
                        ctrl.$setValidity('cccInputInvalid', !fn(scope));
                    });
                }
            };
        }
    ]);

})();
