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

    angular.module('CCC.View.Home').directive('cccUserTemporaryAccountSelector', function () {

        return {

            restrict: 'E',

            scope: {
                user: '=',
                isTemporary: '=',
                isDisabled: '=?'
            },

            controller: [

                '$scope',
                'Moment',

                function ($scope, Moment) {

                    /*============ PRIVATE VARIABLES AND METHODS ==============*/


                    /*============ MODEL ==============*/

                    $scope.user.startDate = $scope.user.startDate || new Moment();


                    /*============ MODEL DEPENDENT METHODS ============*/

                    /*============ BEHAVIOR ==============*/

                    /*============ LISTENERS ==============*/

                    /*============ INITIALIZATION ==============*/

                }
            ],

            template: [

                '<label class="ccc-user-temporary-account-checkbox"><input type="checkbox" ng-model="isTemporary" ng-disabled="isDisabled"/> This is a temporary account</label>',

                '<div ng-if="isTemporary" class="row ccc-user-temporary-account-selector-dates">',

                    '<div class="col-md-6">',
                        '<label id="ccc-user-temporary-account-selector-start-date-label"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> Start Date </label>',
                        '<ccc-date-picker is-disabled="isDisabled" date="user.startDate" id="ccc-user-temporary-account-selector-start-date-input" label="start date"></ccc-date-picker>',
                    '</div>',

                    '<div class="col-md-6">',
                        '<label id="ccc-user-temporary-account-selector-end-date-label"><i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> End Date </label>',
                        '<ccc-date-picker is-disabled="isDisabled" date="user.endDate" id="ccc-user-temporary-account-selector-end-date-input" label="end date" min-date="user.startDate" min-error-text="CCC_VIEW_HOME.TEMPORARY_ACCOUNT_SELECTOR.MIN_END_DATE"></ccc-date-picker>',
                    '</div>',

                '</div>'

            ].join('')

        };

    });

})();
