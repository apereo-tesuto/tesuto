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

    angular.module('CCC.Components').directive('cccFocusable', [function () {

        return {

            restrict: 'A',

            compile: function ($element) {

                var currentTabIndex = $element.attr('tabindex');

                if (currentTabIndex === undefined) {
                    $element.attr('tabindex', '0');
                }

                return function (scope, element) {

                    var elementSelected = function () {
                        scope.$emit('ccc-focusable.clicked', element);
                        scope.$apply();
                    };

                    element.on('click keypress', function (event) {

                        if (event.type === 'click') {
                            elementSelected();

                        } else {

                            var code = event.charCode || event.keyCode;

                            // Listen for spacebar (32) or enter (13)
                            if (code === 32 || code === 13) {

                                // Prevent spacebar default scroll behavior
                                event.preventDefault();
                                elementSelected();
                            }
                        }
                    });
                };
            }
        };
    }]);

})();
