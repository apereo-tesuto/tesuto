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

    angular.module('CCC.Components').directive('cccDropdownFocus', [

        function () {

            return {

                restrict: 'A',

                link: function (scope, element, attr) {

                    $(element).on('focusout', function() {
                        var that = $(this);

                        setTimeout(function() {
                            var hasFocus = (that.find(':focus').length > 0);

                            if (!hasFocus) {

                                if ($(element).hasClass('open')) {

                                    $(element).removeClass('open');
                                    $(element).children('[data-toggle=dropdown]:first').attr('aria-expanded', 'false');
                                }
                            }
                        }, 500);
                    });

                }
            };
        }
    ]);

})();
