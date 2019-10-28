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

    // replication of click logic without using ng-aria to avoid Jaws not reading MathML content
    // https://github.com/angular/angular.js/blob/291d7c467fba51a9cb89cbeee62202d51fe64b09/src/ng/directive/ngEventDirs.js
    angular.module('CCC.Assessment').directive('cccMathmlClick', ['$parse', '$rootScope', function ($parse, $rootScope) {

        return {

            restrict: 'A',

            compile: function ($element, attr) {

                var fn = $parse(attr['cccMathmlClick'], /* interceptorFn */ null, /* expensiveChecks */ true);

                // return the link function
                return function (scope, element) {

                    element.on('click', function (event) {
                        var callback = function () {
                            event.preventDefault();
                            fn(scope, {$event: event});
                        };
                        scope.$apply(callback);
                    });
                    element.on('keyup', function (event) {
                        if (event.which === 13) {
                            var callback = function () {
                                fn(scope, {$event: event});
                            };
                            scope.$apply(callback);
                        }
                    });
                };
            }
        };

    }]);

})();
