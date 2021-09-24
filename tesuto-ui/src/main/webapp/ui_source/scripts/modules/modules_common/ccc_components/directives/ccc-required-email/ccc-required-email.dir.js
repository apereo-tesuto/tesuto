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

    angular.module('CCC.Components').directive('cccRequiredEmail', [

        'CCC_REGEX_STORE',

        function (CCC_REGEX_STORE) {

        return {
            restrict: 'A',
            require: '?ngModel',

            link: function (scope, elm, attrs, ctrl) {
                if (!ctrl) {
                    return;
                }

                ctrl.$validators.requiredEmail = function (viewValue, modelValue) {

                    if (attrs.cccRequiredEmail === 'allowEmpty' && $.trim(viewValue) === "") {
                        return true;
                    }

                    // Modified official html5 regex for email (see https://www.w3.org/TR/html5/forms.html#valid-e-mail-address)
                    // we do require a TLD, so local domains (test@local) are not valid.
                    var pattern = CCC_REGEX_STORE.EMAIL;
                    return pattern.test(modelValue);
                };

                scope.$watch(function () {
                    return ctrl.$viewValue;
                }, function () {
                    ctrl.$validate();
                });
            }
        };
    }]);

})();
