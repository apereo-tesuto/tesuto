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

    angular.module('CCC.View.Home').directive('cccUserLoginDetailsForm', function () {

        return {

            restrict: 'E',

            scope: {
                user: '=',
                submitted: '=',
                loading: '='
            },

            controller: [

                '$scope',
                'CCC_REGEX_STORE',

                function ($scope, CCC_REGEX_STORE) {

                    /*============ MODEL ============*/

                    $scope.isHelpTextVisible = false;
                    $scope.usernameRegex = CCC_REGEX_STORE.SYSTEM_USER;


                    /*============ BEHAVIOR ============*/

                    $scope.toggleHelpText = function () {
                        $scope.isHelpTextVisible = !$scope.isHelpTextVisible;
                    };


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-colleges-list.collegeSelected', function (e, selectedCollege, firstLoad) {

                        $scope.user.primaryCollegeId = selectedCollege.cccId;

                        if (!firstLoad) {
                            $scope.userLoginDetailsForm.$setDirty();
                        }
                    });
                }
            ],

            template: [

                '<div class="ng-form" name="userLoginDetailsForm">',

                    '<div class="row">',

                        '<div class="col-sm-6">',
                            '<div class="form-group">',

                                '<label for="username">',
                                    '<i class="ccc-form-required fa fa-asterisk" aria-hidden="true"></i> ',
                                    'User Account Id ',
                                    '<a ng-click="toggleHelpText()" href="#">',
                                        '<i class="fa fa-question-circle" aria-hidden="true"></i>',
                                        '<span class="sr-only">show help text</span>',
                                    '</a>',
                                '</label>',
                                '<div ccc-show-errors="userLoginDetailsForm.username.$dirty || submitted">',
                                    '<input type="text" id="username" class="form-control" name="username" placeholder="User Account Id" ',
                                        'required ',
                                        'ng-maxlength="120" ',
                                        'ng-pattern="usernameRegex" ',
                                        'autocomplete="off" ',
                                        'ng-model-options="{ debounce: 100 }" ',
                                        'ng-disabled="loading" ',
                                        'ng-model="user.username" ',
                                        'ccc-validation-badge="userLoginDetailsForm" ',
                                        'ccc-validation-badge-style="fullWidth" ',
                                        'aria-describedby="cccUserAccountIdErrors" ',
                                    '> ',
                                    '<div id="cccUserAccountIdErrors" class="ccc-validation-messages noanim ccc-validation-messages-user-name" ng-messages="userLoginDetailsForm.username.$error">',
                                        '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.ERROR.USER_ACCOUNT_ID_REQUIRED"></span></p>',
                                        '<p ng-message="maxlength" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.ERROR.USER_ACCOUNT_ID_MAX"></span></p>',
                                        '<p ng-message="pattern" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.ERROR.USER_ACCOUNT_ID_PATTERN"></span></p>',
                                    '</div>',
                                '</div>',

                            '</div>',
                        '</div>',

                        '<div class="col-sm-6">',
                            '<div class="form-group ccc-user-login-details-college-dropdown">',

                                '<div ccc-show-errors="userLoginDetailsForm.primaryCollegeId.$dirty || submitted">',
                                    '<span class="at"> at </span>',
                                    '<ccc-colleges-dropdown id="primaryCollegeId" name="primaryCollegeId" ',
                                        'ng-model="user.primaryCollegeId" ',
                                        'ng-required="true" ',
                                        'ccc-validation-badge="userLoginDetailsForm" ',
                                        'ccc-validation-badge-style="dropdown" ',
                                        'selected-ccc-id="user.primaryCollegeId" ',
                                        '>',
                                    '</ccc-colleges-dropdown>',

                                    '<div id="cccPrimaryCollegeIdErrors" class="ccc-validation-messages noanim ccc-validation-messages-primary-college-id" ng-messages="userLoginDetailsForm.primaryCollegeId.$error">',
                                        '<p ng-message="required" class="noanim"><span translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.ERROR.PRIMARY_COLLEGE"></span></p>',
                                    '</div>',
                                '</div>',
                            '</div>',
                        '</div>',

                    '</div>',

                    '<div class="row">',
                        '<div class="col-sm-12">',
                            '<div ng-show="isHelpTextVisible" class="user-account-id-help-text help-tip-box">',
                                '<a ng-click="toggleHelpText()" href="#">',
                                    '<i class="fa fa-times close-help-text" aria-hidden="true"></i>',
                                    '<span class="sr-only">close help text</span>',
                                '</a>',
                                '<p translate="CCC_VIEW_HOME.WORKFLOW.CREATE_USER.USER_ACCOUNT_ID_HELP"></p>',
                            '</div>',
                        '</div>',
                    '</div>',

                '</div>'

            ].join('')

        };

    });

})();
