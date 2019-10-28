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

    angular.module('CCC.Assessment').directive('cccToggleInput', function () {

        return {

            restrict: 'E',

            scope: {
                toggleValue: '=',       // the value
                type: '@',              // 'radio' or 'checkbox' NOTE: only coding for checkbox right now
                data: '=',              // data associated with this radio/checkbox
                inputAriaLabel: '=',    // a string of ids to use for aria-labelledby
                hasError: '=?'          // a flag to show errors
            },

            link: function (scope, element, attrs) {

                /*=========== PRIVATE VARIABLES AND METHODS =============*/

                /*=========== MODEL =============*/

                scope.hasError = scope.hasError || false;


                /*=========== MODEL DEPENDENT METHODS =============*/


                /*=========== BEHAVIOR =============*/

                scope.toggleButton = function (event) {
                    event.stopPropagation();
                    event.preventDefault();
                    scope.toggleValue = !scope.toggleValue;
                };


                /*=========== LISTENERS =============*/

                scope.$watch('toggleValue', function () {
                    scope.$emit('ccc-toggle-input.changed', scope.toggleValue, scope.data);
                });


                /*=========== INITIALIZATION =============*/

                // propagate id down for accessibility
                if (attrs.id) {
                    element.removeAttr('id');
                    element.find('button').attr('id', attrs.id);
                }

            },

            template: [
                '<button role="checkbox" ng-click="toggleButton($event)" class="ccc-fg" ng-class="{\'ccc-toggle-input-checked\': toggleValue, \'ccc-toggle-input-error\': hasError}" aria-invalid="{{hasError}}" aria-checked="{{toggleValue}}" aria-label="{{inputAriaLabel}}">',
                    '<i class="fa" ng-class="{\'fa-times\': hasError, \'fa-check\': !hasError}" aria-hidden="true"></i>',
                '</button>'
            ].join('')

        };

    });

})();

