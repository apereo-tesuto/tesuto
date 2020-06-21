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

    angular.module('CCC.Components').directive('cccValidationBadge', function () {

        return {

            restrict: 'A',

            controller: [

                '$scope',
                '$element',
                '$attrs',
                '$compile',

                function ($scope, $element, $attrs, $compile) {

                    /*============ PRIVATE VARIABLES ============*/

                    var badgeScope = $scope.$new();

                    // did the original input have the twitter boostrap input-lg class? if so we need to adjust styling accordingly
                    var badgeClass = $($element).hasClass('input-lg') ? 'ccc-validation-badge-lg' : '';

                    var formRef = false;


                    /*============ MODEL ============*/

                    badgeScope.errorCount = 0;


                    /*============ LISTENERS ============*/

                    $scope.$watch(function () {

                        var totalErrors = 0;

                        if (!formRef) {

                            // do this to allow . notation for nested forms on scope (treat as expression)
                            try {

                                var keyList = $attrs.cccValidationBadge.split('.');
                                var currentModelRef = $scope;

                                _.each(keyList, function (key) {
                                    currentModelRef = currentModelRef[key];
                                });

                                formRef = currentModelRef;

                            } catch (e) {

                            }
                        }

                        // we wrap in a try catch because of timing issues related to forms and inputs being rendered
                        try {

                            var errors = formRef[$attrs['name']].$error;

                            for (var key in errors) {
                                if (errors.hasOwnProperty(key)) {
                                    totalErrors++;
                                }
                            }

                        } catch (e) {}

                        return totalErrors;

                    }, function (totalErrors) {

                        badgeScope.errorCount = totalErrors;

                    });


                    /*============ INITIALIZATION ============*/

                    var inputWrap = $('<span class="ccc-validation-badge-wrap ' + badgeClass + '"></span>');

                    if ($attrs.cccValidationBadgeStyle === 'fullWidth') {
                        inputWrap.addClass('ccc-validation-badge-wrap-full-width');
                    }
                    if ($attrs.cccValidationBadgeStyle === 'fullWidthSelect') {
                        inputWrap.addClass('ccc-validation-badge-wrap-full-width-select');
                    }
                    if ($attrs.cccValidationBadgeStyle === 'dropdown') {
                        inputWrap.addClass('ccc-validation-badge-wrap-dropdown');
                    }

                    $($element).wrap(inputWrap);

                    var badge = $('<span class="ccc-validation-badge" ng-hide="errorCount < 1"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i></span>');
                    badge.appendTo($element.parent());

                    $compile(badge)(badgeScope);

                }
            ]

        };

    });

})();






