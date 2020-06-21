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

    // this directive was copied from the AngularJS source for the ng-click directive
    // this is used to provide click behavior without touching things like role or tabindex
    // this is a UI enhancement meant only for mouse users
    // an example can be seen in the match interaction, the entire cell is clickable for mouse user convenience
    // but we don't want the ng-aria module to make it a button and add a tabindex because we want keyboard users
    // to tab directly to the button and not the table cell

    angular.module('CCC.Components').directive('cccClickNoFocus', [

        '$parse',
        '$rootScope',

        function ($parse, $rootScope) {

            return {

                restrict: 'A',

                compile: function ($element, attr) {
                    // We expose the powerful $event object on the scope that provides access to the Window,
                    // etc. that isn't protected by the fast paths in $parse.  We explicitly request better
                    // checks at the cost of speed since event handler expressions are not executed as
                    // frequently as regular change detection.
                    var fn = $parse(attr['cccClickNoFocus'], /* interceptorFn */ null, /* expensiveChecks */ true);

                    $element.attr('tabindex', '-1');

                    return function (scope, element) {
                        element.on('click', function (event) {
                            var callback = function () {
                                fn(scope, {$event: event});
                            };
                            scope.$apply(callback);
                        });
                    };
                }
            };
        }
    ]);

})();








